package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.CustomerEntity
import id.yukelola.core.domain.model.actor.Customer

/**
 * Persistence mapper converting between [Customer] domain model and [CustomerEntity] Room entity.
 * Backed by canonical Contract v1.3.0 Section 3.
 */
fun Customer.toEntity(): CustomerEntity = CustomerEntity(
    id = id,
    businessId = businessId,
    branchId = branchId,
    name = name,
    phone = phone,
    debtBalance = debtBalance,
    isActive = isActive
)

fun CustomerEntity.toDomain(): Customer = Customer(
    id = id,
    businessId = businessId,
    branchId = branchId,
    name = name,
    phone = phone,
    debtBalance = debtBalance,
    isActive = isActive
)
