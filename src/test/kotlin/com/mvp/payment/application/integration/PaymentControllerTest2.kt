package com.mvp.payment.application.integration

import com.mvp.payment.application.v1.PaymentController
import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.OrderCheckoutDTO
import com.mvp.payment.domain.model.payment.OrderFinishDTO
import com.mvp.payment.domain.model.payment.RequestCheckoutDTO
import com.mvp.payment.domain.model.payment.store.QrDataDTO
import com.mvp.payment.domain.service.payment.MPOrderService
import com.mvp.payment.domain.service.payment.PaymentServiceImpl
import com.mvp.payment.infrastruture.entity.OrderEntity
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.math.BigDecimal

class PaymentControllerTest {

    private lateinit var paymentService: PaymentServiceImpl
    private lateinit var mpOrderService: MPOrderService
    private lateinit var paymentController: PaymentController

    private lateinit var orderEntity: OrderEntity
    private lateinit var orderByIdResponseDTO: OrderByIdResponseDTO

    @BeforeEach
    fun setUp() {
        paymentService = mockk(relaxed = true)
        mpOrderService = mockk(relaxed = true)
        paymentController = PaymentController(paymentService, mpOrderService)

        orderEntity = OrderEntity(
            id = "1",
            externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc",
            idClient = 1,
            totalPrice = BigDecimal.TEN,
            status = "PENDING",
            isFinished = false
        )
        orderByIdResponseDTO = OrderByIdResponseDTO(
            id = 1,
            externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc",
            idClient = 1,
            totalPrice = BigDecimal.TEN,
            status = "PENDING",
            isFinished = false,
        )
    }

    @Test
    fun testCheckoutOrder() = runBlocking {
        val requestCheckoutDTO = RequestCheckoutDTO(externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc")
        val qrDataDTO = QrDataDTO(inStoreOrderId = "7fc3e0bd-b454-474e-8840-ea67c934bb96",
            qrData = "00020101021243650016COM.MERCADOLIBRE0201306367fc3e0bd-b454-474e-8840-" +
                    "ea67c934bb965204000053039865802BR5908delivery6009SAO PAULO62070503***63049061")

        // Mocking
        coEvery { mpOrderService.checkoutOrder(any()) } returns qrDataDTO

        // Call the method
        val response = paymentController.checkoutOrder(requestCheckoutDTO)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(qrDataDTO, response.body)
    }

    @Test
    fun testFakeCheckoutOrder() = runBlocking {
        val orderCheckoutDTO = OrderCheckoutDTO(externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc")
        val expectedResponse = orderByIdResponseDTO

        coEvery { paymentService.fakeCheckoutOrder(orderCheckoutDTO) } returns expectedResponse

        val response = paymentController.checkoutOrder(orderCheckoutDTO)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedResponse, response.body)
    }

    @Test
    fun testFinishedOrderWithPayment() {
        val orderCheckoutDTO =  OrderCheckoutDTO(externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc")
        val expectedResponse = OrderFinishDTO(externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc", isFinished = true)

        every { paymentService.finishedOrderWithPayment(orderCheckoutDTO) } returns expectedResponse

        val response = paymentController.finishedOrderWithPayment(orderCheckoutDTO)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedResponse, response.body)
    }


}