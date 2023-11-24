package ru.otus.social.dialogs.http.dto

data class DialogMessage(
    val from: Int,
    val to: Int,
    val text: String
)
