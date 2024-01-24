package com.mvp.payment.domain.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sqs.SqsClient

@Configuration
@ConfigurationProperties(prefix = "aws")
class AwsConfig {

    lateinit var topicArn: String
    lateinit var statusQueue: String
    lateinit var region: String

    @Bean
    fun snsClient(): SnsClient {
        return SnsClient.builder()
                .region(Region.of(region))
                .build()
    }

    @Bean
    fun sqsClient(): SqsClient {
        return SqsClient.builder()
            .region(Region.of(region))
            .build()
    }
}