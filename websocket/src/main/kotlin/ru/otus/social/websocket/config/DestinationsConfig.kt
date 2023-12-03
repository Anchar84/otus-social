package ru.otus.social.websocket.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("destinations")
class DestinationsConfig {

    private var queues = mutableMapOf<String, DestinationInfo>()

    fun getQueues(): Map<String, DestinationInfo> {
        return queues
    }

    fun setQueues(queues: Map<String, DestinationInfo>) {
        this.queues.clear()
        this.queues.putAll(queues)
    }

    class DestinationInfo {
        var exchange: String? = null
        var routingKey: String? = null
    }
}
