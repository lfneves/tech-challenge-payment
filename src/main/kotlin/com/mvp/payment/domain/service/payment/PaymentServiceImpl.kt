package com.mvp.payment.domain.service.payment

import com.mvp.payment.domain.model.exception.Exceptions
import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.OrderCheckoutDTO
import com.mvp.payment.domain.model.payment.enums.PaymentStatusEnum
import com.mvp.payment.infrastruture.repository.OrderRepository
import com.mvp.payment.utils.ErrorMsgConstants
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Service
class PaymentServiceImpl(
    private val orderRepository: OrderRepository
): PaymentService {
    var logger: Logger = LoggerFactory.getLogger(PaymentServiceImpl::class.java)

    override fun getOrderById(id: Long): OrderByIdResponseDTO {
        logger.info("PaymentServiceImpl - getOrderById")
        val order = orderRepository.findById(id)
        if(order.isPresent) {
            return order.get().toResponseDTO()
        }
        return OrderByIdResponseDTO()
    }


    override suspend fun getOrderByExternalId(externalId: UUID): OrderByIdResponseDTO? {
        logger.info("PaymentServiceImpl - getOrderByExternalId")
        return orderRepository.findByExternalId(externalId.toString()).toResponseDTO()
    }

    override fun fakeCheckoutOrder(orderCheckoutDTO: OrderCheckoutDTO): Boolean {
        logger.info("PaymentServiceImpl - fakeCheckoutOrder")
        try {
            val order = orderRepository.findById(orderCheckoutDTO.idOrder)
            if (!order.isPresent) {
                throw Exceptions.NotFoundException(ErrorMsgConstants.ERROR_ORDER_NOT_FOUND)
            } else {
                val randomMinutes = (20..75).random().toLong()
                val updatedOrder = order.get().copy(
                    status = PaymentStatusEnum.PAID.value,
                    waitingTime = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).plusMinutes(randomMinutes).toLocalDateTime()
                )
                orderRepository.save(updatedOrder)
                return true
            }
        } catch (e: Exception) {
            logger.info("ERROR PaymentServiceImpl - fakeCheckoutOrder {}", e.printStackTrace())
            return false
        }
    }
}