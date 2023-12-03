package ru.otus.social.websocket.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.otus.social.websocket.config.RabbitConfiguration
import ru.otus.social.websocket.dto.NewPostEvent

@Service
class PostBrokerListener(
    private val rabbitConfiguration: RabbitConfiguration,
    private val postsWSSender: PostsWSSender
) {
    private val logger = LoggerFactory.getLogger(PostBrokerListener::class.java)
    private val queryName = "OTUS_SEND_POSTS_UPDATE"
    private val mapper = ObjectMapper()

    @PostConstruct
    fun registryListeners() {
        rabbitConfiguration.createMessageListenerContainer(queryName) {
            val payload = String(it.body)
            logger.info("received new post $payload")
            GlobalScope.async {
                postsWSSender.sendPost(mapper.readValue(payload, NewPostEvent::class.java))
            }
        }
    }
}
