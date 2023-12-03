package ru.otus.social.websocket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import ru.otus.social.websocket.config.DestinationsConfig

@EnableConfigurationProperties(DestinationsConfig::class)
@SpringBootApplication
class WebsocketApplication

fun main(args: Array<String>) {
	runApplication<WebsocketApplication>(*args)
}
