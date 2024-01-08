package ru.otus.social.posts.http

import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.*

@Component
class RequestIdFilter: WebFilter {

    companion object {
        const val REQUEST_ID = "requestId"
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestId = exchange.request.headers["X-REQUEST-ID"]?.first()
        MDC.put(REQUEST_ID, requestId ?: UUID.randomUUID().toString())
        return chain.filter(exchange)
    }
}
