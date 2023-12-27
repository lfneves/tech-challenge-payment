package com.mvp.order.domain.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "order.mp-api")
class OrderPropertyConfiguration {

    lateinit var token: String
    lateinit var userId: String
    lateinit var sponsorId: String
    lateinit var url: String
    lateinit var notificationUrl: String
}