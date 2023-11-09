package ru.otus.social.posts.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory


//@Configuration
class RedisConfiguration(
//    @Value("\${spring.redis.host}") private val redisHost: String,
//    @Value("\${spring.redis.post}") private val redisPost: String
) {

//    @Bean
//    fun redisConnectionFactory(): ReactiveRedisConnectionFactory {
//        return LettuceConnectionFactory(this.redisHost, redisPost.toInt())
//    }
}
