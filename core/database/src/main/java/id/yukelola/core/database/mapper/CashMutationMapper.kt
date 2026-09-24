package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.CashMutationEntity
import id.yukelola.core.domain.model.cash.CashMutation
import id.yukelola.core.domain.model.cash.CashMutationCategory
import id.yukelola.core.domain.model.cash.CashMutationType

/**
 * Persistence mapper converting between [CashMutation] domain model and [CashMutationEntity] Room entity.
 * Backed by canonical Contract v1.3.0 and Financial Effect Matrix Section 1 & 3.
 */
fun CashMutation.toEntity(): CashMutationEntity = CashMutationEntity(
    id = id,
    businessId = businessId,
    branchId = branchId,
    registerId = registerId,
    cashierSessionId = cashierSessionId,
    mutationType = mutationType.name,
    category = category.name,
    amount = amount,
    source = source,
    referenceId = referenceId,
    notes = notes,
    createdAt = createdAt
)

fun CashMutationEntity.toDomain(): CashMutation = CashMutation(
    id = id,
    businessId = businessId,
    branchId = branchId,
    registerId = registerId,
    cashierSessionId = cashierSessionId,
    mutationType = CashMutationType.valueOf(mutationType),
    category = CashMutationCategory.valueOf(category),
    amount = amount,
    source = source,
    referenceId = referenceId,
    notes = notes,
    createdAt = createdAt
)
