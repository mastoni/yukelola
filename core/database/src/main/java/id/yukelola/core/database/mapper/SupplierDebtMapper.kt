package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.SupplierDebtEntity
import id.yukelola.core.domain.model.debt.DebtStatus
import id.yukelola.core.domain.model.debt.SupplierDebt

/**
 * Persistence mapper converting between [SupplierDebt] domain model and [SupplierDebtEntity] Room entity.
 * Backed by canonical Contract v1.3.0 Section 3.
 */
fun SupplierDebt.toEntity(): SupplierDebtEntity = SupplierDebtEntity(
    id = id,
    businessId = businessId,
    branchId = branchId,
    supplierId = supplierId,
    purchaseId = purchaseId,
    originalAmount = originalAmount,
    remainingAmount = remainingAmount,
    status = status.name,
    dueDate = dueDate,
    createdAt = createdAt
)

fun SupplierDebtEntity.toDomain(): SupplierDebt = SupplierDebt(
    id = id,
    businessId = businessId,
    branchId = branchId,
    supplierId = supplierId,
    purchaseId = purchaseId,
    originalAmount = originalAmount,
    remainingAmount = remainingAmount,
    status = DebtStatus.valueOf(status),
    dueDate = dueDate,
    createdAt = createdAt
)
