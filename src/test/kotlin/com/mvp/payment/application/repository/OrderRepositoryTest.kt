package com.mvp.payment.application.repository

import com.mvp.payment.infrastruture.entity.OrderEntity
import com.mvp.payment.infrastruture.repository.OrderRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class OrderRepositoryTest {

    private val orderRepository: OrderRepository = mockk()

    private lateinit var orderEntity: OrderEntity

    @BeforeEach
    fun setUp() {
        orderEntity = OrderEntity(
            id = "1",
            externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc",
            idClient = 1,
            totalPrice = BigDecimal.TEN,
            status = "PENDING",
            isFinished = false
        )
    }

    @Test
    fun `test findByExternalId`() {
        val externalId = "some-external-id"
        val optionalOrderEntity = Optional.of(orderEntity)

        // Mocking the behavior
        every { orderRepository.findByExternalId(externalId) } returns optionalOrderEntity

        // Call the method
        val result = orderRepository.findByExternalId(externalId)

        // Verify the result
        assertTrue(result.isPresent)
        assertEquals(orderEntity, result.get())

        // Verify interactions (optional)
        verify(exactly = 1) { orderRepository.findByExternalId(externalId) }
    }
}