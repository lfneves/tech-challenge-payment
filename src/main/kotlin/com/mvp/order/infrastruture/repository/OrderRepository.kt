package com.mvp.order.infrastruture.repository

import com.mvp.order.infrastruture.entity.OrderEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface OrderRepository : ReactiveCrudRepository<OrderEntity?, Long?> {

    @Query("""
        SELECT tb_order.id, tb_order.external_id, id_client, SUM(price) AS total_price, status, is_finished, waiting_time
         FROM tb_order 
         INNER JOIN tb_client ON tb_client.id = tb_order.id_client
         INNER JOIN tb_order_product ON tb_order_product.id_order = tb_order.id
         INNER JOIN tb_product ON tb_product.id = tb_order_product.id_product
         WHERE tb_client.cpf = $1
         GROUP BY tb_order.id, id_client, status, is_finished
    """)
    fun findByUsername(username: String?): Mono<OrderEntity>

    @Query("""
        SELECT tb_order.id, tb_order.external_id, id_client, SUM(price) AS total_price, status, is_finished, waiting_time
         FROM tb_order 
         INNER JOIN tb_client ON tb_client.id = tb_order.id_client
         INNER JOIN tb_order_product ON tb_order_product.id_order = tb_order.id
         INNER JOIN tb_product ON tb_product.id = tb_order_product.id_product
         WHERE tb_order.id = $1
         GROUP BY tb_order.id, id_client, status, is_finished
    """)
    fun findByIdOrder(id: Long): Mono<OrderEntity>

    @Query("""
        SELECT tb_order.id, tb_order.external_id, id_client, SUM(price) AS total_price, status, is_finished, waiting_time
         FROM tb_order 
         INNER JOIN tb_client ON tb_client.id = tb_order.id_client
         INNER JOIN tb_order_product ON tb_order_product.id_order = tb_order.id
         INNER JOIN tb_product ON tb_product.id = tb_order_product.id_product
         WHERE tb_order.external_id = $1
         GROUP BY tb_order.id, id_client, status, is_finished
    """)
    fun findByExternalId(externalId: UUID): Mono<OrderEntity>

    @Query("""
        SELECT tb_order.id, tb_order.external_id, id_client, SUM(price) AS total_price, status, is_finished, waiting_time
         FROM tb_order 
         INNER JOIN tb_client ON tb_client.id = tb_order.id_client
         INNER JOIN tb_order_product ON tb_order_product.id_order = tb_order.id
         INNER JOIN tb_product ON tb_product.id = tb_order_product.id_product
         GROUP BY tb_order.id, id_client, status, is_finished
    """)
    fun findAllOrder(): Flux<OrderEntity>


    @Query("""
        UPDATE tb_order 
            SET status = :#{#orderEntity.status}
        WHERE id = :#{#orderEntity.id}
    """)
    suspend fun updateStatus(orderEntity: OrderEntity): OrderEntity
}