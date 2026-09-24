package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.PaymentEntity
import id.yukelola.core.domain.model.payment.Payment
import id.yukelola.core.domain.model.payment.PaymentMethod
import id.yukelola.core.domain.model.payment.PaymentTransactionType

/**
 * Lossless mapping functions between canonical domain [Payment] and persistence [PaymentEntity].
 */
fun Payment.toEntity(): PaymentEntity {
    return PaymentEntity(
        id = id,
        businessId = businessId,
        branchId = branchId,
        transactionType = transactionType.name,
        referenceId = referenceId,
        paymentMethod = paymentMethod.name,
        amount = amount,
        createdAt = createdAt
    )
}

fun PaymentEntity.toDomain(): Payment {
    return Payment(
        id = id,
        businessId = businessId,
        branchId = branchId,
        transactionType = PaymentTransactionType.valueOf(transactionType),
        referenceId = referenceId,
        paymentMethod = PaymentMethod.valueOf(paymentMethod),
        amount = amount,
        createdAt = createdAt
    )
}
