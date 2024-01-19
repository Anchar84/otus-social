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


@Repository
class TarantoolDialogsRepository(
    private val tarantoolProps: TarantoolConfigProps
) {

    fun setupClient(): TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> {
        return TarantoolClientFactory.createClient()
            .withAddress(
                tarantoolProps.getHost(),
                tarantoolProps.getPort()
            )
            .withCredentials(tarantoolProps.getUsername(), tarantoolProps.getPassword())
            .build()
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
        return setupClient().use { client ->
            try {
                return (client.call(
                    "find_dialod_messages",
                    fromUserId,
                    toUserId
                ).get() as (List<List<List<Object>>>))[0]
                    .map { map(it) } +
                      (client.call(
                    "find_dialod_messages",
                    toUserId,
                    fromUserId
                ).get() as (List<List<List<Object>>>))[0]
                    .map { map(it) }
                          .sortedBy { it.createdAt }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            emptyList()
        }
    }

    fun saveMessage(fromUserId: Int, toUserId: Int, text: String, createdAt: Instant) {
        setupClient().use { client ->
            try {
                client.call("add_dialod_messages", fromUserId, toUserId, text, createdAt.epochSecond).get()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

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
