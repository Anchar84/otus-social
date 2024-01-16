package ru.otus.social.counter.http

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.otus.social.counter.service.CounterService

@RequestMapping("/counter")
@RestController
class CounterController(
    private val counterService: CounterService
) {

    @GetMapping("/messages")
    suspend fun getMessagesCount(
        @RequestHeader("X-Auth") token: String,
        ): Int {
        return coroutineScope {
            withContext(MDCContext()) {
                val (userId, _) = Util.getUserInfo(token)
                counterService.getCounter(userId, "message")
            }
        }
    }
}
