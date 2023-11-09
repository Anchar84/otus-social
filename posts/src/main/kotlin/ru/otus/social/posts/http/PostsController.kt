package ru.otus.social.posts.http

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

    @GetMapping("/get/{postId}")
    suspend fun getPost(
        @PathVariable("postId") postId: Int
    ): ResponseEntity<PostGetDto> {
        val post = postService.getPost(postId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(
            PostGetDto(
                id = post.id!!,
                authorUserId = post.authorId,
                text = post.text
            )
        )
    }

    @PutMapping("/update")
    suspend fun updatePost(
        @RequestHeader("X-Auth") token: String,
        @RequestBody post: PostUpdateDto
    ): ResponseEntity<Unit> {
        val (userId, _) = Util.getUserInfo(token)
        postService.updatePost(post.id, userId, post.text)
        return ResponseEntity.ok(Unit)
    }

    @DeleteMapping("/delete/{postId}")
    suspend fun deletePost(
        @RequestHeader("X-Auth") token: String,
        @PathVariable("postId") postId: Int
    ): ResponseEntity<Unit> {
        val (userId, _) = Util.getUserInfo(token)
        postService.deletePost(postId, userId)
        return ResponseEntity.ok(Unit)
    }

    @GetMapping("/feed")
    suspend fun getFeed(
        @RequestHeader("X-Auth") token: String,
        @RequestParam("offset", defaultValue = "0") offset: Int = 0,
        @RequestParam("limit", defaultValue = "10") limit: Int = 10,
    ): List<PostGetDto> {
        val (userId, _) = Util.getUserInfo(token)

        return feedService.getPostsFeed(userId, offset, limit).map {
            PostGetDto(
                id = it.id!!,
                authorUserId = it.authorId,
                text = it.text
            )
        }
    }

    @PostMapping("/feed")
    suspend fun createCache(
        @RequestHeader("X-Auth") token: String,
    ) {
        val (userId, _) = Util.getUserInfo(token)
        feedService.createFeed(userId)
    }

    @PostMapping("/create")
    suspend fun addPosts(
        @RequestHeader("X-Auth") token: String,
        @RequestBody text: String
    ): Int {
        val (userId, userName) = Util.getUserInfo(token)

        return postService.createPost(
            Post(
                authorId = userId,
                authorName = userName,
                text = text
            )
        )
    }

}
