package ru.otus.social.counter.http.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PostUpdateDto(
    @field:JsonProperty("id")
    val id: Int = 0,
    @field:JsonProperty("text")
    val text: String = ""
)
