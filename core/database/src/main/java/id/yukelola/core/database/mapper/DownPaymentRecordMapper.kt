package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.DownPaymentRecordEntity
import id.yukelola.core.domain.model.payment.PaymentMethod
import id.yukelola.core.domain.model.serviceorder.DownPaymentRecord

/**
 * Lossless mapping functions between canonical domain [DownPaymentRecord] and persistence [DownPaymentRecordEntity].
 */
fun DownPaymentRecord.toEntity(): DownPaymentRecordEntity {
    return DownPaymentRecordEntity(
        id = id,
        businessId = businessId,
        branchId = branchId,
        orderId = orderId,
        amount = amount,
        paymentMethod = paymentMethod.name,
        notes = notes,
        createdAt = createdAt
    )
}

fun DownPaymentRecordEntity.toDomain(): DownPaymentRecord {
    return DownPaymentRecord(
        id = id,
        businessId = businessId,
        branchId = branchId,
        orderId = orderId,
        amount = amount,
        paymentMethod = PaymentMethod.valueOf(paymentMethod),
        notes = notes,
        createdAt = createdAt
    )
}
