package ru.otus.social.posts.http

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.otus.social.posts.http.dto.PostGetDto
import ru.otus.social.posts.http.dto.PostUpdateDto
import ru.otus.social.posts.model.Post
import ru.otus.social.posts.service.FeedService
import ru.otus.social.posts.service.PostService

@RestController
@RequestMapping("/post")
class PostsController(
    private val postService: PostService,
    private val feedService: FeedService
) {

    @GetMapping("/get/{postId}", headers = ["X-API-VERSION=1"])
    suspend fun getPost(
        @PathVariable("postId") postId: Int
    ): ResponseEntity<PostGetDto> {
        return coroutineScope {
            withContext(MDCContext()) {
                val post = postService.getPost(postId) ?: return@withContext ResponseEntity.notFound().build()
                return@withContext ResponseEntity.ok(
                    PostGetDto(
                        id = post.id!!,
                        authorUserId = post.authorId,
                        text = post.text
                    )
                )
            }
        }
    }

    @PutMapping("/update", headers = ["X-API-VERSION=1"])
    suspend fun updatePost(
        @RequestHeader("X-Auth") token: String,
        @RequestBody post: PostUpdateDto
    ): ResponseEntity<Unit> {
        return coroutineScope {
            withContext(MDCContext()) {
                val (userId, _) = Util.getUserInfo(token)
                postService.updatePost(post.id, userId, post.text)
                ResponseEntity.ok(Unit)
            }
        }
    }

    @DeleteMapping("/delete/{postId}", headers = ["X-API-VERSION=1"])
    suspend fun deletePost(
        @RequestHeader("X-Auth") token: String,
        @PathVariable("postId") postId: Int
    ): ResponseEntity<Unit> {
        return coroutineScope {
            withContext(MDCContext()) {
                val (userId, _) = Util.getUserInfo(token)
                postService.deletePost(postId, userId)
                ResponseEntity.ok (Unit)
            }
        }
    }

    @GetMapping("/feed", headers = ["X-API-VERSION=1"])
    suspend fun getFeed(
        @RequestHeader("X-Auth") token: String,
        @RequestParam("offset", defaultValue = "0") offset: Int = 0,
        @RequestParam("limit", defaultValue = "10") limit: Int = 10,
    ): List<PostGetDto> {
        return coroutineScope {
            withContext(MDCContext()) {
                val (userId, _) = Util.getUserInfo(token)

                feedService.getPostsFeed(userId, offset, limit).map {
                    PostGetDto(
                        id = it.id!!,
                        authorUserId = it.authorId,
                        text = it.text
                    )
                }
            }
        }
    }

    @PostMapping("/feed", headers = ["X-API-VERSION=1"])
    suspend fun createCache(
        @RequestHeader("X-Auth") token: String,
    ) {
        coroutineScope {
            withContext(MDCContext()) {
                val (userId, _) = Util.getUserInfo(token)
                feedService.createFeed(userId)
            }
        }
    }

    @PostMapping("/create", headers = ["X-API-VERSION=1"])
    suspend fun addPosts(
        @RequestHeader("X-Auth") token: String,
        @RequestBody text: String
    ): Int {
        return coroutineScope {
            withContext(MDCContext()) {
                val (userId, userName) = Util.getUserInfo(token)

                postService.createPost(
                    Post(
                        authorId = userId,
                        authorName = userName,
                        text = text
                    )
                )
            }
        }
    }

}
