package com.mvp.order.infrastruture.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("tb_order_product")
data class OrderProductEntity(
    @Id @Column("id")
    var id: Long? = null,
    @Column("id_product")
    var idProduct: Long? = null,
    @Column("id_order")
    var idOrder: Long? = null
)

data class OrderProductResponseEntity(
    var id: Long? = null,
    var idProduct: Long? = null,
    var idOrder: Long? = null,
    var productName: String? = null,
    var categoryName: String? = null,
    var price: BigDecimal = BigDecimal.ZERO
)