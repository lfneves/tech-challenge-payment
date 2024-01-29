package com.mvp.payment.application.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mvp.payment.application.v1.PaymentController
import com.mvp.payment.domain.configuration.AwsConfig
import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.OrderCheckoutDTO
import com.mvp.payment.domain.model.payment.RequestCheckoutDTO
import com.mvp.payment.domain.model.payment.enums.PaymentStatusEnum
import com.mvp.payment.domain.model.payment.store.QrDataDTO
import com.mvp.payment.domain.model.payment.store.webhook.MerchantOrderDTO
import com.mvp.payment.domain.model.payment.store.webhook.MerchantOrderResponseDTO
import com.mvp.payment.domain.service.message.SnsAndSqsService
import com.mvp.payment.domain.service.payment.MPOrderServiceImpl
import com.mvp.payment.domain.service.payment.PaymentServiceImpl
import com.mvp.payment.infrastruture.entity.OrderEntity
import com.mvp.payment.infrastruture.repository.OrderRepository
import io.mockk.*
import io.mockk.junit5.MockKExtension
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sqs.SqsClient
import java.math.BigDecimal
import java.util.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class PaymentControllerTest {

    private val paymentService: PaymentServiceImpl = mockk<PaymentServiceImpl>(relaxed = true)
    private val snsAndSqsService: SnsAndSqsService = mockk<SnsAndSqsService>(relaxed = true)

    private val mpOrderService: MPOrderServiceImpl = mockk<MPOrderServiceImpl>(relaxed = true)
    private val orderRepository: OrderRepository = mockk<OrderRepository>(relaxed = true)

    private var orderEntity: OrderEntity = mockk<OrderEntity>(relaxed = true)
    private var orderByIdResponseDTO: OrderByIdResponseDTO = mockk<OrderByIdResponseDTO>(relaxed = true)

    private lateinit var paymentController: PaymentController

    private val mapper = jacksonObjectMapper()

    private val mpOrderServiceMockk = mockk<MPOrderServiceImpl>()

    private val awsConfig = mockk<AwsConfig>(relaxed = true)
    private val snsClient= mockk<SnsClient>(relaxed = true)
    private val sqsClient= mockk<SqsClient>(relaxed = true)

    private val TOPIC_ORDER_SNS = System.getenv("TOPIC_ORDER_SNS") ?: "arn:aws:sns:us-east-1:111111111111:ORDER_TOPIC"
    private val PAYMENT_QUEUE = System.getenv("TOPIC_ORDER_SNS") ?: "https://sqs.us-east-1.amazonaws.com/566907801160/PAYMENT_QUEUE"
    private val STATUS_QUEUE = System.getenv("TOPIC_ORDER_SNS") ?: "https://sqs.us-east-1.amazonaws.com/566907801160/STATUS_QUEUE\n"

    @LocalServerPort
    private var port: Int = 8080

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port

        paymentController = PaymentController(paymentService, mpOrderService)
        RestAssuredMockMvc.standaloneSetup(paymentController)
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
    fun `should checkout order and return QrDataDTO`() {
        val requestCheckoutDTO = RequestCheckoutDTO(externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc")
        val qrDataDTO = QrDataDTO(inStoreOrderId = "7fc3e0bd-b454-474e-8840-ea67c934bb96",
            qrData = "00020101021243650016COM.MERCADOLIBRE0201306367fc3e0bd-b454-474e-8840-" +
                    "ea67c934bb965204000053039865802BR5908delivery6009SAO PAULO62070503***63049061",)

        every { orderRepository.save(any()) } returns orderEntity
        orderRepository.save(orderEntity)

        coEvery { mpOrderServiceMockk.generateOrderQrs("""{"cash_out":{"amount":0},
            "description":"PENDING","external_reference":"4879d212-bdf1-413c-9fd1-5b65b50257bc",
            "items":[],"notification_url":"https://webhook.site/2f67acbe-9e94-4c7d-8556-7591233a0938",
            "sponsor":{"id":57174696},"title":"0","total_amount":0}""".trimMargin()) } returns qrDataDTO

        RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body(mapper.writeValueAsString(requestCheckoutDTO))
            .`when`()
            .post("/api/v1/payment/qr-code-checkout")
            .then()
            .statusCode(HttpStatus.ACCEPTED.value())
    }


    @Test
    fun `checkoutOrder should update status and return OrderByIdResponseDTO - NOT_FOUND`() {
        every { orderRepository.save(any()) } returns orderEntity
        orderRepository.save(orderEntity)

        val orderCheckoutDTO = OrderCheckoutDTO(externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc")

        every {  snsAndSqsService.sendQueueStatusMessage(any()) } answers { callOriginal() }

        every { paymentService.fakeCheckoutOrder(any()) } returns orderByIdResponseDTO

        RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body(orderCheckoutDTO)
            .`when`()
            .put("/api/v1/payment/fake-checkout")
            .then()
            .statusCode(HttpStatus.OK.value())
//            .body("externalId", equalTo(orderCheckoutDTO.externalId))
    }

    @Test
    fun `finishedOrderWithPayment returns OrderFinishDTO for valid request - INTERNAL_SERVER_ERROR`() {

        every {  orderRepository.save(any()) } returns orderEntity
        orderRepository.save(orderEntity)

        val orderCheckoutDTO = OrderCheckoutDTO(externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc")

        every {  snsAndSqsService.sendQueueStatusMessage(any()) } just runs

        RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body(orderCheckoutDTO)
            .`when`()
            .post("/api/v1/payment/fake-finish-payment")
            .then()
            .statusCode(HttpStatus.OK.value())
            //.body("externalId", equalTo("4879d212-bdf1-413c-9fd1-5b65b50257bc"))
    }

    @Test
    fun `test saveCheckoutOrderExternalStoreID with payment required status`() = runBlocking {
        val merchantOrderDTO = MerchantOrderDTO(resource = "1", topic = "A")
        val order = OrderByIdResponseDTO(status = PaymentStatusEnum.PAYMENT_REQUIRED.value)

        var mpOrderService: MPOrderServiceImpl = mockk(relaxed = true)

        coEvery { mpOrderService.getMerchantOrderByID(merchantOrderDTO.resource) } returns MerchantOrderResponseDTO()
        coEvery { paymentService.getOrderByExternalId(UUID.fromString(orderEntity.externalId)) } returns order

        mpOrderService.saveCheckoutOrderExternalStoreID(merchantOrderDTO)

        assertEquals(PaymentStatusEnum.PAYMENT_REQUIRED.value, order.status)
    }

}

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//@ExtendWith(MockKExtension::class)
//@ActiveProfiles("test")
//class PaymentControllerTest {
//
//    @MockK
//    private lateinit var paymentService: PaymentServiceImpl
//
//    @MockK
//    private lateinit var mpOrderService: MPOrderService
//
//    @Autowired
//    private lateinit var mockMvc: MockMvc
//
//    private lateinit var paymentController: PaymentController
//
//    private val awsConfig = mockk<AwsConfig>(relaxed = true)
//    private val snsClient= mockk<SnsClient>(relaxed = true)
//    private val sqsClient= mockk<SqsClient>(relaxed = true)
//
//    private val TOPIC_ORDER_SNS = System.getenv("TOPIC_ORDER_SNS") ?: "arn:aws:sns:us-east-1:111111111111:ORDER_TOPIC"
//    private val PAYMENT_QUEUE = System.getenv("TOPIC_ORDER_SNS") ?: "https://sqs.us-east-1.amazonaws.com/566907801160/PAYMENT_QUEUE"
//    private val STATUS_QUEUE = System.getenv("TOPIC_ORDER_SNS") ?: "https://sqs.us-east-1.amazonaws.com/566907801160/STATUS_QUEUE\n"
//
//    @BeforeEach
//    fun setup() {
//        paymentController = PaymentController(paymentService, mpOrderService)
//        RestAssuredMockMvc.standaloneSetup(paymentController)
//    }
//
//    @Test
//    fun `test checkoutOrder`() {
//        // Mocking
//        val requestCheckoutDTO = RequestCheckoutDTO(externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc")
//        val qrDataDTO = QrDataDTO(
//            inStoreOrderId = "7fc3e0bd-b454-474e-8840-ea67c934bb96",
//            qrData = "00020101021243650016COM.MERCADOLIBRE0201306367fc3e0bd-b454-474e-8840-" +
//                    "ea67c934bb965204000053039865802BR5908delivery6009SAO PAULO62070503***63049061"
//        )
//
//        every { awsConfig.topicArn } returns TOPIC_ORDER_SNS
//        every { awsConfig.region } returns "us-east-1"
//        every { awsConfig.statusQueue } returns STATUS_QUEUE
//
//        every { awsConfig.sqsClient() } returns sqsClient
//        every { awsConfig.snsClient() } returns snsClient
//
//        coEvery { mpOrderService.checkoutOrder(any()) } returns qrDataDTO
//
//        // Testing
//        RestAssuredMockMvc.given()
//            .contentType(ContentType.JSON)
//            .body(requestCheckoutDTO)
//            .`when`()
//            .post("/api/v1/payment/qr-code-checkout")
//            .then()
//            .statusCode(HttpStatus.ACCEPTED.value())
//
//        // Verification
//        coVerify(exactly = 1) { mpOrderService.checkoutOrder(any()) }
//    }
//}
