package ru.otus.social.posts.repository

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import ru.otus.social.posts.model.Friend

@Repository
class FriendsRepository(
    private val connectionFactory: ConnectionFactory
) {

    private val logger = LoggerFactory.getLogger(FriendsRepository::class.java)

    suspend fun addFriend(friend: Friend) {
        val connection = connectionFactory.create().awaitSingle()
        try {
            val insertPost = connection.createStatement(
                """
                insert into t_friends (userId, friendId)
                values ($1, $2) on conflict do nothing
            """.trimIndent()
            ).returnGeneratedValues("friendId")
            insertPost.bind("$1",friend.friendId)
            insertPost.bind("$2",friend.userId)

            insertPost.execute().awaitSingle().map { row -> row.get("friendId") }.awaitFirstOrNull()
        } finally {
            connection.close()
        }
    }

    suspend fun deleteFriend(friend: Friend) {
        val dataClient = DatabaseClient.create(connectionFactory)
        val deletedRows = dataClient.sql("delete from t_friends where userId = :1 and friendId = :2")
            .bind("1", friend.userId)
            .bind("2", friend.friendId)
            .fetch()
            .rowsUpdated()
            .awaitSingle()

        logger.info("deleted $deletedRows rows")

    }

    fun getAllFriends(userId: Int): Flow<Int> {
        return DatabaseClient.create(connectionFactory)
            .sql("""select friendId from t_friends where userId = :1""")
            .bind("1", userId)
            .map { it ->
                it.get("friendId", Integer::class.java)?.toInt() ?: 0
            }
            .flow()
    }

    fun getAllUsers(): Flow<Int> {
        return DatabaseClient.create(connectionFactory)
            .sql("""select distinct userId from t_friends """)
            .map { it ->
                it.get("userId", Integer::class.java)?.toInt() ?: 0
            }
            .flow()
    }
}
