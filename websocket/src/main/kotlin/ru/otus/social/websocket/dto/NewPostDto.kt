package ru.otus.social.websocket.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NewPostDto(
    val id: Int = 0,
    @get:JsonProperty("author_user_id")
    val authorUserId: Int = 0,
    val text: String = ""
)
