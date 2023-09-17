package ru.otus.social.user.model

data class UserCredentials(
    val userId: String? = null,
    val passwdHash: String = "",
    val passwdSalt: String = ""
)
