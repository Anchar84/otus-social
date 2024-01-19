package ru.otus.social.dialogs.http

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.otus.social.dialogs.http.dto.DialogMessage
import ru.otus.social.dialogs.http.dto.MessageText
import ru.otus.social.dialogs.service.DialogsService


@RestController
@RequestMapping("/dialog")
class DialogsController(
    private val dialogsService: DialogsService
) {

    @PostMapping("/{messageId}/read", headers = ["X-API-VERSION=1"])
    suspend fun read(
        @RequestHeader("X-Auth") token: String,
        @PathVariable("messageId") messageId: Int,
    ) {
        coroutineScope {
            withContext(MDCContext()) {
                val userIndo = Util.getUserInfo(token)
                dialogsService.markAsRead(userIndo.userId, messageId)
                ResponseEntity.ok(Unit)
            }
        }
    }

    @PostMapping("/{toUserId}/send", headers = ["X-API-VERSION=1"])
    suspend fun send(
        @RequestHeader("X-Auth") token: String,
        @PathVariable("toUserId") toUserId: Int,
        @RequestBody text: MessageText
    ): ResponseEntity<Unit> {
        return coroutineScope {
            withContext(MDCContext()) {
                val userIndo = Util.getUserInfo(token)
                dialogsService.addMessage(userIndo.userId, toUserId, text.text)
                ResponseEntity.ok(Unit)
            }
        }
    }

    @GetMapping("/{toUserId}/list", headers = ["X-API-VERSION=1"])
    fun list(
        @RequestHeader("X-Auth") token: String,
        @PathVariable("toUserId") toUserId: Int,
    ): Flow<DialogMessage> {
        val userInfo = Util.getUserInfo(token)
        return dialogsService.getMessages(userInfo.userId, toUserId).map {
            DialogMessage(
                id = it.id,
                from = it.fromUserId,
                to = it.toUserId,
                text = it.text
            )
        }
    }

    @PostMapping("/{toUserId}/t/send")
    suspend fun sendT(
        @RequestHeader("X-Auth") token: String,
        @PathVariable("toUserId") toUserId: Int,
        @RequestBody text: MessageText
    ) : ResponseEntity<Unit> {
        val userIndo = Util.getUserInfo(token)
        dialogsService.addMessageT(userIndo.userId, toUserId, text.text)
        return ResponseEntity.ok(Unit)
    }

    @GetMapping("/{toUserId}/t/list")
    fun listT(
        @RequestHeader("X-Auth") token: String,
        @PathVariable("toUserId") toUserId: Int,
    ): List<DialogMessage> {
        val userInfo = Util.getUserInfo(token)
        return dialogsService.getMessagesT(userInfo.userId, toUserId).map {
            DialogMessage (
                from = it.fromUserId,
                to = it.toUserId,
                text = it.text,
                id = it.id
            )
        }
    }
}
