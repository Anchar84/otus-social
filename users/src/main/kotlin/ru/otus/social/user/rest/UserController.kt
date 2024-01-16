package ru.otus.social.user.rest

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.otus.social.user.model.User
import ru.otus.social.user.rest.dto.*
import ru.otus.social.user.service.UserService
import kotlinx.coroutines.slf4j.MDCContext

@RestController
@RequestMapping("/")
class UserController(
    private val userService: UserService,
) {

    @PostMapping("user/registry", headers = ["X-API-VERSION=1"])
    suspend fun registry(@RequestBody registry: Registration): UserId {
        return coroutineScope {
            withContext(MDCContext()) {
                UserId(
                    userService.registry(
                        User(
                            firstName = registry.firstName,
                            secondName = registry.secondName,
                            age = registry.age,
                            birthdate = registry.birthdate,
                            biography = registry.biography,
                            city = registry.city
                        ),
                        registry.password
                    )
                )
            }
        }
    }

    @GetMapping("user/get/{id}", headers = ["X-API-VERSION=1"])
    suspend fun getUserById(@PathVariable("id") id: Int): ResponseEntity<User?> {
        return coroutineScope {
            withContext(MDCContext()) {
                val user = userService.getUserById(id)
                if (user != null) {
                    ResponseEntity.ok(user)
                } else {
                    ResponseEntity.notFound().build()
                }
            }
        }
    }

    @PostMapping("login", headers = ["X-API-VERSION=1"])
    suspend fun login(@RequestBody login: Login): ResponseEntity<LoginResponse> {
        return coroutineScope {
            withContext(MDCContext()) {
                val user = userService.authUser(login.id, login.password)
                if (user != null) {
                    ResponseEntity.ok(LoginResponse(Util.getToken(user.id!!, user.secondName + " " + user.firstName)))
                } else {
                    ResponseEntity.notFound().build()
                }
            }
        }
    }

    @GetMapping("user/search", headers = ["X-API-VERSION=1"])
    suspend fun search(
        @RequestParam("first_name") firstName: String,
        @RequestParam("last_name") secondName: String,
    ): ResponseEntity<List<SearchUser>> {
        return  coroutineScope {
            withContext(MDCContext()) {
                ResponseEntity.ok(
                    userService.searchUsersByNames(firstName, secondName).map { SearchUser(it.firstName) })
            }
        }
    }

    @PostMapping("user/generate/{count}", headers = ["X-API-VERSION=1"])
    suspend fun generate(@PathVariable("count") count: Int): ResponseEntity<Unit> {
        return coroutineScope {
            withContext(MDCContext()) {
                userService.generateUsers(count)
                ResponseEntity.ok(Unit)
            }
        }
    }
}
