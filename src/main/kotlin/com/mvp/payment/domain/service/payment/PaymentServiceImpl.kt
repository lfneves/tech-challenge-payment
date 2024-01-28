package com.mvp.payment.domain.service.payment

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mvp.payment.domain.model.exception.Exceptions
import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.OrderCheckoutDTO
import com.mvp.payment.domain.model.payment.OrderFinishDTO
import com.mvp.payment.domain.model.payment.enums.PaymentStatusEnum
import com.mvp.payment.domain.model.status.StatusDTO
import com.mvp.payment.domain.service.message.SnsAndSqsService
import com.mvp.payment.infrastruture.repository.OrderRepository
import com.mvp.payment.infrastruture.repository.ProductRepository
import com.mvp.payment.utils.ErrorMsgConstants
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Service
class PaymentServiceImpl(
    private val orderRepository: OrderRepository,
    private val snsAndSqsService: SnsAndSqsService,
    private val productRepository: ProductRepository,
): PaymentService {
    private val logger: Logger = LoggerFactory.getLogger(PaymentServiceImpl::class.java)

    private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    override fun getOrderById(id: Long): OrderByIdResponseDTO {
        logger.info("PaymentServiceImpl - getOrderById")
        val orderEntity = orderRepository.findById(id)
        if(orderEntity.isPresent) {
            return OrderByIdResponseDTO.fromOrderEntityToOrderByIdResponseDTO(orderEntity.get())
        }
        return OrderByIdResponseDTO()
    }


    override fun getOrderByExternalId(externalId: UUID): OrderByIdResponseDTO? {
        logger.info("PaymentServiceImpl - getOrderByExternalId")
        val orderEntity = orderRepository.findByExternalId(externalId.toString())
        if(orderEntity.isPresent) {
            return OrderByIdResponseDTO.fromOrderEntityToOrderByIdResponseDTO(orderEntity.get())
        } else {
            throw Exceptions.NotFoundException(ErrorMsgConstants.ERROR_ORDER_NOT_FOUND)
        }
    }

    override fun fakeCheckoutOrder(orderCheckoutDTO: OrderCheckoutDTO): OrderByIdResponseDTO {
        logger.info("PaymentServiceImpl - fakeCheckoutOrder")
        try {
            val order = orderRepository.findByExternalId(orderCheckoutDTO.externalId)
            if (!order.isPresent) {
                throw Exception(ErrorMsgConstants.ERROR_ORDER_NOT_FOUND)
            } else {
                val randomMinutes = (20..75).random().toLong()
                val updatedOrder = order.get().copy(
                    status = PaymentStatusEnum.PAYMENT_REQUIRED.value,
                    waitingTime = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).plusMinutes(randomMinutes).toLocalDateTime()
                )
                val savedOrder = orderRepository.save(updatedOrder)
                val orderResponse = OrderByIdResponseDTO.fromOrderEntityToOrderByIdResponseDTO(savedOrder)
                orderResponse.products = productRepository.findByExternalId(orderResponse.externalId).toMutableList()
                snsAndSqsService.sendQueueStatusMessage(mapper.writeValueAsString(orderResponse.toStatusDTO()))
                return orderResponse
            }
        } catch (e: Exception) {
            logger.info("ERROR PaymentServiceImpl - fakeCheckoutOrder {}", e.printStackTrace())
            throw Exceptions.RequestedElementNotFoundException(ErrorMsgConstants.ERROR_ORDER_NOT_FOUND)
        }
    }

    override fun finishedOrderWithPayment(orderCheckoutDTO: OrderCheckoutDTO): OrderFinishDTO {
        logger.info("PaymentServiceImpl - fakeCheckoutOrder")
        try {
            val order = orderRepository.findByExternalId(orderCheckoutDTO.externalId)
            if (!order.isPresent) {
                throw Exception(ErrorMsgConstants.ERROR_ORDER_NOT_FOUND)
            } else {
                val updatedOrder = order.get().copy(
                    status = PaymentStatusEnum.FINISHED.value,
                    waitingTime = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime(),
                    isFinished = true
                )
                val savedOrder = orderRepository.save(updatedOrder)
                val orderResponse =  OrderByIdResponseDTO.fromOrderEntityToOrderByIdResponseDTO(savedOrder)
                val statusDTO: StatusDTO = orderResponse.toStatusDTO()
                snsAndSqsService.sendQueueStatusMessage(mapper.writeValueAsString(statusDTO))
                return OrderFinishDTO(
                    isFinished = true,
                    externalId = statusDTO.externalId
                )
            }
        } catch (e: Exception) {
            logger.info("ERROR PaymentServiceImpl - finishedOrderWithPayment {}", e.printStackTrace())
            throw Exception(ErrorMsgConstants.ERROR_ORDER_CHECKOUT)
        }
    }
}