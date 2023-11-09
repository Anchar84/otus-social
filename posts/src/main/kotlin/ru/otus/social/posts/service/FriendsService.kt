package ru.otus.social.posts.service

import org.springframework.stereotype.Service
import ru.otus.social.posts.model.Friend
import ru.otus.social.posts.repository.FriendsRepository

@Service
class FriendsService(
    private val friendsRepository: FriendsRepository,
    private val feedService: FeedService
) {

    suspend fun addFriend(friend: Friend) {
        friendsRepository.addFriend(friend)
        feedService.sendPostsFeedUpdate(friend.userId)
    }

    suspend fun deleteFriend(friend: Friend) {
        friendsRepository.deleteFriend(friend)
        feedService.sendPostsFeedUpdate(friend.userId)
    }
}
