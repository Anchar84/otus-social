package ru.otus.social.dialogs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "tarantool")
data class TarantoolConfigProps (
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val connections: Int = 10,
    val connectTimeout: Int = 1000,
    val requestTimeout: Int = 3000,
    val readTimeout: Int = 1000,
    )
