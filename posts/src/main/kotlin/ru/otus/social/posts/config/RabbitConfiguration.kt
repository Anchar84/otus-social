package ru.otus.social.posts.config

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.MessageListenerContainer
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.stereotype.Component
import ru.otus.social.posts.config.DestinationsConfig.DestinationInfo


@Component
class RabbitConfiguration(
    private val connectionFactory: ConnectionFactory,
    private val amqpTemplate: AmqpTemplate,
    private val amqpAdmin: AmqpAdmin,
    private val destinationsConfig: DestinationsConfig
    ) {

    private val logger = LoggerFactory.getLogger(RabbitConfiguration::class.java)

    @PostConstruct
    fun setupQueueDestinations() {
        logger.info("[I48] Creating Destinations...")
        destinationsConfig.getQueues()
            .forEach { (key: String, destination: DestinationInfo) ->
                logger.info(
                    "[I54] Creating directExchange: key={}, name={}, routingKey={}",
                    key,
                    destination.exchange,
                    destination.routingKey
                )
                val ex = ExchangeBuilder.directExchange(destination.exchange)
                    .build<DirectExchange>()
                amqpAdmin.declareExchange(ex)

                val q = QueueBuilder.durable(key)
                    .build()
                logger.info("create queue: ${amqpAdmin.declareQueue(q)}")

                val b = BindingBuilder.bind(q)
                    .to(ex)
                    .with(destination.routingKey)
                amqpAdmin.declareBinding(b)

                logger.info("[I70] Binding successfully created.")
            }
    }

    fun createMessageListenerContainer(queueName: String, messageListener: MessageListener): MessageListenerContainer {
        val mlc = SimpleMessageListenerContainer(connectionFactory)
        mlc.addQueueNames(queueName)
        mlc.acknowledgeMode = AcknowledgeMode.AUTO

        mlc.setupMessageListener(messageListener)
        mlc.start()

        return mlc
    }

    fun sendMessage(query: String, payload: String) {
        val destination = destinationsConfig.getQueues()[query] ?: return
        amqpTemplate.convertAndSend(destination.exchange, destination.routingKey, payload)
    }
}
