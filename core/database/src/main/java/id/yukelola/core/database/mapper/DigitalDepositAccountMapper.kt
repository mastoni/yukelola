package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.DigitalDepositAccountEntity
import id.yukelola.core.domain.model.digital.DigitalDepositAccount

/**
 * Persistence mapper converting between [DigitalDepositAccount] domain model and [DigitalDepositAccountEntity] Room entity.
 * Backed by canonical Contract v1.3.0 and Financial Effect Matrix Section 1 & 3.
 */
fun DigitalDepositAccount.toEntity(): DigitalDepositAccountEntity = DigitalDepositAccountEntity(
    id = id,
    businessId = businessId,
    branchId = branchId,
    currentBalance = currentBalance,
    updatedAt = updatedAt
)

fun DigitalDepositAccountEntity.toDomain(): DigitalDepositAccount = DigitalDepositAccount(
    id = id,
    businessId = businessId,
    branchId = branchId,
    currentBalance = currentBalance,
    updatedAt = updatedAt
)
