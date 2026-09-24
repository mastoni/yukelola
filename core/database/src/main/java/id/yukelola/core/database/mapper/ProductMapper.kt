package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.ProductEntity
import id.yukelola.core.domain.model.catalog.Product
import id.yukelola.core.domain.model.catalog.ProductType

/**
 * Persistence mapper converting between [Product] domain aggregate and [ProductEntity] Room entity.
 * Backed by canonical Contract v1.3.0 Section 3.
 */
fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    businessId = businessId,
    categoryId = categoryId,
    sku = sku,
    barcode = barcode,
    name = name,
    productType = productType.name,
    baseUnit = baseUnit,
    defaultCostPrice = defaultCostPrice,
    defaultSellingPrice = defaultSellingPrice,
    trackStock = trackStock,
    isActive = isActive
)

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    businessId = businessId,
    categoryId = categoryId,
    sku = sku,
    barcode = barcode,
    name = name,
    productType = ProductType.valueOf(productType),
    baseUnit = baseUnit,
    defaultCostPrice = defaultCostPrice,
    defaultSellingPrice = defaultSellingPrice,
    trackStock = trackStock,
    isActive = isActive
)
