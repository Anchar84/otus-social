package ru.otus.social.user.repository

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.reactivestreams.Publisher
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import ru.otus.social.user.model.User
import ru.otus.social.user.model.UserCredentials
import java.time.LocalDate
import java.util.*
import java.util.concurrent.Flow
import java.util.function.Function

@Repository
class UserRepository(
    private val r2dbConnectionFactory: ConnectionFactory
) {

    suspend fun createUser(user: User, passwordHash: String, passwordSalt: String): String {
        val connection = r2dbConnectionFactory.create().awaitSingle()
        try {
//            connection.beginTransaction().awaitFirstOrNull()

            val insertUser = connection.createStatement(
                """
                insert into t_user (first_name, second_name, age, birthdate, biography, city)
                values ($1, $2, $3, $4, $5, $6)
            """.trimIndent()
            ).returnGeneratedValues("id")
            insertUser.bind("$1", user.firstName)
            insertUser.bind("$2", user.secondName)
            insertUser.bind("$3", user.age)
            if (user.birthdate != null)
                insertUser.bind("$4", user.birthdate)
            else
                insertUser.bindNull("$4", LocalDate::class.java)
            insertUser.bind("$5", user.biography)
            insertUser.bind("$6", user.city)

            val rs = insertUser.execute().awaitSingle()
            val userId = rs.map { row -> row.get("id", String::class.java) }.awaitFirst()!!

            val insertUserCredentials = connection.createStatement(
                """
                insert into t_user_credentials (user_id, passwd_hash, passwd_salt)
                values ($1, $2, $3)
            """.trimIndent()
            )
            insertUserCredentials.bind("$1", UUID.fromString(userId))
            insertUserCredentials.bind("$2", passwordHash)
            insertUserCredentials.bind("$3", passwordSalt)
            insertUserCredentials.returnGeneratedValues("user_id")
            insertUserCredentials.execute().awaitSingle().map { row -> row.get("user_id") }.awaitFirstOrNull()

            return userId
        } finally {
            connection.close().awaitFirstOrNull()
        }
    }

    suspend fun getUserById(id: String): User? {
        val connection = r2dbConnectionFactory.create().awaitSingle()
        try {
            val select = connection.createStatement("""
                select first_name, second_name, age, birthdate, biography, city from t_user where id = $1""".trimIndent())
            select.bind("$1", UUID.fromString(id))
            return select.execute().awaitSingle().map { it ->
                User(
                    id = UUID.fromString(id),
                    firstName = it.get("first_name", String::class.java) ?: "",
                    secondName = it.get("second_name", String::class.java) ?: "",
                    age = it.get("age", Integer::class.java)?.toInt() ?: 0,
                    birthdate = it.get("birthdate", LocalDate::class.java),
                    biography = it.get("biography", String::class.java) ?: "",
                    city = it.get("city", String::class.java) ?: ""
                )
            }.awaitFirstOrNull()

        } finally {
            connection.close().awaitFirstOrNull()
        }
    }

    suspend fun getUserSaltAndPasswordHash(id: String): UserCredentials? {
        val connection = r2dbConnectionFactory.create().awaitSingle()
        try {
            val select = connection.createStatement("""
                select passwd_hash, passwd_salt from t_user_credentials where user_id = $1
            """.trimIndent())
            select.bind("$1", UUID.fromString(id))

            return select.execute().awaitSingle().map { it ->
                UserCredentials(
                    userId = id,
                    passwdHash = it.get("passwd_hash", String::class.java) ?: "",
                    passwdSalt = it.get("passwd_salt", String::class.java) ?: ""
                )
            }.awaitFirstOrNull()
        } finally {
            connection.close().awaitFirstOrNull()
        }
    }

    suspend fun searchUsersByNames(firstName: String, secondName: String): List<User> {
        val connection = r2dbConnectionFactory.create().awaitSingle()
        try {
            val select = connection.createStatement("""
                select id, first_name, second_name, age, birthdate, biography, city 
                  from t_user 
                  where first_name ilike $1 and second_name ilike $2""".trimIndent())
            select.bind("$1", "$firstName%")
            select.bind("$2", "$secondName%")


            return select.execute().asFlow().flatMapConcat { result ->
                result.map { it ->
                    User(
                        id = it.get("id", UUID::class.java),
                        firstName = it.get("first_name", String::class.java) ?: "",
                        secondName = it.get("second_name", String::class.java) ?: "",
                        age = it.get("age", Integer::class.java)?.toInt() ?: 0,
                        birthdate = it.get("birthdate", LocalDate::class.java),
                        biography = it.get("biography", String::class.java) ?: "",
                        city = it.get("city", String::class.java) ?: ""
                    )
                }.asFlow()
            }.toList()
        } finally {
            connection.close().awaitFirstOrNull()
        }
    }
}
