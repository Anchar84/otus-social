package ru.otus.social.posts.model

data class NewPostEvent(
    val receiverId: Int,
    val post: NewPostDto

)
