package com.mvp.payment.application.bdd

import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.domain.service.payment.PaymentService
import com.mvp.payment.infrastruture.entity.OrderEntity
import com.mvp.payment.infrastruture.repository.OrderRepository
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceSteps {

    @Autowired
    private lateinit var paymentService: PaymentService
    @Autowired
    private lateinit var orderRepository: OrderRepository

    private lateinit var orderResponse: OrderByIdResponseDTO
    private var orderId: Long = 0
    private lateinit var  orderEntity: OrderEntity

    @Given("Entity")
    fun setup() {
        orderEntity = OrderEntity(
            id = "1",
            externalId = "4879d212-bdf1-413c-9fd1-5b65b50257bc",
            idClient = 1,
            totalPrice = BigDecimal.TEN,
            status = "PENDING",
            isFinished = false
        )
    }

    @Given("I have an order with ID {long}")
    fun i_have_an_order_with_id(id: Long) {
        orderId = id
    }

    @Then("Save order Repository")
    fun save_order_repository() {
        orderRepository.save(orderEntity)
    }

    @When("I request the order by this ID")
    fun i_request_the_order_by_this_id() {
        orderResponse = paymentService.getOrderByExternalId(UUID.fromString(orderEntity.externalId))!!
    }

    @Then("I should receive the order details")
    fun i_should_receive_the_order_details() {
        Assertions.assertNotNull(orderResponse)
        Assertions.assertEquals("4879d212-bdf1-413c-9fd1-5b65b50257bc", orderResponse.externalId)
    }
}
