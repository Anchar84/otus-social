package ru.otus.social.posts.http

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.otus.social.posts.model.Friend
import ru.otus.social.posts.service.FriendsService

@RestController
@RequestMapping("/friend")
class FriendsController(
    private val friendsService: FriendsService
) {

    @PutMapping("/add/{friendId}", headers = ["X-API-VERSION=1"])
    suspend fun addFriend(
        @RequestHeader("X-Auth") token: String,
        @PathVariable("friendId") friendId: Int
    ): ResponseEntity<Unit> {
        return coroutineScope {
            withContext(MDCContext()) {
                val userInfo = Util.getUserInfo(token)
                friendsService.addFriend(Friend(userInfo.userId, friendId))
                ResponseEntity.ok(Unit)
            }
        }
    }

    @DeleteMapping("/delete/{friendId}", headers = ["X-API-VERSION=1"])
    suspend fun deleteFriend(
        @RequestHeader("X-Auth") token: String,
        @PathVariable("friendId") friendId: Int
    ): ResponseEntity<Unit> {
        return coroutineScope {
            withContext(MDCContext()) {
                val userInfo = Util.getUserInfo(token)
                friendsService.deleteFriend(Friend(userInfo.userId, friendId))
                ResponseEntity.ok(Unit)
            }
        }
    }
}
