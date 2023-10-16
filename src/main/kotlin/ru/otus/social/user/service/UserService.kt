package ru.otus.social.user.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.otus.social.user.model.User
import ru.otus.social.user.repository.UserRepository
import ru.otus.social.user.utli.sha256
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

@Service
class UserService(
    private val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    suspend fun registry(user: User, password: String): Int {
        val passwordSalt = UUID.randomUUID().toString()
        val passwordHash = (password + passwordSalt).sha256()
        return userRepository.createUser(user, passwordHash, passwordSalt)
    }

    suspend fun getUserById(id: Int): User? = userRepository.getUserById(id)

    suspend fun authUser(userId: Int, password: String): String? {
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

    suspend fun generateUsers(count: Int): Int {
        val counter = AtomicInteger()
        val names = UserService::class.java.getResource("/names.txt").readText().split("\n")
        val rnd = Random()
        try {
            while(counter.get() < count) {
                val name = names[rnd.nextInt(names.size)].split(" ")
                val salt = "test${counter.get()}"
                userRepository.createUser(
                    User(
                        firstName = name[1].trim(),
                        secondName = name[0].trim(),
                        age = 10 + rnd.nextInt(90)
                    ), "test$salt".sha256(), salt
                )
                logger.info("user generated: ${counter.incrementAndGet()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return counter.get()
    }
}
