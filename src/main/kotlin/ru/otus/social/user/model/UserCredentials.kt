package ru.otus.social.user.model

data class UserCredentials(
    val userId: Int? = null,
    val passwdHash: String = "",
    val passwdSalt: String = ""
)
