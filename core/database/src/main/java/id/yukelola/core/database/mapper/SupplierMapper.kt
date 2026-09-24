package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.SupplierEntity
import id.yukelola.core.domain.model.actor.Supplier

/**
 * Persistence mapper converting between [Supplier] domain model and [SupplierEntity] Room entity.
 * Backed by canonical Contract v1.3.0 Section 3.
 */
fun Supplier.toEntity(): SupplierEntity = SupplierEntity(
    id = id,
    businessId = businessId,
    name = name,
    phone = phone,
    debtBalance = debtBalance,
    isActive = isActive
)

fun SupplierEntity.toDomain(): Supplier = Supplier(
    id = id,
    businessId = businessId,
    name = name,
    phone = phone,
    debtBalance = debtBalance,
    isActive = isActive
)
