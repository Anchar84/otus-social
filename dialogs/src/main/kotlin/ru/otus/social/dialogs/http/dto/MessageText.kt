package ru.otus.social.dialogs.http.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageText(
    @field:JsonProperty("text")
    val text: String = ""
)
