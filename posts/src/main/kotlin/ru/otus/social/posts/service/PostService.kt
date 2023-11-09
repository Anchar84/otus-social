package ru.otus.social.posts.service

import org.springframework.stereotype.Service
import ru.otus.social.posts.model.Post
import ru.otus.social.posts.repository.FriendsRepository
import ru.otus.social.posts.repository.PostsRepository

@Service
class PostService(
    private val postsRepository: PostsRepository,
    private val friendsRepository: FriendsRepository,
    private val feedService: FeedService
) {

    suspend fun createPost(post: Post): Int {
        val postId = postsRepository.createPost(post)
        friendsRepository.getAllFriends(post.authorId).collect {
            feedService.sendPostsFeedUpdate(it)
        }
        return postId
    }

    suspend fun updatePost(postId: Int, authorId: Int, newText: String) {
        val post = getPost(postId)
        if (post?.authorId == authorId) {
            postsRepository.updatePost(post, newText)
            friendsRepository.getAllFriends(authorId).collect {
                feedService.sendPostsFeedUpdate(it)
            }
        }
    }

    suspend fun deletePost(postId: Int, authorId: Int) {
        val post = getPost(postId)
        if (post?.authorId == authorId) {
            postsRepository.deletePost(post.id!!)
            friendsRepository.getAllFriends(authorId).collect {
                feedService.sendPostsFeedUpdate(it)
            }
        }
    }

    suspend fun getPost(id: Int): Post? {
       return postsRepository.getPost(id)
    }


}
