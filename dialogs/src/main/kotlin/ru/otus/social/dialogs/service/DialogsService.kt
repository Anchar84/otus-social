package ru.otus.social.dialogs.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service
import ru.otus.social.dialogs.config.RabbitConfiguration
import ru.otus.social.dialogs.dto.ChangeEntityStatus
import ru.otus.social.dialogs.http.RequestIdFilter
import ru.otus.social.dialogs.model.Dialog
import ru.otus.social.dialogs.repository.DialogsRepository
import ru.otus.social.dialogs.repository.TarantoolDialogsRepository
import ru.otus.social.dialogs.repository.toDialog
import java.time.ZonedDateTime

@Service
class DialogsService (
    private val dialogsRepository: DialogsRepository,
    private val rabbitConfiguration: RabbitConfiguration,
    private val tarantoolDialogsRepository: TarantoolDialogsRepository
) {
    companion object {
        private const val queryName = "OTUS_POSTS_COUNTER"
        private val mapper = ObjectMapper()
        private val logger = LoggerFactory.getLogger(DialogsService::class.java)
    }

    @PostConstruct
    fun init() {
        rabbitConfiguration.createMessageListenerContainer("OTUS_POSTS_COUNTER_APPROVE") {
            val payload = String(it.body)
            val changeEntityStatus = mapper.readValue(payload, ChangeEntityStatus::class.java)
            MDC.put(RequestIdFilter.REQUEST_ID, changeEntityStatus.requestId)
            logger.info("received message $payload")
            MDC.clear()
        }
    }

    fun markAsRead(userId: Int, messageId: Int) {
        rabbitConfiguration.sendMessage(
            queryName,
            mapper.writeValueAsString(
                ChangeEntityStatus(
                    userId = userId.toString(),
                    entityId = messageId.toString(),
                    entityType = "message",
                    action = "read"
                )
            )
        )
    }

    suspend fun addMessage(fromUserId: Int, toUserId: Int, text: String) {
        val id = dialogsRepository.createDialog(
            Dialog(0, fromUserId, toUserId, text)
        )
        rabbitConfiguration.sendMessage(
            queryName,
            mapper.writeValueAsString(
                ChangeEntityStatus(
                    userId = toUserId.toString(),
                    entityId = id.toString(),
                    entityType = "message",
                    action = "added"
                )
            )
        )
    }

    fun getMessages(user1Id: Int, user2Id: Int): Flow<Dialog> {
        return dialogsRepository.getDialogs(user1Id, user2Id)
    }

    fun addMessageT(fromUserId: Int, toUserId: Int, text: String) {
        tarantoolDialogsRepository.saveMessage(fromUserId, toUserId, text, ZonedDateTime.now().toInstant())
    }

    fun getMessagesT(user1Id: Int, user2Id: Int): List<Dialog> {
        return tarantoolDialogsRepository.loadMessages(user1Id, user2Id).sortedBy { it.createdAt }.map { toDialog(it) }
    }
}
