package com.mvp.payment.domain.service.payment

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mvp.payment.domain.configuration.OrderPropertyConfiguration
import com.mvp.payment.domain.configuration.PaymentURLPropertyConfiguration
import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.enums.PaymentStatusEnum
import com.mvp.payment.domain.model.payment.store.*
import com.mvp.payment.domain.model.payment.store.webhook.MerchantOrderDTO
import com.mvp.payment.domain.model.payment.store.webhook.MerchantOrderResponseDTO
import com.mvp.payment.infrastruture.repository.OrderRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.util.*

@Service
class MPOrderServiceImpl(
    private val httpClient: HttpClient,
    private val orderRepository: OrderRepository,
    private val paymentService: PaymentService,
    private val orderPropertyConfiguration: OrderPropertyConfiguration,
    private val paymentURLPropertyConfiguration: PaymentURLPropertyConfiguration
): MPOrderService {

    override suspend fun checkoutOrder(username: String): QrDataDTO {
        val order = orderRepository.findByUsername(username)
        val paymentOrder = paymentService.getOrderById(order.id!!)
        val jsonRequest = orderCheckoutGenerateQrs(paymentOrder)
        return generateOrderQrs(jsonRequest)
    }

    override suspend fun saveCheckoutOrderExternalStoreID(merchantOrderDTO: MerchantOrderDTO) {
        val  merchantOrderID = getMerchantOrderByID(merchantOrderDTO.resource)
        val order = paymentService.getOrderByExternalId(UUID.fromString(merchantOrderID.externalReference))
        order?.status = if (merchantOrderID.orderStatus == "payment_required") PaymentStatusEnum.PAYMENT_REQUIRED.value
            else PaymentStatusEnum.PENDING.value
        orderRepository.updateStatus(order)
    }

    override suspend fun getMerchantOrderByID(requestUrl: String): MerchantOrderResponseDTO {
        return httpClient.get(requestUrl) {
            header(HttpHeaders.Authorization, orderPropertyConfiguration.token)
            accept(ContentType.Application.Json)
        }.body()
    }

    override suspend fun generateOrderQrs(requestBody: String): QrDataDTO {
        val endpoint = paymentURLPropertyConfiguration.qrs.replace("?", orderPropertyConfiguration.userId)
        val requestUrl = orderPropertyConfiguration.url + endpoint

        return withContext(Dispatchers.IO) {
            httpClient.put(requestUrl) {
                header(HttpHeaders.Authorization, orderPropertyConfiguration.token)
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(requestBody)
            }.body()
        }
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