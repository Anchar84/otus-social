package ru.otus.social.user.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.otus.social.user.model.User
import ru.otus.social.user.rest.dto.Login
import ru.otus.social.user.rest.dto.LoginResponse
import ru.otus.social.user.rest.dto.Registration
import ru.otus.social.user.rest.dto.UserId
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
    suspend fun getUserById(@PathVariable("id") id: String): ResponseEntity<User?> {
        val user = userService.getUserById(id)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("login")
    suspend fun login(@RequestBody login: Login): ResponseEntity<LoginResponse> {
        val token = userService.authUser(login.id, login.password)
        return if (token != null) {
            ResponseEntity.ok(LoginResponse(token))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
