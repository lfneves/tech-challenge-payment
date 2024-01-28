package com.mvp.payment.application.v1


import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.OrderCheckoutDTO
import com.mvp.payment.domain.model.payment.OrderFinishDTO
import com.mvp.payment.domain.model.payment.RequestCheckoutDTO
import com.mvp.payment.domain.model.payment.store.QrDataDTO
import com.mvp.payment.domain.model.payment.store.webhook.MerchantOrderDTO
import com.mvp.payment.domain.service.payment.MPOrderService
import com.mvp.payment.domain.service.payment.PaymentServiceImpl
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/payment")
class PaymentController(
    private val paymentService: PaymentServiceImpl,
    private val mpOrderService: MPOrderService
) {

    @PostMapping("/qr-code-checkout")
    @Operation(
        summary = "Efetua o pagamento atualizando os status",
        description = "Efetua o pagamento atualizando os status",
        tags = ["Pedidos"]
    )
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun checkoutOrder(@RequestBody requestCheckoutDTO: RequestCheckoutDTO): ResponseEntity<QrDataDTO> {
        return ResponseEntity.ok(mpOrderService.checkoutOrder(requestCheckoutDTO.externalId))
    }

    @PostMapping("/webhook")
    @Operation(
        summary = "Recebe chamadas do Mercado Pago",
        description = "Efetua atualiza o status de pagamento",
        tags = ["Pedidos"]
    )
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun handlerWebHook(@RequestBody merchantOrderDTO: MerchantOrderDTO): ResponseEntity<Any> {
        return ResponseEntity.ok(mpOrderService.saveCheckoutOrderExternalStoreID(merchantOrderDTO))
    }

    @PutMapping("/fake-checkout")
    @Operation(
        summary = "Efetua o pagamento atualizando os status",
        description = "Efetua o pagamento atualizando os status",
        tags = ["Pedidos"]
    )
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun checkoutOrder(@RequestBody orderCheckoutDTO: OrderCheckoutDTO): ResponseEntity<OrderByIdResponseDTO> {
        return ResponseEntity.ok(paymentService.fakeCheckoutOrder(orderCheckoutDTO))
    }

    @PostMapping("/fake-finish-payment")
    @Operation(
        summary = "Efetua o pagamento atualizando os status",
        description = "Efetua o pagamento atualizando os status",
        tags = ["Pedidos"]
    )
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun finishedOrderWithPayment(@RequestBody orderCheckoutDTO: OrderCheckoutDTO): ResponseEntity<OrderFinishDTO> {
        return ResponseEntity.ok(paymentService.finishedOrderWithPayment(orderCheckoutDTO))
    }
}