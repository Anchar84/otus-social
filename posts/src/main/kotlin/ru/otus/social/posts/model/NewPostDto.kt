package ru.otus.social.posts.model

import com.fasterxml.jackson.annotation.JsonProperty

data class NewPostDto(
    val id: Int,
    @get:JsonProperty("author_user_id")
    val authorUserId: Int,
    val text: String
)
