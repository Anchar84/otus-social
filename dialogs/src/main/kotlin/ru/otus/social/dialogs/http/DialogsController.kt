package ru.otus.social.dialogs.http

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.otus.social.dialogs.http.dto.DialogMessage
import ru.otus.social.dialogs.http.dto.MessageText
import ru.otus.social.dialogs.service.DialogsService


@RestController
@RequestMapping("/dialog")
class DialogsController (
    private val dialogsService: DialogsService
) {

    @PostMapping("/{toUserId}/send")
    suspend fun send(
        @RequestHeader("X-Auth") token: String,
        @PathVariable("toUserId") toUserId: Int,
        @RequestBody text: MessageText
    ) : ResponseEntity<Unit> {
        val userIndo = Util.getUserInfo(token)
        dialogsService.addMessage(userIndo.userId, toUserId, text.text)
        return ResponseEntity.ok(Unit)
    }

    @GetMapping("/{toUserId}/list")
    fun list(
        @RequestHeader("X-Auth") token: String,
        @PathVariable("toUserId") toUserId: Int,
    ): Flow<DialogMessage> {
        val userInfo = Util.getUserInfo(token)
        return dialogsService.getMessages(userInfo.userId, toUserId).map {
            DialogMessage (
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
                text = it.text
            )
        }
    }
}
