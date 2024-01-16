package ru.otus.social.counter.repository

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingle
import org.springframework.stereotype.Repository

@Repository
class CounterRepository(
    private val connectionFactory: ConnectionFactory
) {

    companion object {
        private val logger = LoggerFactory.getLogger(CounterRepository::class.java)
    }

    suspend fun saveEntityStatus(userId: Int, entityId: String, entityType: String, status: String): Boolean {
        try {
            if (status == "added") {
                DatabaseClient.create(connectionFactory)
                    .sql("""
                        insert into t_counter (userId, entityId, entityType) 
                        values (:1, :2, :3) on conflict do nothing 
                    """.trimMargin())
                    .bind("1", userId)
                    .bind("2", entityId)
                    .bind("3", entityType)
                    .fetch()
                    .rowsUpdated()
                    .awaitSingle()
            } else {
                DatabaseClient.create(connectionFactory)
                    .sql("""
                        delete from t_counter 
                        where  userId = :1 and entityId = :2 and entityType = :3
                    """.trimMargin())
                    .bind("1", userId)
                    .bind("2", entityId)
                    .bind("3", entityType)
                    .fetch()
                    .rowsUpdated()
                    .awaitSingle()
            }
            DatabaseClient.create(connectionFactory)
                .sql("""
                    insert into t_counter_sum (userid, entityType, sum) (select :1, :2, count(*) from t_counter where userId = :1 and entityType = :2)
                    on conflict (userId, entityType) do update set sum = (select count(*) from t_counter where userId = :1 and entityType = :2)
                    where t_counter_sum.userId = :1 and t_counter_sum.entityType = :2
                """.trimIndent())
                .bind("1", userId)
                .bind("2", entityType)
                .fetch()
                .rowsUpdated()
                .awaitSingle()
            return true
        } catch (e: Exception) {
            logger.error("cannot update counter", e)
            return false
        }
    }

    suspend fun getSum(userId: Int, entityType: String): Int {
        return DatabaseClient.create(connectionFactory)
            .sql("select sum from t_counter_sum where userId = :1 and entityType = :2")
            .bind("1", userId)
            .bind("2", entityType)
            .map { it -> it.get("sum", Integer::class.java)?.toInt() ?: 0 }
            .awaitSingle()
    }
}
