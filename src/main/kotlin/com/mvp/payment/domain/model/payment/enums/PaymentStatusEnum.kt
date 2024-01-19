package com.mvp.payment.domain.model.payment.enums

enum class PaymentStatusEnum(val value: String) {
    PENDING("Pendente"),
    PAYMENT_REQUIRED("Aguardando Pagamento"),
    PREPARING("Preparando"),
    PAID("Pago"),
    FINISHED("Finalizado"),
}