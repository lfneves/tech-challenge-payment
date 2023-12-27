package com.mvp.order.domain.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "order.api-endpoints")
class OrderEndpointPropertyConfiguration {

    lateinit var qrs: String
}