package com.mvp.payment.domain.model.status

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

data class StatusDTO(
    var externalId: String = "",
    var idClient: Int? = null,
    var totalPrice: BigDecimal = BigDecimal.ZERO,
    var status: String = "",
    var waitingTime: LocalDateTime = ZonedDateTime.now(ZoneId.of( "America/Sao_Paulo")).toLocalDateTime(),
    var isFinished: Boolean = false
)
