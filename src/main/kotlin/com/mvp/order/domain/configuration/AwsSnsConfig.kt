package com.mvp.order.domain.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.sns.SnsClient

@Configuration
@ConfigurationProperties(prefix = "order.sns")
class AwsSnsConfig {

    lateinit var topicArn: String

    @Bean
    fun snsClient(): SnsClient = SnsClient.builder().build()
}