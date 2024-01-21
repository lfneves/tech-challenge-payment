package com.mvp.payment.domain.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient

@Configuration
@ConfigurationProperties(prefix = "aws")
class AwsSnsConfig {

    lateinit var topicArn: String
    lateinit var region: String

    @Bean
    fun snsClient(): SnsClient {
        return SnsClient.builder()
                .region(Region.of(region))
                .build()
    }
}