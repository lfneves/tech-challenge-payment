package com.mvp.payment.domain.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "order.api-endpoints")
class PaymentURLPropertyConfiguration {

    lateinit var qrs: String
}