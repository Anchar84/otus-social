package ru.otus.social.posts.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Service
import ru.otus.social.posts.config.RabbitConfiguration
import ru.otus.social.posts.model.NewPostDto
import ru.otus.social.posts.model.NewPostEvent
import ru.otus.social.posts.model.Post
import ru.otus.social.posts.model.UserPostsFeed
import ru.otus.social.posts.repository.FriendsRepository
import ru.otus.social.posts.repository.PostsRepository
import java.time.LocalDateTime

@Service
class FeedService(
    private val postsFeedOperations: ReactiveRedisOperations<String, UserPostsFeed>,
    private val postsRepository: PostsRepository,
    private val friendsRepository: FriendsRepository,
    private val rabbitConfiguration: RabbitConfiguration
) {

    private val logger = LoggerFactory.getLogger(FeedService::class.java)
    private val queryName = "OTUS_POSTS_UPDATE"
    private val newPostQueryName = "OTUS_SEND_POSTS_UPDATE"
    private val mapper = ObjectMapper()

    @PostConstruct
    fun registryListeners() {
        rabbitConfiguration.createMessageListenerContainer(queryName) {
            val payload = String(it.body)
            logger.info("received message $payload")
            runBlocking { createFeed(payload.toInt()) }
        }

        GlobalScope.launch {
            val count = postsFeedOperations.keys("*")
                .count()
                .awaitSingle()
            if (count == 0L) {
                friendsRepository.getAllUsers().collect {
                    sendPostsFeedUpdate(it)
                }
            }
        }


    }

    fun getFeed(userId: Int): Flow<Post> {
        return postsRepository.getFeedPosts(userId)
    }

    suspend fun getPostsFeed(userId: Int, offset: Int, limit: Int): List<Post> {
        val posts = postsFeedOperations.keys(userId.toString()).flatMap { postsFeedOperations.opsForValue().get(it) }
            .awaitFirstOrNull()
        return if (posts != null) {
            posts.posts.subList(
                offset,
                if (offset + limit < posts.posts.size)
                    offset + limit
                else
                    posts.posts.size
            )
        } else {
            logger.info("no cached posts found")
            sendPostsFeedUpdate(userId)
            emptyList()
        }
    }

    suspend fun createFeed(userId: Int) {
        val posts = postsRepository.getFeedPosts(userId)
        val mPosts = mutableListOf<Post>()
        posts.collect { post -> mPosts.add(post) }
        postsFeedOperations.opsForValue().set(
            userId.toString(), UserPostsFeed(
                userId = userId,
                posts = mPosts,
                createAt = LocalDateTime.now()
            )
        ).awaitSingle()
    }

    fun sendPostsFeedUpdate(userId: Int) {
        rabbitConfiguration.sendMessage(queryName, userId.toString())
    }

    fun sendNewPostEvent(receiverId: Int, postId: Int, post: Post) {
        rabbitConfiguration.sendMessage(newPostQueryName, mapper.writeValueAsString(NewPostEvent(
            receiverId = receiverId,
            post = NewPostDto(
                id = postId,
                authorUserId = post.authorId,
                text = post.text
            )
        )))
    }
}
