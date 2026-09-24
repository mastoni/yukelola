package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.CashRegisterEntity
import id.yukelola.core.domain.model.cash.CashRegister

/**
 * Persistence mapper converting between [CashRegister] domain model and [CashRegisterEntity] Room entity.
 * Backed by canonical Contract v1.3.0 and Financial Effect Matrix Section 1 & 3.
 */
fun CashRegister.toEntity(): CashRegisterEntity = CashRegisterEntity(
    id = id,
    businessId = businessId,
    branchId = branchId,
    name = name,
    currentBalance = currentBalance,
    updatedAt = updatedAt
)

fun CashRegisterEntity.toDomain(): CashRegister = CashRegister(
    id = id,
    businessId = businessId,
    branchId = branchId,
    name = name,
    currentBalance = currentBalance,
    updatedAt = updatedAt
)
