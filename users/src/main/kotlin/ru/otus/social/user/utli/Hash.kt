package ru.otus.social.user.utli

import java.security.MessageDigest

object Hash {

    fun hashString(input: String, algorithm: String): String {
        return MessageDigest
            .getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}

fun String.sha256(): String {
    return Hash.hashString(this, "SHA-256")
}
