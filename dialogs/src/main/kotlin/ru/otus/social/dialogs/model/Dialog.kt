package ru.otus.social.dialogs.model

data class Dialog (
    val id: Int = 0,
    val fromUserId: Int,
    val toUserId: Int,
    val text: String
)
