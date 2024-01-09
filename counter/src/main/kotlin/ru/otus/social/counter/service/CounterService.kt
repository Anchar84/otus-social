package ru.otus.social.counter.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service
import ru.otus.social.counter.config.RabbitConfiguration
import ru.otus.social.counter.dto.ChangeEntityStatus
import ru.otus.social.counter.http.RequestIdFilter
import ru.otus.social.counter.repository.CounterRepository

@Service
class CounterService(
    private val counterRepository: CounterRepository,
    private val rabbitConfiguration: RabbitConfiguration,
) {

    companion object {
        private const val queryName = "OTUS_POSTS_COUNTER"
        private val mapper = ObjectMapper()
        private val logger = LoggerFactory.getLogger(CounterService::class.java)
    }

    @PostConstruct
    fun init() {
        rabbitConfiguration.createMessageListenerContainer(queryName) {
            val payload = String(it.body)
            val changeEntityStatus = mapper.readValue(payload, ChangeEntityStatus::class.java)
            MDC.put(RequestIdFilter.REQUEST_ID, changeEntityStatus.requestId)
            logger.info("received message $payload")
            runBlocking {
                withContext(MDCContext()) {
                    val result = updateCounter(
                        changeEntityStatus.userId.toInt(),
                        changeEntityStatus.entityId,
                        changeEntityStatus.entityType,
                        changeEntityStatus.action
                    )
                    rabbitConfiguration.sendMessage(
                        "OTUS_POSTS_COUNTER_APPROVE",
                        mapper.writeValueAsString(
                            ChangeEntityStatus(
                                userId = changeEntityStatus.userId,
                                entityId = changeEntityStatus.entityId,
                                entityType = changeEntityStatus.entityType,
                                action = result.toString()
                            )
                        )
                    )
                }
            }
            MDC.clear()
        }
    }

    suspend fun updateCounter(userId: Int, entityId: String, entityType: String, status: String): Boolean {
        return counterRepository.saveEntityStatus(userId, entityId, entityType, status)
    }

    suspend fun getCounter(userId: Int, entityType: String): Int {
        return counterRepository.getSum(userId, entityType)
    }
}
