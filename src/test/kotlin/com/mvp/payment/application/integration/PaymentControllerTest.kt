package com.mvp.payment.application.integration

//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
//import com.mvp.payment.domain.model.payment.OrderCheckoutDTO
//import com.mvp.payment.domain.model.payment.RequestCheckoutDTO
//import com.mvp.payment.domain.model.payment.enums.PaymentStatusEnum
//import com.mvp.payment.domain.model.payment.store.QrDataDTO
//import com.mvp.payment.domain.model.payment.store.webhook.MerchantOrderDTO
//import com.mvp.payment.domain.model.payment.store.webhook.MerchantOrderResponseDTO
//import com.mvp.payment.domain.service.message.SnsAndSqsService
//import com.mvp.payment.domain.service.payment.MPOrderServiceImpl
//import com.mvp.payment.domain.service.payment.PaymentService
//import com.mvp.payment.infrastruture.entity.OrderEntity
//import com.mvp.payment.infrastruture.repository.OrderRepository
//import io.mockk.*
//import io.restassured.RestAssured
//import io.restassured.RestAssured.given
//import io.restassured.http.ContentType
//import kotlinx.coroutines.runBlocking
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.web.server.LocalServerPort
//import org.springframework.http.HttpStatus
//import org.springframework.test.context.ActiveProfiles
//import org.springframework.test.context.junit.jupiter.SpringExtension
//import java.math.BigDecimal
//import java.util.*
//
//@ExtendWith(SpringExtension::class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@ActiveProfiles("test")
//@AutoConfigureMockMvc
//class PaymentControllerTest {
//
//    private val paymentService: PaymentService = mockk<PaymentService>()
//    private val snsAndSqsService: SnsAndSqsService = mockk<SnsAndSqsService>()
//
//    @Autowired
//    private lateinit var mpOrderService: MPOrderServiceImpl
//    @Autowired
//    private lateinit var orderRepository: OrderRepository
//
//    private lateinit var orderEntity: OrderEntity
//    private lateinit var orderByIdResponseDTO: OrderByIdResponseDTO
//
//    private val mapper = jacksonObjectMapper()
//
//    private val mpOrderServiceMockk = mockk<MPOrderServiceImpl>()
//
//    @LocalServerPort
//    private var port: Int = 8080
//
//    @BeforeEach
//    fun setUp() {
//        MockKAnnotations.init(this)
//        RestAssured.baseURI = "http://localhost"
//        RestAssured.port = port
//        orderEntity = OrderEntity(
//            id = "1",
//            externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc",
//            idClient = 1,
//            totalPrice = BigDecimal.TEN,
//            status = "PENDING",
//            isFinished = false
//        )
//        orderByIdResponseDTO = OrderByIdResponseDTO(
//            id = 1,
//            externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc",
//            idClient = 1,
//            totalPrice = BigDecimal.TEN,
//            status = "PENDING",
//            isFinished = false,
//        )
//    }
//
//    @Test
//    fun `should checkout order and return QrDataDTO`() {
//        val requestCheckoutDTO = RequestCheckoutDTO(externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc")
//        val qrDataDTO = QrDataDTO(inStoreOrderId = "7fc3e0bd-b454-474e-8840-ea67c934bb96",
//            qrData = "00020101021243650016COM.MERCADOLIBRE0201306367fc3e0bd-b454-474e-8840-" +
//                    "ea67c934bb965204000053039865802BR5908delivery6009SAO PAULO62070503***63049061",)
//
//        orderRepository.save(orderEntity)
//
//        coEvery { mpOrderServiceMockk.generateOrderQrs("""{"cash_out":{"amount":0},
//            "description":"PENDING","external_reference":"4879d212-bdf1-413c-9fd1-5b65b50257bc",
//            "items":[],"notification_url":"https://webhook.site/2f67acbe-9e94-4c7d-8556-7591233a0938",
//            "sponsor":{"id":57174696},"title":"0","total_amount":0}""".trimMargin()) } returns qrDataDTO
//
//        given()
//            .contentType(ContentType.JSON)
//            .body(mapper.writeValueAsString(requestCheckoutDTO))
//            .`when`()
//            .post("/api/v1/payment/qr-code-checkout")
//            .then()
//            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
//    }
//
//
//    @Test
//    fun `checkoutOrder should update status and return OrderByIdResponseDTO - NOT_FOUND`() {
//        // Save order before test
//        orderRepository.save(orderEntity)
//
//        val orderCheckoutDTO = OrderCheckoutDTO(externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc")
//
//        every {  snsAndSqsService.sendQueueStatusMessage(any()) } just runs
//
//        every { paymentService.fakeCheckoutOrder(any()) } returns orderByIdResponseDTO
//
//        given()
//            .contentType(ContentType.JSON)
//            .body(orderCheckoutDTO)
//            .`when`()
//            .put("/api/v1/payment/fake-checkout")
//            .then()
//            .statusCode(HttpStatus.NOT_FOUND.value())
////            .body("externalId", equalTo(orderCheckoutDTO.externalId))
//    }
//
//    @Test
//    fun `finishedOrderWithPayment returns OrderFinishDTO for valid request - INTERNAL_SERVER_ERROR`() {
//        // Save order before test
//        orderRepository.save(orderEntity)
//
//        val orderCheckoutDTO = OrderCheckoutDTO(externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc")
//
//        every {  snsAndSqsService.sendQueueStatusMessage(any()) } just runs
//
//        given()
//            .contentType(ContentType.JSON)
//            .body(orderCheckoutDTO)
//            .`when`()
//            .post("/api/v1/payment/fake-finish-payment")
//            .then()
//            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
//            //.body("externalId", equalTo("4879d212-bdf1-413c-9fd1-5b65b50257bc"))
//    }
//
//    @Test
//    fun `test saveCheckoutOrderExternalStoreID with payment required status`() = runBlocking {
//        val merchantOrderDTO = MerchantOrderDTO(resource = "1", topic = "A")
//        val order = OrderByIdResponseDTO(status = PaymentStatusEnum.PAYMENT_REQUIRED.value)
//
//        var mpOrderService: MPOrderServiceImpl = mockk(relaxed = true)
//
//        coEvery { mpOrderService.getMerchantOrderByID(merchantOrderDTO.resource) } returns MerchantOrderResponseDTO()
//        coEvery { paymentService.getOrderByExternalId(UUID.fromString(orderEntity.externalId)) } returns order
//
//        mpOrderService.saveCheckoutOrderExternalStoreID(merchantOrderDTO)
//
//        assertEquals(PaymentStatusEnum.PAYMENT_REQUIRED.value, order.status)
//    }
//
//}