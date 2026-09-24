package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.CustomerDebtEntity
import id.yukelola.core.domain.model.debt.CustomerDebt
import id.yukelola.core.domain.model.debt.DebtReferenceType
import id.yukelola.core.domain.model.debt.DebtStatus

/**
 * Persistence mapper converting between [CustomerDebt] domain model and [CustomerDebtEntity] Room entity.
 * Backed by canonical Contract v1.3.0 Section 3.
 */
fun CustomerDebt.toEntity(): CustomerDebtEntity = CustomerDebtEntity(
    id = id,
    businessId = businessId,
    branchId = branchId,
    customerId = customerId,
    referenceType = referenceType.name,
    referenceId = referenceId,
    originalAmount = originalAmount,
    remainingAmount = remainingAmount,
    status = status.name,
    dueDate = dueDate,
    createdAt = createdAt
)

fun CustomerDebtEntity.toDomain(): CustomerDebt = CustomerDebt(
    id = id,
    businessId = businessId,
    branchId = branchId,
    customerId = customerId,
    referenceType = DebtReferenceType.valueOf(referenceType),
    referenceId = referenceId,
    originalAmount = originalAmount,
    remainingAmount = remainingAmount,
    status = DebtStatus.valueOf(status),
    dueDate = dueDate,
    createdAt = createdAt
)
