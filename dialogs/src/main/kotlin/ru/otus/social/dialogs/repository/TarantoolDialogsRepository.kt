package ru.otus.social.dialogs.repository

import io.tarantool.driver.api.conditions.Conditions;
import io.tarantool.driver.api.TarantoolClient
import io.tarantool.driver.api.TarantoolResult
import io.tarantool.driver.api.space.TarantoolSpaceOperations
import io.tarantool.driver.api.tuple.DefaultTarantoolTupleFactory
import io.tarantool.driver.api.tuple.TarantoolTuple
import org.springframework.data.annotation.Id
import org.springframework.data.tarantool.core.mapping.Field
import org.springframework.data.tarantool.core.mapping.Tuple
import org.springframework.data.tarantool.repository.Query
import org.springframework.data.tarantool.repository.TarantoolRepository
import org.springframework.data.tarantool.repository.TarantoolSerializationType
import org.springframework.stereotype.Repository
import ru.otus.social.dialogs.model.Dialog
import java.time.Instant
import java.util.*


@Repository
interface TarantoolDialogsRepository: TarantoolRepository<TarantoolDialogDTO, Int> {

    companion object {
        private const val ns = "dialogs"
    }

    @Query(function = "find_dialod_messages", output = TarantoolSerializationType.TUPLE)
    fun loadMessages(fromUserId: Int, toUserId: Int): List<TarantoolDialogDTO>
//    {
//        val space: TarantoolSpaceOperations<TarantoolTuple, TarantoolResult<TarantoolTuple>> = tarantoolClient.space(ns)
//
//        val tuples = space.select(Conditions.any()).get()
//        println(tuples.size)
//
//        return emptyList()
//    }

    @Query(function = "add_dialod_messages", output = TarantoolSerializationType.TUPLE)
    fun saveMessage(fromUserId: Int, toUserId: Int, test: String, createdAt: Instant)
//    {
//        val space: TarantoolSpaceOperations<TarantoolTuple, TarantoolResult<TarantoolTuple>> = tarantoolClient.space(ns)
//
//        // Use TarantoolTupleFactory for instantiating new tuples
//        val tupleFactory = DefaultTarantoolTupleFactory(
//            tarantoolClient.config.messagePackMapper
//        )
//        space.insertMany(listOf(tupleFactory.create(null, fromUserId, toUserId, test, createdAt))).get()
//    }

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
        fromUserId = dialog.fromUserId,
        toUserId = dialog.toUserId,
        text = dialog.text
    )
}
