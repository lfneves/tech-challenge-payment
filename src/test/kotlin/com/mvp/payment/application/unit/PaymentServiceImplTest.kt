package com.mvp.payment.application.unit

import com.mvp.payment.domain.model.exception.Exceptions
import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO.Companion.fromOrderEntityToOrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.OrderCheckoutDTO
import com.mvp.payment.domain.model.payment.enums.PaymentStatusEnum
import com.mvp.payment.domain.service.message.SnsAndSqsService
import com.mvp.payment.domain.service.payment.PaymentService
import com.mvp.payment.domain.service.payment.PaymentServiceImpl
import com.mvp.payment.infrastruture.entity.OrderEntity
import com.mvp.payment.infrastruture.repository.OrderRepository
import com.mvp.payment.infrastruture.repository.ProductRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceImplTest {

    @Autowired
    private lateinit var paymentService: PaymentService
    @Autowired
    private lateinit var orderRepository: OrderRepository
    @Autowired
    private lateinit var productRepository: ProductRepository

    private val snsAndSqsService: SnsAndSqsService = mockk<SnsAndSqsService>()

    private lateinit var orderEntity: OrderEntity

    @BeforeEach
    fun setUp() {
        paymentService = PaymentServiceImpl(orderRepository, snsAndSqsService, productRepository)

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
    fun `getOrderById should return OrderByIdResponseDTO`() {
        val orderRepositoryMockk: OrderRepository = mockk<OrderRepository>()
        val externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc"
        val orderEntity = OrderEntity(
            id = "1",
            externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc",
            idClient = 1,
            totalPrice = BigDecimal.TEN,
            status = "PENDING",
            isFinished = false
        )

        orderRepository.save(orderEntity)
        every { orderRepositoryMockk.findByExternalId(externalId) } returns Optional.of(orderEntity)

        val result = paymentService.getOrderByExternalId(UUID.fromString(externalId))
        assertEquals(externalId, result?.externalId)
        assertEquals(orderEntity.externalId, result?.externalId)
    }

    @Test
    fun `getOrderById should return empty OrderByIdResponseDTO`() {
        val orderRepositoryMockk: OrderRepository = mockk<OrderRepository>()
        val orderId = 2L

        every { orderRepositoryMockk.findById(orderId) } returns Optional.empty()

        val result = paymentService.getOrderById(orderId)

        assertEquals("", result.externalId)
    }

    @Test
    fun `Test from OrderEntity To OrderByIdResponseDTO()`() {
        val orderEntity = OrderEntity(
            id = "1",
            externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc",
            idClient = 1,
            totalPrice = BigDecimal.TEN,
            status = "PENDING",
            isFinished = false
        )

        val orderResponseDTO = fromOrderEntityToOrderByIdResponseDTO(orderEntity)

        assertEquals("4879d212-bdf1-413c-9fd1-5b65b50257bc", orderResponseDTO.externalId)
        assertEquals(1, orderResponseDTO.idClient)
        assertEquals(BigDecimal.TEN, orderResponseDTO.totalPrice)
        assertEquals("PENDING", orderResponseDTO.status)
        assertEquals(false, orderResponseDTO.isFinished)
    }

    @Test
    fun `getOrderByExternalId should return OrderByIdResponseDTO`() = runBlocking {
        val orderRepositoryMockk: OrderRepository = mockk<OrderRepository>()

        val externalId = UUID.fromString("4879d212-bdf1-413c-9fd1-5b65b50257bc")
        val orderEntity = orderEntity
        val orderByIdResponseDTO = OrderByIdResponseDTO(
            id = 1,
            externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc",
            idClient = 1,
            totalPrice = BigDecimal.TEN,
            status = "PENDING",
            isFinished = false,
        )
        val slot = slot<String>()

        every { orderRepositoryMockk.findByExternalId(capture(slot)) } returns Optional.of(orderEntity)

        val result = paymentService.getOrderByExternalId(externalId)

        assertEquals(result?.externalId, orderByIdResponseDTO.externalId)
    }

    @Test
    fun `getOrderByExternalId should throw NotFoundException`(): Unit = runBlocking {
        val orderRepositoryMockk: OrderRepository = mockk<OrderRepository>()
        val externalId = UUID.randomUUID()

        every { orderRepositoryMockk.findByExternalId(any()) } returns Optional.empty()

        assertThrows<Exceptions.NotFoundException> {
            paymentService.getOrderByExternalId(externalId)
        }
    }

    @Test
    fun `test fakeCheckoutOrder with valid data`() {
        val orderResponse = orderRepository.save(orderEntity)
        orderResponse.waitingTime = null

        val orderCheckoutDTO = OrderCheckoutDTO(externalId = orderEntity.externalId)

        every { snsAndSqsService.sendQueueStatusMessage(any()) }just runs

        val result = paymentService.fakeCheckoutOrder(orderCheckoutDTO)

        assertNotNull(result)
        assertEquals(PaymentStatusEnum.PAYMENT_REQUIRED.value, result.status)
    }

    @Test
    fun `test fakeCheckoutOrder with exception`() {

        val orderCheckoutDTO = OrderCheckoutDTO(externalId = "123")

        assertThrows<Exception> {
            paymentService.fakeCheckoutOrder(orderCheckoutDTO)
        }
    }

    @Test
    fun `test finishedOrderWithPayment with valid data`() {
        every { snsAndSqsService.sendQueueStatusMessage(any()) } just Runs
        orderRepository.save(orderEntity)

        val orderCheckoutDTO = OrderCheckoutDTO(externalId = orderEntity.externalId)

        val result = paymentService.finishedOrderWithPayment(orderCheckoutDTO)

        assertNotNull(result)
        assertTrue(result.isFinished)
        assertEquals(orderEntity.externalId, result.externalId)
    }

    @Test
    fun `test assertThrows finishedOrderWithPayment with valid data`() {
        val orderCheckoutDTO = OrderCheckoutDTO(externalId = "123")

        assertThrows<Exception> {
            paymentService.finishedOrderWithPayment(orderCheckoutDTO)
        }
    }
}
