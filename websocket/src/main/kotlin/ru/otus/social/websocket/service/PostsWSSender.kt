package ru.otus.social.websocket.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import ru.otus.social.websocket.dto.NewPostDto
import ru.otus.social.websocket.dto.NewPostEvent
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Service
class PostsWSSender : WebSocketHandler {

    companion object {
        private val usersMap = ConcurrentHashMap<Int, WebSocketSession>()
        private val mapper = ObjectMapper()
    }

    suspend fun sendPost(postInfo: NewPostEvent) {
        val session = usersMap[postInfo.receiverId] ?: return
        session.send(
            Mono.fromSupplier {
                session.textMessage(mapper.writeValueAsString(postInfo.post))
            }
        ).awaitFirstOrNull()
    }

    override fun handle(session: WebSocketSession): Mono<Void> {
        val token = session.handshakeInfo.headers["X-Auth"] ?: throw Exception("not authorized")
        if (token.isEmpty()) {
            throw Exception("not authorized")
        }
        val userInfo = Util.getUserInfo(token[0])
        usersMap[userInfo.userId] = session

        return session.send(session.receive()
            .doOnNext(WebSocketMessage::retain) // Use retain() for Reactor Netty
            .doOnComplete {
                usersMap.remove(userInfo.userId)
            }
            .map { m -> session.textMessage("received:" + m.payloadAsText) }
        )
    }
}
