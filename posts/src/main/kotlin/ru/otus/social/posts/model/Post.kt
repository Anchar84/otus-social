package ru.otus.social.posts.model

import java.time.LocalDateTime

data class Post(
    val id: Int? = null,
    val authorId: Int = 0,
    val authorName: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val text: String = ""
)
