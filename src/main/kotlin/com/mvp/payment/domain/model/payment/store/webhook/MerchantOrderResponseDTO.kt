package com.mvp.payment.domain.model.payment.store.webhook


import com.fasterxml.jackson.annotation.JsonProperty

data class MerchantOrderResponseDTO(
    @JsonProperty("additional_info")
    var additionalInfo: String?,
    @JsonProperty("application_id")
    var applicationId: Any?,
    @JsonProperty("cancelled")
    var cancelled: Boolean?,
    @JsonProperty("collector")
    var collector: Collector?,
    @JsonProperty("date_created")
    var dateCreated: String?,
    @JsonProperty("external_reference")
    var externalReference: String?,
    @JsonProperty("id")
    var id: Long?,
    @JsonProperty("is_test")
    var isTest: Boolean?,
    @JsonProperty("items")
    var items: List<Item?>?,
    @JsonProperty("last_updated")
    var lastUpdated: String?,
    @JsonProperty("marketplace")
    var marketplace: String?,
    @JsonProperty("notification_url")
    var notificationUrl: String?,
    @JsonProperty("order_status")
    var orderStatus: String?,
    @JsonProperty("paid_amount")
    var paidAmount: Int?,
    @JsonProperty("payer")
    var payer: Any?,
    @JsonProperty("payments")
    var payments: List<Any?>?,
    @JsonProperty("payouts")
    var payouts: List<Any?>?,
    @JsonProperty("preference_id")
    var preferenceId: String?,
    @JsonProperty("refunded_amount")
    var refundedAmount: Int?,
    @JsonProperty("shipments")
    var shipments: List<Any?>?,
    @JsonProperty("shipping_cost")
    var shippingCost: Int?,
    @JsonProperty("site_id")
    var siteId: String?,
    @JsonProperty("sponsor_id")
    var sponsorId: Int?,
    @JsonProperty("status")
    var status: String?,
    @JsonProperty("total_amount")
    var totalAmount: Int?
)