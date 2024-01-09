package ru.otus.social.dialogs.repository

import io.r2dbc.spi.ConnectionFactory
import org.slf4j.LoggerFactory
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import ru.otus.social.dialogs.model.Dialog
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.flow
import kotlinx.coroutines.flow.Flow


@Repository
class DialogsRepository(
    private val connectionFactory: ConnectionFactory
) {

    suspend fun createDialog(dialog: Dialog): Int {
        val id = DatabaseClient.create(connectionFactory)
            .sql("insert into t_dialogs(fromUserId, toUserId, text, shadr_key) values (:1, :2, :3, :4)")
            .filter{ s, next -> next.execute(s.returnGeneratedValues("id"))}
            .bind("1", dialog.fromUserId)
            .bind("2", dialog.toUserId)
            .bind("3", dialog.text)
            .bind("4", "${dialog.fromUserId}_${dialog.toUserId}")
            .fetch()
            .first()
            .map { r -> r["id"]?.toString()?.toInt() ?: 0 }
            .awaitSingle()

        return id
    }

    fun getDialogs(user1Id: Int, user2Id: Int): Flow<Dialog> {
        return DatabaseClient.create(connectionFactory)
            .sql("select * from t_dialogs " +
                    "where shadr_key = :1 or shadr_key = :2 " +
                    "order by createdAt")
            .bind("1", "${user1Id}_$user2Id")
            .bind("2", "${user2Id}_$user1Id")
            .map { it ->
                Dialog(
                    id = it.get("id", Integer::class.java)?.toInt() ?: 0,
                    fromUserId = it.get("fromUserId", Integer::class.java)?.toInt() ?: 0,
                    toUserId = it.get("toUserId", Integer::class.java)?.toInt() ?: 0,
                    text = it.get("text", String::class.java) ?: ""
                )
            }
            .flow()
    }

}
