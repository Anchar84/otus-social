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
                from = it.fromUserId,
                to = it.toUserId,
                text = it.text
            )
        }
    }
}
