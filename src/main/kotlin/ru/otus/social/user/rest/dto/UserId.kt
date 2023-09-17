package ru.otus.social.user.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserId(
    @get:JsonProperty("user_id")
    val userId: String = ""
)
