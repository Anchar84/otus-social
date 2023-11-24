package ru.otus.social.dialogs.http

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

object Util {
    private val ISSUER = "otus-soical-ha"
    private val SECRET = "very-secret-word"
    private val SUBJECT = "ha"

    private val algorithm = Algorithm.HMAC256(SECRET)

    private val verifier = JWT.require(algorithm)
        .withIssuer(ISSUER)
        .build()

    fun getToken(userId: Int, userName: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(SUBJECT)
            .withClaim("userId", userId)
            .withClaim("userName", userName)
            .sign(algorithm);
    }

    fun getUserInfo(token: String): UserInfo {
        val decodedJWT = verifier.verify(token)
        return UserInfo(
            decodedJWT.getClaim("userId").asInt(),
            decodedJWT.getClaim("userName").asString())
    }
}

data class UserInfo (
    val userId: Int,
    val userName: String
)
