package ru.otus.social.user.model

import java.time.LocalDate
import java.util.*

data class User(
    val id: Int? = null,
    val firstName: String = "",
    val secondName: String = "",
    val age: Int = 0,
    val birthdate: LocalDate? = null,
    val biography: String = "",
    val city: String = ""
)
