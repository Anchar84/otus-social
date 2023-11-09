package ru.otus.social.user.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.otus.social.user.model.User
import ru.otus.social.user.rest.dto.*
import ru.otus.social.user.service.UserService


@RestController
@RequestMapping("/")
class UserController(
    private val userService: UserService,
) {

    @PostMapping("user/registry")
    suspend fun registry(@RequestBody registry: Registration): UserId {
        return UserId(
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

    @GetMapping("user/get/{id}")
    suspend fun getUserById(@PathVariable("id") id: Int): ResponseEntity<User?> {
        val user = userService.getUserById(id)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("login")
    suspend fun login(@RequestBody login: Login): ResponseEntity<LoginResponse> {
        val user = userService.authUser(login.id, login.password)
        return if (user != null) {
            ResponseEntity.ok(LoginResponse(Util.getToken(user.id!!, user.secondName + " " + user.firstName)))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("user/search")
    suspend fun search(
        @RequestParam("first_name") firstName: String,
        @RequestParam("last_name") secondName: String,
    ): ResponseEntity<List<SearchUser>> {
        return ResponseEntity.ok(userService.searchUsersByNames(firstName, secondName).map { SearchUser(it.firstName) })
    }

    @PostMapping("user/generate/{count}")
    suspend fun generate(@PathVariable("count") count: Int): ResponseEntity<Unit> {
        userService.generateUsers(count)
        return ResponseEntity.ok(Unit)
    }
}
