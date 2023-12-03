package ru.otus.social.websocket.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import ru.otus.social.websocket.service.PostsWSSender

@Configuration
@EnableWebFlux
internal class WebConfig {

    @Bean
    fun handlerMapping(objectMapper: ObjectMapper): HandlerMapping {
        val map: MutableMap<String, WebSocketHandler> = HashMap()
        map["/posts"] = PostsWSSender()
        val mapping = SimpleUrlHandlerMapping()
        mapping.urlMap = map
        return mapping
    }

    @Bean
    fun webSocketHandlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter()
    }

    @Bean
    fun jackson2ObjectMapper(): ObjectMapper {
        val builder: Jackson2ObjectMapperBuilder = Jackson2ObjectMapperBuilder.json()
        builder.indentOutput(true)
        return builder.build()
    }
}
