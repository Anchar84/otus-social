package ru.otus.social.posts.repository

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.*
import org.slf4j.LoggerFactory
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import ru.otus.social.posts.model.Post
import java.time.LocalDateTime

@Repository
class PostsRepository(
    private val connectionFactory: ConnectionFactory
) {

    private val logger = LoggerFactory.getLogger(PostsRepository::class.java)

    suspend fun createPost(post: Post): Int {
        val connection = connectionFactory.create().awaitSingle()
        try {
            val insertPost = connection.createStatement(
                """
                insert into t_posts (authorId, authorName, text)
                values ($1, $2, $3)
            """.trimIndent()
            ).returnGeneratedValues("id")
            insertPost.bind("$1", post.authorId)
            insertPost.bind("$2", post.authorName)
            insertPost.bind("$3", post.text)

            val rs = insertPost.execute().awaitSingle()
            val postId = rs.map { row -> row.get("id", Integer::class.java) }.awaitFirst()!!
            return postId.toInt()
        } finally {
            connection.close().awaitFirstOrNull()
        }
    }

    suspend fun updatePost(post: Post, newText: String) {
        val updatedPosts = DatabaseClient.create(connectionFactory)
            .sql("update t_posts set text = :2 where id = :1")
            .bind("1", post.id!!)
            .bind("2", newText)
            .fetch()
            .rowsUpdated()
            .awaitSingle()
        logger.info("updated $updatedPosts posts")
    }

    suspend fun getPost(postId: Int): Post? {
        val connection = connectionFactory.create().awaitSingle()
        try {
            val selectPost = connection.createStatement(
                """
                select * from t_posts where id = $1
            """.trimIndent()
            )
            selectPost.bind("$1", postId)

            return selectPost.execute().awaitSingle().map { it ->
                Post(
                    id = it.get("id", Integer::class.java)?.toInt(),
                    authorId = it.get("authorId", Integer::class.java)?.toInt() ?: 0,
                    authorName = it.get("authorName", String::class.java) ?: "",
                    createdAt = it.get("createdAt", LocalDateTime::class.java) ?: LocalDateTime.MIN,
                    text = it.get("text", String::class.java) ?: ""
                )
            }.awaitFirstOrNull()
        } finally {
            connection.close().awaitFirstOrNull()
        }
    }

    suspend fun deletePost(postId: Int) {
        val deletedRows = DatabaseClient.create(connectionFactory)
            .sql("delete from t_posts where id = :1")
            .bind("1", postId)
            .fetch()
            .rowsUpdated()
            .awaitSingle()

        logger.info("deleted $deletedRows posts")
    }

    fun getFeedPosts(userId: Int): Flow<Post> {
        return DatabaseClient.create(connectionFactory)
            .sql("""select p.* from t_posts p 
                     where p.authorId in (select friendId from t_friends f where f.userId = :1)
                     order by p.createdAt desc limit 1000""".trimMargin())
            .bind("1", userId)
            .map { it ->
                Post(
                    id = it.get("id", Integer::class.java)?.toInt(),
                    authorId = it.get("authorId", Integer::class.java)?.toInt() ?: 0,
                    authorName = it.get("authorName", String::class.java) ?: "",
                    createdAt = it.get("createdAt", LocalDateTime::class.java) ?: LocalDateTime.MIN,
                    text = it.get("text", String::class.java) ?: ""
                )
            }
            .flow()
    }

}
