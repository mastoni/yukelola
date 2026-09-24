package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.DebtPaymentEntity
import id.yukelola.core.domain.model.debt.DebtPayment
import id.yukelola.core.domain.model.debt.DebtType
import id.yukelola.core.domain.model.payment.PaymentMethod

/**
 * Persistence mapper converting between [DebtPayment] domain model and [DebtPaymentEntity] Room entity.
 * Backed by canonical Contract v1.3.0 Section 3.
 */
fun DebtPayment.toEntity(): DebtPaymentEntity = DebtPaymentEntity(
    id = id,
    businessId = businessId,
    branchId = branchId,
    debtType = debtType.name,
    debtId = debtId,
    amount = amount,
    paymentMethod = paymentMethod.name,
    notes = notes,
    createdAt = createdAt
)

fun DebtPaymentEntity.toDomain(): DebtPayment = DebtPayment(
    id = id,
    businessId = businessId,
    branchId = branchId,
    debtType = DebtType.valueOf(debtType),
    debtId = debtId,
    amount = amount,
    paymentMethod = PaymentMethod.valueOf(paymentMethod),
    notes = notes,
    createdAt = createdAt
)
