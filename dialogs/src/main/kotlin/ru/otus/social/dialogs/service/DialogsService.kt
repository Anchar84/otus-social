package ru.otus.social.dialogs.service

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import ru.otus.social.dialogs.model.Dialog
import ru.otus.social.dialogs.repository.DialogsRepository
import ru.otus.social.dialogs.repository.TarantoolDialogsRepository
import ru.otus.social.dialogs.repository.toDialog
import java.time.ZonedDateTime

@Service
class DialogsService(
    private val dialogsRepository: DialogsRepository,
    private val tarantoolDialogsRepository: TarantoolDialogsRepository
) {

    suspend fun addMessage(fromUserId: Int, toUserId: Int, text: String) {
        dialogsRepository.createDialog(
            Dialog(fromUserId, toUserId, text)
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
