package ru.otus.social.websocket.dto

data class NewPostEvent(
    val receiverId: Int = 0,
    val post: NewPostDto = NewPostDto(),
    val requestId: String = ""
)
