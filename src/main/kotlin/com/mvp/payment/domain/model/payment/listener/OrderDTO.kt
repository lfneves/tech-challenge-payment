package com.mvp.payment.domain.model.payment.listener

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class OrderDTO(
        val id: Long = 0,
        val externalId: String = "",
        val idClient: Int = 0,
        val totalPrice: Double = 0.0,
        val status: String = "",
        @JsonProperty("waitingTime")
        private val waitingTimeRaw: List<Int> = emptyList(),
        val productList: List<Product> = emptyList(),
        val finished: Boolean = false
) {
        val waitingTime: LocalDateTime
                get() = LocalDateTime.of(
                        waitingTimeRaw.getOrElse(0) { 0 },
                        waitingTimeRaw.getOrElse(1) { 1 },
                        waitingTimeRaw.getOrElse(2) { 1 },
                        waitingTimeRaw.getOrElse(3) { 0 },
                        waitingTimeRaw.getOrElse(4) { 0 },
                        waitingTimeRaw.getOrElse(5) { 0 },
                        waitingTimeRaw.getOrElse(6) { 0 }
                )
}