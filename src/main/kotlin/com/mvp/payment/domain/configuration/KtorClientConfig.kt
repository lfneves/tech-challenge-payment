package com.mvp.payment.domain.configuration


import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KtorClientConfig {

    @Bean
    fun ktorHttpClient(): HttpClient {
        return HttpClient() {
            install(ContentNegotiation) {
                jackson()
            }
        }
    }
}

