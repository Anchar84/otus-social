package ru.otus.social.dialogs.repository

import io.tarantool.driver.api.TarantoolClient
import io.tarantool.driver.api.TarantoolClientFactory
import io.tarantool.driver.api.TarantoolResult
import io.tarantool.driver.api.tuple.TarantoolTuple
import org.springframework.data.annotation.Id
import org.springframework.data.tarantool.core.mapping.Field
import org.springframework.data.tarantool.core.mapping.Tuple
import org.springframework.stereotype.Repository
import ru.otus.social.dialogs.TarantoolConfigProps
import ru.otus.social.dialogs.model.Dialog
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap


@Repository
class TarantoolDialogsRepository(
    private val tarantoolProps: TarantoolConfigProps
) {

    private val pool = mutableListOf<Connection>()

    fun setupClient(): TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> {


        return TarantoolClientFactory.createClient()
            .withAddress(
                tarantoolProps.getHost(),
                tarantoolProps.getPort()
            )
            .withCredentials(tarantoolProps.getUsername(), tarantoolProps.getPassword())
            .build()
    }

    @Synchronized fun getConnection(): Connection? {
        if (pool.isEmpty()) {
            (1..30).forEach { pool.add(Connection(it, setupClient())) }
        }
        var connection: Connection? = null
        while (connection == null) {
            connection = pool.firstOrNull { it.available }
            if (connection == null) {
                Thread.sleep(300)
            } else {
                connection.available = false
                return connection
            }
        }
        return null
    }

    private fun map(tuple: List<Object>): TarantoolDialogDTO {
        return TarantoolDialogDTO(
            id = tuple[0] as Int,
            fromUserId = tuple[1] as Int,
            toUserId = tuple[2] as Int,
            text = tuple[3] as String,
            createdAt = Instant.ofEpochSecond((tuple[4] as Int).toLong())
        )
    }

    fun loadMessages(fromUserId: Int, toUserId: Int): List<TarantoolDialogDTO> {
        val connection = getConnection() ?: return emptyList()
        return try {
            (connection.client.call(
                "find_dialod_messages",
                fromUserId,
                toUserId
            ).get() as (List<List<List<Object>>>))[0]
                .map { map(it) } +
                    (connection.client.call(
                        "find_dialod_messages",
                        toUserId,
                        fromUserId
                    ).get() as (List<List<List<Object>>>))[0]
                        .map { map(it) }
                        .sortedBy { it.createdAt }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        } finally {
            connection.available = true
        }

    }

    fun saveMessage(fromUserId: Int, toUserId: Int, text: String, createdAt: Instant) {
        val connection = getConnection() ?: return
        try {
            connection.client.call("add_dialod_messages", fromUserId, toUserId, text, createdAt.epochSecond).get()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.available = true
        }

    }
}

class Connection (
    var inx: Int,
    val client: TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>>,
    var available: Boolean = true
)

@Tuple("dialogs")
data class TarantoolDialogDTO(
    @Id
    @Field
    val id: Int? = null,
    @Field
    val fromUserId: Int,
    @Field
    val toUserId: Int,
    @Field
    val text: String,
    @Field
    val createdAt: Instant
)

fun toDialog(dialog: TarantoolDialogDTO): Dialog {
    return Dialog(
        id = dialog.id ?: 0,
        fromUserId = dialog.fromUserId,
        toUserId = dialog.toUserId,
        text = dialog.text
    )
}
