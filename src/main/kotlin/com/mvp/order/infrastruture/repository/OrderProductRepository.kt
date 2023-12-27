package com.mvp.order.infrastruture.repository

import com.mvp.order.infrastruture.entity.OrderProductEntity
import com.mvp.order.infrastruture.entity.OrderProductResponseEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrderProductRepository : ReactiveCrudRepository<OrderProductEntity?, Long?> {

    @Query("""
        SELECT tb_order_product.id, id_product, id_order, tb_product.name AS product_name, tb_product.price, 
            tb_category.name AS category_name
        FROM tb_order_product
        INNER JOIN tb_order ON tb_order.id = tb_order_product.id_order
        INNER JOIN tb_product ON tb_product.id = tb_order_product.id_product
        INNER JOIN tb_category ON tb_product.id_category = tb_category.id
        WHERE tb_order_product.id_order = :id
    """)
    fun findAllByIdOrderInfo(id: Long): Flux<OrderProductResponseEntity>

    fun deleteByIdOrder(id: Long): Mono<Void>

    @Query("""
        DELETE FROM tb_order_product WHERE id IN (:ids)
    """)
    fun deleteById(ids: List<Long>): Mono<Void>
}