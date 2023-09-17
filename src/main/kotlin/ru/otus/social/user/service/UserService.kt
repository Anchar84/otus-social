package ru.otus.social.user.service

import org.springframework.stereotype.Service
import ru.otus.social.user.model.User
import ru.otus.social.user.repository.UserRepository
import ru.otus.social.user.utli.sha256
import java.util.UUID

@Service
class UserService (
    private val userRepository: UserRepository
) {

    suspend fun registry(user: User, password: String): String {
        val passwordSalt = UUID.randomUUID().toString()
        val passwordHash = (password + passwordSalt).sha256()
        return userRepository.createUser(user, passwordHash, passwordSalt)
    }

    suspend fun getUserById(id: String): User? = userRepository.getUserById(id)

    suspend fun authUser(userId: String, password: String): String? {
        val userCredentials = userRepository.getUserSaltAndPasswordHash(userId) ?: return null
        val passwordHash = (password + userCredentials.passwdSalt).sha256()
        return if (passwordHash == userCredentials.passwdHash) {
            UUID.randomUUID().toString()
        } else {
            null
        }
    }
}
