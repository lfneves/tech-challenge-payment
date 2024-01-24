package com.mvp.payment.domain.model.payment.store.webhook


import com.fasterxml.jackson.annotation.JsonProperty

data class MerchantOrderResponseDTO(
    @JsonProperty("additional_info")
    var additionalInfo: String? = "",
    @JsonProperty("application_id")
    var applicationId: Any? = null,
    @JsonProperty("cancelled")
    var cancelled: Boolean? = null,
    @JsonProperty("collector")
    var collector: Collector? = null,
    @JsonProperty("date_created")
    var dateCreated: String? = "",
    @JsonProperty("external_reference")
    var externalReference: String? = "",
    @JsonProperty("id")
    var id: Long? = null,
    @JsonProperty("is_test")
    var isTest: Boolean? = null,
    @JsonProperty("items")
    var items: List<Item?>? = null,
    @JsonProperty("last_updated")
    var lastUpdated: String? = null,
    @JsonProperty("marketplace")
    var marketplace: String? = "",
    @JsonProperty("notification_url")
    var notificationUrl: String? = "",
    @JsonProperty("order_status")
    var orderStatus: String? = "",
    @JsonProperty("paid_amount")
    var paidAmount: Int? = null,
    @JsonProperty("payer")
    var payer: Any? = null,
    @JsonProperty("payments")
    var payments: List<Any?>? = null,
    @JsonProperty("payouts")
    var payouts: List<Any?>? = null,
    @JsonProperty("preference_id")
    var preferenceId: String? = "",
    @JsonProperty("refunded_amount")
    var refundedAmount: Int? = null,
    @JsonProperty("shipments")
    var shipments: List<Any?>? = null ,
    @JsonProperty("shipping_cost")
    var shippingCost: Int? = null,
    @JsonProperty("site_id")
    var siteId: String? = "",
    @JsonProperty("sponsor_id")
    var sponsorId: Int? = null,
    @JsonProperty("status")
    var status: String? = "",
    @JsonProperty("total_amount")
    var totalAmount: Int? = null
)