package com.mvp.order.domain.service.payment

import com.mvp.order.domain.model.exception.Exceptions
import com.mvp.order.domain.model.payment.OrderByIdResponseDTO
import com.mvp.order.domain.model.payment.OrderCheckoutDTO
import com.mvp.order.domain.model.payment.enums.PaymentStatusEnum
import com.mvp.order.infrastruture.repository.OrderProductRepository
import com.mvp.order.infrastruture.repository.OrderRepository
import com.mvp.order.utils.ErrorMsgConstants
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Service
class PaymentServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderProductRepository: OrderProductRepository,
): PaymentService {
    var logger: Logger = LoggerFactory.getLogger(PaymentServiceImpl::class.java)

    override fun getOrderById(id: Long): Mono<OrderByIdResponseDTO> {
        return orderRepository.findByIdOrder(id)
            .switchIfEmpty(Mono.error(Exceptions.NotFoundException(ErrorMsgConstants.ERROR_ORDER_NOT_FOUND)))
            .flatMap { it?.toResponseDTO().toMono() }
            .flatMap { order ->
                orderProductRepository.findAllByIdOrderInfo(order.id!!)
                    .collectList()
                    .flatMap {
                        order.products.addAll(it)
                        order.toMono()
                    }.then(Mono.just(order))
            }
    }

    override suspend fun getOrderByExternalId(externalId: UUID): OrderByIdResponseDTO? {
        return orderRepository.findByExternalId(externalId).awaitSingle().toResponseDTO()
    }

    override fun fakeCheckoutOrder(orderCheckoutDTO: OrderCheckoutDTO): Mono<Boolean> {
        return orderRepository.findByIdOrder(orderCheckoutDTO.idOrder)
            .switchIfEmpty(Mono.error(Exceptions.NotFoundException(ErrorMsgConstants.ERROR_ORDER_NOT_FOUND)))
            .doOnNext { setStatus ->
                setStatus.status = PaymentStatusEnum.PAID.value
                val randomMinutes = (20..75).random().toLong()
                val z = ZoneId.of( "America/Sao_Paulo")
                setStatus.waitingTime = ZonedDateTime.now(z).plusMinutes(randomMinutes).toLocalDateTime()
            }.flatMap(orderRepository::save)
            .thenReturn(true)
    }
}