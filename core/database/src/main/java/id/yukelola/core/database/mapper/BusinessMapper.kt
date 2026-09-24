package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.BusinessEntity
import id.yukelola.core.domain.model.business.Business

/**
 * Persistence mapper converting between [Business] domain aggregate and [BusinessEntity] Room entity.
 * Backed by canonical Contract v1.3.0 Section 3.
 */
fun Business.toEntity(): BusinessEntity = BusinessEntity(
    id = id,
    legalName = legalName,
    ownerUserId = ownerUserId,
    createdAt = createdAt,
    isActive = isActive
)

fun BusinessEntity.toDomain(): Business = Business(
    id = id,
    legalName = legalName,
    ownerUserId = ownerUserId,
    createdAt = createdAt,
    isActive = isActive
)
