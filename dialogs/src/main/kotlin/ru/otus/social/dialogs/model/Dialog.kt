package ru.otus.social.dialogs.model

data class Dialog (
    val fromUserId: Int,
    val toUserId: Int,
    val text: String
)
