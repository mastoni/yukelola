package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.CategoryEntity
import id.yukelola.core.domain.model.catalog.Category

/**
 * Persistence mapper converting between [Category] domain model and [CategoryEntity] Room entity.
 * Backed by canonical Contract v1.3.0 Section 3.
 */
fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    businessId = businessId,
    name = name,
    color = color,
    icon = icon,
    sortOrder = sortOrder
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    businessId = businessId,
    name = name,
    color = color,
    icon = icon,
    sortOrder = sortOrder
)
