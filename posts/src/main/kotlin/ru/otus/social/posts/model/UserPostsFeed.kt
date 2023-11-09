package ru.otus.social.posts.model

import java.time.LocalDateTime

data class UserPostsFeed(
    val userId: Int = 0,
    val posts: List<Post> = emptyList(),
    val createAt: LocalDateTime = LocalDateTime.MIN
)
