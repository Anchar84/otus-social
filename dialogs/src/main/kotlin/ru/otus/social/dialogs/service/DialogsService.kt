package ru.otus.social.dialogs.service

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import ru.otus.social.dialogs.model.Dialog
import ru.otus.social.dialogs.repository.DialogsRepository

@Service
class DialogsService (
    private val dialogsRepository: DialogsRepository
) {

    suspend fun addMessage(fromUserId: Int, toUserId: Int, text: String) {
        dialogsRepository.createDialog(
            Dialog(fromUserId, toUserId, text)
        )
    }

    fun getMessages(user1Id: Int, user2Id: Int): Flow<Dialog> {
        return dialogsRepository.getDialogs(user1Id, user2Id)
    }
}
