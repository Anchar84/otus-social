package ru.otus.social.counter.dto

data class ChangeEntityStatus(
    val entityId: String = "",
    val entityType: String = "",
    val userId: String = "",
    val action: String = "",
    val requestId: String = ""
)
