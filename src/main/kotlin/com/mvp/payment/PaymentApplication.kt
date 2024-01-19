package com.mvp.payment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories
class PaymentApplication

fun main(args: Array<String>) {
    runApplication<PaymentApplication>(*args)
}