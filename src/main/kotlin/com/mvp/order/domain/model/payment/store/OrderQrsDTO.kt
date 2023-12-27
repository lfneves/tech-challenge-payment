package com.mvp.order.domain.model.payment.store

import com.fasterxml.jackson.annotation.JsonProperty

data class OrderQrsDTO(
    @JsonProperty("cash_out")
    var cashOut: CashOutDTO = CashOutDTO(),
    var description: String = "",
    @JsonProperty("external_reference")
    var externalReference: String = "",
    @JsonProperty("items")
    var itemDTOS: List<ItemDTO> = listOf(),
    @JsonProperty("notification_url")
    var notificationUrl: String? = "",
    @JsonProperty("sponsor")
    var sponsorDTO: SponsorDTO = SponsorDTO(),
    var title: String = "",
    @JsonProperty("total_amount")
    var totalAmount: Int = 0
)