package com.mvp.order.domain.service.payment

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mvp.order.domain.configuration.OrderEndpointPropertyConfiguration
import com.mvp.order.domain.configuration.OrderPropertyConfiguration
import com.mvp.order.domain.model.exception.Exceptions
import com.mvp.order.domain.model.payment.OrderByIdResponseDTO
import com.mvp.order.domain.model.payment.enums.PaymentStatusEnum
import com.mvp.order.domain.model.payment.store.*
import com.mvp.order.domain.model.payment.store.webhook.MerchantOrderDTO
import com.mvp.order.domain.model.payment.store.webhook.MerchantOrderResponseDTO
import com.mvp.order.infrastruture.repository.OrderRepository
import com.mvp.order.utils.ErrorMsgConstants
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.*

@Service
class MPOrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val paymentService: PaymentService,
    private val orderPropertyConfiguration: OrderPropertyConfiguration,
    private val orderEndpointPropertyConfiguration: OrderEndpointPropertyConfiguration
): MPOrderService {

    private val client = WebClient.create()

    override fun checkoutOrder(username: String): Mono<QrDataDTO> {
        return orderRepository.findByUsername(username)
            .switchIfEmpty(Mono.error(Exceptions.NotFoundException(ErrorMsgConstants.ERROR_ORDER_NOT_FOUND)))
            .flatMap {
                paymentService.getOrderById(it.id!!)
                    .flatMap {
                        Mono.just(orderCheckoutGenerateQrs(it))
                    }.map { jsonRequest ->
                        jsonRequest
                    }
            }.flatMap {
                generateOrderQrs(it)
            }
    }

    override suspend fun saveCheckoutOrderExternalStoreID(merchantOrderDTO: MerchantOrderDTO): Mono<Void> {
        val  test = getMerchantOrderByID(merchantOrderDTO.resource).awaitSingle()
        val order = paymentService.getOrderByExternalId(UUID.fromString(test.externalReference))
        order?.status = if (test.orderStatus == "payment_required") PaymentStatusEnum.PAYMENT_REQUIRED.value
            else PaymentStatusEnum.PENDING.value
        orderRepository.updateStatus(order!!.toEntity())
        return Mono.empty()
    }

    override fun getMerchantOrderByID(requestUrl: String): Mono<MerchantOrderResponseDTO> {
        return client.get()
            .uri(requestUrl)
            .header("Authorization", orderPropertyConfiguration.token)
            .retrieve()
            .bodyToMono(MerchantOrderResponseDTO::class.java)
    }

    override fun generateOrderQrs(requestBody: String): Mono<QrDataDTO> {

        val endpoint = orderEndpointPropertyConfiguration.qrs.replace("?", orderPropertyConfiguration.userId)
        val requestUrl = orderPropertyConfiguration.url + endpoint
        val responseSpec = client.put()
            .uri(requestUrl)
            .header("Authorization", orderPropertyConfiguration.token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(QrDataDTO::class.java)

        return responseSpec
    }

    override fun orderCheckoutGenerateQrs(order: OrderByIdResponseDTO): String {
        val mapper = jacksonObjectMapper()
        val products = mutableListOf<ItemDTO>()
        val orderQrsDTO = OrderQrsDTO()

        orderQrsDTO.externalReference = order.externalId.toString()
        orderQrsDTO.title = "${order.id}"
        orderQrsDTO.notificationUrl = orderPropertyConfiguration.notificationUrl
        orderQrsDTO.description = order.status

        order.products.forEach {
            val product = ItemDTO(
                category = it.categoryName!!,
                description = "Test",
                quantity = 1,
                sku_number = "${order.idClient}_${order.id}",
                title = it.productName!!,
                total_amount = 1,
                unit_measure = "unit",
                unit_price = it.price.toInt()
            )
            products.add(product)
        }
        val sponsor = SponsorDTO(id = 57174696)
        orderQrsDTO.totalAmount = products.sumOf { it.total_amount + it.unit_price }
        val cashOut = CashOutDTO(amount = products.sumOf { it.unit_price })
        orderQrsDTO.itemDTOS = products
        orderQrsDTO.sponsorDTO = sponsor
        orderQrsDTO.cashOut = cashOut

        return mapper.writeValueAsString(orderQrsDTO)
    }
}