package ru.otus.social.dialogs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import ru.otus.social.dialogs.config.DestinationsConfig

@EnableConfigurationProperties(DestinationsConfig::class, TarantoolConfigProps::class)
@ConfigurationPropertiesScan
@SpringBootApplication
class DialogsApplication

fun main(args: Array<String>) {
    runApplication<DialogsApplication>(*args)
}
