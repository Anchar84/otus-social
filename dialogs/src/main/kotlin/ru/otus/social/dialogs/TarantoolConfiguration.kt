package ru.otus.social.dialogs

import io.tarantool.driver.api.*
import io.tarantool.driver.api.tuple.TarantoolTuple
import io.tarantool.driver.auth.SimpleTarantoolCredentials
import io.tarantool.driver.auth.TarantoolCredentials
import io.tarantool.driver.core.ProxyTarantoolTupleClient
import org.springframework.context.annotation.Configuration
import org.springframework.data.tarantool.config.AbstractTarantoolDataConfiguration
import org.springframework.data.tarantool.repository.config.EnableTarantoolRepositories
import ru.otus.social.dialogs.repository.TarantoolDialogsRepository


//@Configuration
//class TarantoolConfigLocal(
//    private val tarantoolProps: TarantoolConfigProps
//) {
//
//    @Bean
//    fun tarantoolClient(): TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> {
//        return TarantoolClientFactory.createClient()
//            // If any addresses or an address provider are not specified,
//            // the default host 127.0.0.1 and port 3301 are used
//            .withAddress(tarantoolProps.host, tarantoolProps.port)
//            // For connecting to a Cartridge application, use the value of cluster_cookie parameter in the init.lua file
//            .withCredentials(tarantoolProps.username, tarantoolProps.password)
//            // Specify using the default CRUD proxy operations mapping configuration
//            .withProxyMethodMapping()
//            // You may also specify more client settings, such as:
//            // timeouts, number of connections, custom MessagePack entities to Java objects mapping, etc.
//            .build();
//    }
//
//
//}


@Configuration
@EnableTarantoolRepositories(basePackageClasses = [TarantoolDialogsRepository::class])
class TarantoolConfiguration (
    private val tarantoolProps: TarantoolConfigProps
): AbstractTarantoolDataConfiguration() {

    override fun tarantoolServerAddress(): TarantoolServerAddress {
        return TarantoolServerAddress(tarantoolProps.host, tarantoolProps.port)
    }

    override fun tarantoolCredentials(): TarantoolCredentials {
        return SimpleTarantoolCredentials(tarantoolProps.username, tarantoolProps.password)
    }

    override fun configureClientConfig(builder: TarantoolClientConfig.Builder) {
        builder
            .withConnections(tarantoolProps.connections)
            .withConnectTimeout(tarantoolProps.connectTimeout)
            .withRequestTimeout(tarantoolProps.requestTimeout)
            .withReadTimeout(tarantoolProps.readTimeout)
    }

//    override fun tarantoolClient(
//        tarantoolClientConfig: TarantoolClientConfig,
//        tarantoolClusterAddressProvider: TarantoolClusterAddressProvider
//    ): TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> {
//        return ProxyTarantoolTupleClient(
//            super.tarantoolClient(tarantoolClientConfig, tarantoolClusterAddressProvider)
//        )
//    }

    override fun tarantoolClient(
        tarantoolClientConfig: TarantoolClientConfig,
        tarantoolClusterAddressProvider: TarantoolClusterAddressProvider
    ): TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> {
        return ProxyTarantoolTupleClient(
            super.tarantoolClient(tarantoolClientConfig, tarantoolClusterAddressProvider)
        )
    }

}
