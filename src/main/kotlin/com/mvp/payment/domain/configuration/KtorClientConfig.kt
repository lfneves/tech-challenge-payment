package com.mvp.payment.domain.configuration

import io.ktor.client.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KtorClientConfig {

    @Bean
    fun httpClient(): HttpClient {
        return HttpClient {
        }
    }
}

