package com.mvp.payment.domain.model.payment.listener

import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = "products")
data class Product(
        val id: String = "",
        val idProduct: String = "",
        var idOrder: String = "",
        var externalId: String = ""
)