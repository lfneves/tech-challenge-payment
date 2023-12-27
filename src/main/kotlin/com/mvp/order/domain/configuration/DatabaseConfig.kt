package com.mvp.order.domain.configuration

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories
@Profile("production")
class DatabaseConfig : AbstractR2dbcConfiguration() {

    @Value("\${datasource.host}")
    private val host: String? = null

    @Value("\${datasource.port}")
    private val port: Int? = null

    @Value("\${datasource.user}")
    private val user: String? = null

    @Value("\${datasource.password}")
    private val password: String? = null

    @Value("\${datasource.database}")
    private val database: String? = null

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        return PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(host!!)
                //.host("localhost")
                .port(port!!)
                .username(user!!)
                .password(password)
                .database(database)
                .build()
        )
    }
}