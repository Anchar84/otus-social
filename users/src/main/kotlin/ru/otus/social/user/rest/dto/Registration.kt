package ru.otus.social.user.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class Registration(
    @get:JsonProperty("first_name")
    val firstName: String = "",
    @get:JsonProperty("second_name")
    val secondName: String = "",
    @get:JsonProperty("age")
    val age: Int = 0,
    @get:JsonProperty("birthdate")
    val birthdate: LocalDate = LocalDate.now(),
    @get:JsonProperty("biography")
    val biography: String = "",
    @get:JsonProperty("city")
    val city: String = "",
    @get:JsonProperty("password")
    val password: String = ""
)
