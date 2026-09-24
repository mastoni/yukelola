package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.BranchProductOverrideEntity
import id.yukelola.core.domain.model.catalog.BranchProductOverride

/**
 * Persistence mapper converting between [BranchProductOverride] domain model and [BranchProductOverrideEntity] Room entity.
 * Backed by canonical Contract v1.3.0 Section 3.
 */
fun BranchProductOverride.toEntity(): BranchProductOverrideEntity = BranchProductOverrideEntity(
    branchId = branchId,
    productId = productId,
    stock = stock,
    minStock = minStock,
    localCostPrice = localCostPrice,
    localSellingPrice = localSellingPrice,
    isAvailable = isAvailable
)

fun BranchProductOverrideEntity.toDomain(): BranchProductOverride = BranchProductOverride(
    branchId = branchId,
    productId = productId,
    stock = stock,
    minStock = minStock,
    localCostPrice = localCostPrice,
    localSellingPrice = localSellingPrice,
    isAvailable = isAvailable
)
