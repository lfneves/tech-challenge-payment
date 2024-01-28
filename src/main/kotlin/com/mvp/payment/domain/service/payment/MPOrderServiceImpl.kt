package com.mvp.payment.domain.service.payment

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mvp.payment.domain.configuration.KtorClientConfig
import com.mvp.payment.domain.configuration.OrderPropertyConfiguration
import com.mvp.payment.domain.configuration.PaymentURLPropertyConfiguration
import com.mvp.payment.domain.model.exception.Exceptions
import com.mvp.payment.domain.model.payment.enums.PaymentStatusEnum
import com.mvp.payment.domain.model.payment.listener.OrderDTO
import com.mvp.payment.domain.model.payment.store.*
import com.mvp.payment.domain.model.payment.store.webhook.MerchantOrderDTO
import com.mvp.payment.domain.model.payment.store.webhook.MerchantOrderResponseDTO
import com.mvp.payment.infrastruture.entity.OrderEntity
import com.mvp.payment.infrastruture.repository.OrderRepository
import com.mvp.payment.infrastruture.repository.ProductRepository
import com.mvp.payment.utils.ErrorMsgConstants
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class MPOrderServiceImpl(
    private val ktorClientConfig: KtorClientConfig,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val paymentService: PaymentService,
    private val orderPropertyConfiguration: OrderPropertyConfiguration,
    private val paymentURLPropertyConfiguration: PaymentURLPropertyConfiguration
): MPOrderService {
    private val logger: Logger = LoggerFactory.getLogger(MPOrderServiceImpl::class.java)

    private val ktorHttpClient = ktorClientConfig.ktorHttpClient()

    override suspend fun checkoutOrder(externalId: String): QrDataDTO {
        val orderEntity = orderRepository.findByExternalId(externalId)
        if(orderEntity.isPresent) {
            val orderDTO = OrderDTO.mapOrderEntityToDTO(orderEntity.get())
            orderDTO.productList = productRepository.findByExternalId(orderDTO.externalId)

            val jsonRequest = orderCheckoutGenerateQrs(orderDTO)
            return generateOrderQrs(jsonRequest)
        } else {
            throw Exceptions.RequestedElementNotFoundException(ErrorMsgConstants.ERROR_ORDER_NOT_FOUND)
        }
    }

    override suspend fun saveCheckoutOrderExternalStoreID(merchantOrderDTO: MerchantOrderDTO) {
        val  merchantOrderID = getMerchantOrderByID(merchantOrderDTO.resource)
        val order = paymentService.getOrderByExternalId(UUID.fromString(merchantOrderID.externalReference))
        order?.status = if (merchantOrderID.orderStatus == "payment_required") PaymentStatusEnum.PAYMENT_REQUIRED.value
            else PaymentStatusEnum.PENDING.value
        orderRepository.save(OrderEntity.fromOrderByIdResponseDTO(order!!))
    }

    suspend fun getMerchantOrderByID(requestUrl: String): MerchantOrderResponseDTO {
        return ktorHttpClient.get(requestUrl) {
            header(HttpHeaders.Authorization, orderPropertyConfiguration.token)
            accept(ContentType.Application.Json)
        }.body()
    }

    override suspend fun generateOrderQrs(requestBody: String): QrDataDTO {
        val endpoint = paymentURLPropertyConfiguration.qrs.replace("?", orderPropertyConfiguration.userId)
        val requestUrl = orderPropertyConfiguration.url + endpoint

        val response : QrDataDTO = ktorHttpClient.post(requestUrl) {
            header(HttpHeaders.Authorization, orderPropertyConfiguration.token)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
        return response
    }

    fun orderCheckoutGenerateQrs(order: OrderDTO): String {
        val mapper = jacksonObjectMapper()
        val products = mutableListOf<ItemDTO>()
        val orderQrsDTO = OrderQrsDTO()

        orderQrsDTO.externalReference = order.externalId
        orderQrsDTO.title = "${order.id}"
        orderQrsDTO.notificationUrl = orderPropertyConfiguration.notificationUrl
        orderQrsDTO.description = order.status

        order.productList.forEach {
            val product = ItemDTO(
                category = "",
                description = "Test",
                quantity = 1,
                sku_number = "${order.idClient}_${order.id}",
                title = "",
                total_amount = 1,
                unit_measure = "unit",
                unit_price = 1
            )
            products.add(product)
        }
        val sponsor = SponsorDTO(id = 57174696)
        val cashOut = CashOutDTO(amount = 0)
        orderQrsDTO.totalAmount = products.sumOf { it.total_amount + cashOut.amount }
        orderQrsDTO.itemDTOS = products
        orderQrsDTO.sponsorDTO = sponsor
        orderQrsDTO.cashOut = cashOut

        logger.info("orderCheckoutGenerateQrs {}", mapper.writeValueAsString(orderQrsDTO))
        return mapper.writeValueAsString(orderQrsDTO)
    }
}