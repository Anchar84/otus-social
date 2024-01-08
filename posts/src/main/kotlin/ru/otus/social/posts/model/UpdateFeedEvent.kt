package ru.otus.social.posts.model

data class UpdateFeedEvent(
    val userId: String = "",
    val requestId: String = ""
)
