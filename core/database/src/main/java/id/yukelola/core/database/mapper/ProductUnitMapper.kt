package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.ProductUnitEntity
import id.yukelola.core.domain.model.catalog.ProductUnit

/**
 * Persistence mapper converting between [ProductUnit] domain model and [ProductUnitEntity] Room entity.
 * Backed by canonical Contract v1.3.0 and catalog domain model.
 */
fun ProductUnit.toEntity(): ProductUnitEntity = ProductUnitEntity(
    id = id,
    businessId = businessId,
    name = name,
    symbol = symbol,
    conversionFactor = conversionFactor
)

fun ProductUnitEntity.toDomain(): ProductUnit = ProductUnit(
    id = id,
    businessId = businessId,
    name = name,
    symbol = symbol,
    conversionFactor = conversionFactor
)
