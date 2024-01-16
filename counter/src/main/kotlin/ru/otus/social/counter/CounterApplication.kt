package ru.otus.social.counter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import ru.otus.social.counter.config.DestinationsConfig




@EnableConfigurationProperties(DestinationsConfig::class)
@SpringBootApplication
class CounterApplication

fun main(args: Array<String>) {
	runApplication<CounterApplication>(*args)
}
