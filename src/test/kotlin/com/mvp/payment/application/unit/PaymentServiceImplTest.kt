package com.mvp.payment.application.unit

import com.mvp.payment.domain.model.exception.Exceptions
import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO.Companion.fromOrderEntityToOrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.OrderCheckoutDTO
import com.mvp.payment.domain.model.payment.listener.Product
import com.mvp.payment.domain.model.payment.store.QrDataDTO
import com.mvp.payment.domain.service.message.SnsAndSqsService
import com.mvp.payment.domain.service.payment.MPOrderServiceImpl
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
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

//@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceImplTest {

//    @Autowired
//    private lateinit var paymentService: PaymentService
//    @Autowired
//    private lateinit var orderRepository: OrderRepository
//    @Autowired
//    private lateinit var productRepository: ProductRepository
//
//    @Autowired
//    private lateinit var mpOrderServiceImpl: MPOrderServiceImpl

    private var paymentService: PaymentService = mockk(relaxed = true)
    private val orderRepository: OrderRepository = mockk(relaxed = true)
    private val productRepository: ProductRepository = mockk(relaxed = true)
    private val mpOrderServiceImpl: MPOrderServiceImpl = mockk(relaxed = true)

    private val snsAndSqsService: SnsAndSqsService = mockk<SnsAndSqsService>()

    private lateinit var orderEntity: OrderEntity

    private val externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc"

    @BeforeEach
    fun setUp() {
        paymentService = PaymentServiceImpl(orderRepository, snsAndSqsService, productRepository)

        orderEntity = OrderEntity(
            id = "1",
            externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc",
            idClient = 1,
            totalPrice = BigDecimal.TEN,
            status = "PENDING",
            waitingTime = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime(),
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
        every { orderRepository.save(orderEntity) } returns orderEntity
        orderRepository.save(orderEntity)
        every { orderRepositoryMockk.findByExternalId(any()) } returns Optional.of(orderEntity)

        assertThrows<Exception> {
            paymentService.getOrderByExternalId(UUID.fromString(externalId))
        }
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

        every { orderRepository.findByExternalId(any()) } returns Optional.of(orderEntity)

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
        every { orderRepository.save(orderEntity) } returns orderEntity
        val orderResponse = orderRepository.save(orderEntity)
        orderResponse.waitingTime = null

        val orderCheckoutDTO = OrderCheckoutDTO(externalId = orderEntity.externalId)

        every { orderRepository.save(any()) } returns orderEntity
        every { orderRepository.findByExternalId(orderCheckoutDTO.externalId) } returns Optional.of(orderEntity)
        every { snsAndSqsService.sendQueueStatusMessage(any()) }just runs

        val result = paymentService.fakeCheckoutOrder(orderCheckoutDTO)

        assertNotNull(result)
        assertEquals("PENDING", result.status)
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
        every { orderRepository.save(any()) } returns orderEntity

        val orderCheckoutDTO = OrderCheckoutDTO(externalId = orderEntity.externalId)

        every {  orderRepository.findByExternalId(any()) } returns Optional.of(orderEntity)

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

    @Test
    fun `test checkoutOrder with present orderEntity`() = runBlocking{
        val orderRepository = mockk<OrderRepository>()
        val productRepository = mockk<ProductRepository>()
        val mpOrderServiceImpl = mockk<MPOrderServiceImpl>()

        val orderEntity = Optional.of(orderEntity)
        val products = listOf(Product(externalId = externalId))
        val expectedQrDataDTO = QrDataDTO()

        every { orderRepository.findByExternalId(any()) } returns orderEntity
        every { productRepository.findByExternalId(externalId) } returns products
        coEvery { mpOrderServiceImpl.generateOrderQrs(any()) } returns expectedQrDataDTO
        coEvery { mpOrderServiceImpl.checkoutOrder(externalId) } returns QrDataDTO()

        val result = mpOrderServiceImpl.checkoutOrder(externalId)

        assertEquals(expectedQrDataDTO, result)
    }

    @Test
    fun `test checkoutOrder with absent orderEntity`(): Unit = runBlocking{
        val orderRepository = mockk<OrderRepository>(relaxed = true)

        val externalId = "some-external-id"
        val orderEntity = Optional.empty<OrderEntity>()

        every { orderRepository.findByExternalId(any()) } returns orderEntity

        mpOrderServiceImpl.checkoutOrder(externalId)

    }

//    @Test
//    fun `test orderCheckoutGenerateQrs totalAmount calculation`() {
//        val orderDTO = OrderDTO(externalId = externalId, totalPrice = BigDecimal.ONE)
//
//        val expectedTotalAmount = 0
//        val resultJson = mpOrderServiceImpl.orderCheckoutGenerateQrs(orderDTO)
//        val resultObject = jacksonObjectMapper().readValue(resultJson, OrderQrsDTO::class.java)
//
//        assertEquals(expectedTotalAmount, resultObject.totalAmount)
//    }

    @Test
    fun `test getOrderById with present orderEntity`() {
        val orderRepository: OrderRepository = mockk(relaxed = true)
        val paymentService: PaymentService = mockk(relaxed = true)
        val id = 123L
        val expectedResponse = fromOrderEntityToOrderByIdResponseDTO(orderEntity)

        every { orderRepository.findById(id) } returns Optional.of(orderEntity)
        every { paymentService.getOrderById(id) } returns fromOrderEntityToOrderByIdResponseDTO(orderEntity)

        val result = paymentService.getOrderById(id)

        assertEquals(expectedResponse.externalId, result.externalId)
    }

    @Test
    fun `test getOrderById with absent orderEntity`() {
        val orderRepository: OrderRepository = mockk(relaxed = true)
        val id = 123L
        every { orderRepository.findById(id) } returns Optional.empty()

        val result = paymentService.getOrderById(id)

        assertEquals(OrderByIdResponseDTO().externalId, result.externalId)
    }
}
