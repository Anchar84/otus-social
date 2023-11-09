package ru.otus.social.posts

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import ru.otus.social.posts.config.DestinationsConfig




@EnableConfigurationProperties(DestinationsConfig::class)
@SpringBootApplication
class PostsApplication

fun main(args: Array<String>) {
	runApplication<PostsApplication>(*args)
}
