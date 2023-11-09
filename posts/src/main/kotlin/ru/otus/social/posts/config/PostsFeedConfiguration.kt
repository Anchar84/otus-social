package ru.otus.social.posts.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import ru.otus.social.posts.model.UserPostsFeed
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Configuration
class PostsFeedConfiguration {

    @Bean
    fun redisPostsFeedOptions(factory: ReactiveRedisConnectionFactory): ReactiveRedisOperations<String, UserPostsFeed> {
        val timeModule = JavaTimeModule()
        timeModule.addSerializer(
            LocalDateTime::class.java,
            LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        timeModule.addDeserializer(
            LocalDateTime::class.java,
            LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(timeModule)
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

        val serializer = Jackson2JsonRedisSerializer(objectMapper, UserPostsFeed::class.java)

        val builder = RedisSerializationContext.newSerializationContext<String, UserPostsFeed>(StringRedisSerializer())

        return ReactiveRedisTemplate(factory, builder.value(serializer).build())
    }
}
