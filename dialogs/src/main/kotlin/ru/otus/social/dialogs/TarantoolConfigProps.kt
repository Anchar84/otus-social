package ru.otus.social.dialogs

import org.springframework.boot.context.properties.ConfigurationProperties
@ConfigurationProperties(prefix = "tarantool")
class TarantoolConfigProps {

    private var host: String = "tarantool"
    private var port: Int = 3301
    private var username: String = "test"
    private var password: String = "test"

    fun setHost(host: String) {
        this.host = host
    }

    fun setPort(port: Int) {
        this.port = port
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun setPassword(password: String) {
        this.password = password
    }

    fun getHost(): String {
        return host
    }

    fun getPort(): Int {
        return port
    }

    fun getUsername(): String {
        return username
    }

    fun getPassword(): String {
        return password
    }
}
