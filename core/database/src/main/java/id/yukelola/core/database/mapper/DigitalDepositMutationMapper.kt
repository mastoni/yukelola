package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.DigitalDepositMutationEntity
import id.yukelola.core.domain.model.digital.DigitalDepositMutation
import id.yukelola.core.domain.model.digital.DigitalDepositMutationType

/**
 * Persistence mapper converting between [DigitalDepositMutation] domain model and [DigitalDepositMutationEntity] Room entity.
 * Backed by canonical Contract v1.3.0 and Financial Effect Matrix Section 1 & 3.
 */
fun DigitalDepositMutation.toEntity(): DigitalDepositMutationEntity = DigitalDepositMutationEntity(
    id = id,
    businessId = businessId,
    branchId = branchId,
    accountId = accountId,
    mutationType = mutationType.name,
    amount = amount,
    balanceBefore = balanceBefore,
    balanceAfter = balanceAfter,
    referenceId = referenceId,
    notes = notes,
    createdAt = createdAt
)

fun DigitalDepositMutationEntity.toDomain(): DigitalDepositMutation = DigitalDepositMutation(
    id = id,
    businessId = businessId,
    branchId = branchId,
    accountId = accountId,
    mutationType = DigitalDepositMutationType.valueOf(mutationType),
    amount = amount,
    balanceBefore = balanceBefore,
    balanceAfter = balanceAfter,
    referenceId = referenceId,
    notes = notes,
    createdAt = createdAt
)
