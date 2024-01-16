package ru.otus.social.counter.http.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PostGetDto(
    val id: Int,
    @get:JsonProperty("author_user_id")
    val authorUserId: Int,
    val text: String
)
