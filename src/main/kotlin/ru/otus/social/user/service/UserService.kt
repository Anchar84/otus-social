package ru.otus.social.user.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.otus.social.user.model.User
import ru.otus.social.user.repository.UserRepository
import ru.otus.social.user.utli.sha256
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

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

    suspend fun searchUsersByNames(firstName: String, secondName: String): List<User> {
        val result = userRepository.searchUsersByNames(firstName, secondName)
        logger.info("search user [$firstName], [$secondName], found ${result.size} records")
        return result
    }
}
