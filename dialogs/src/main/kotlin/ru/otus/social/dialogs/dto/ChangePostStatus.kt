package ru.otus.social.dialogs.dto

data class ChangeEntityStatus(
    val entityId: String = "",
    val entityType: String = "",
    val userId: String = "",
    val action: String = "",
    val requestId: String = ""
)
