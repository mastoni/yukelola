package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.SaleItemEntity
import id.yukelola.core.domain.model.sale.SaleItem

/**
 * Lossless mapping functions between canonical domain [SaleItem] and persistence [SaleItemEntity].
 */
fun SaleItem.toEntity(): SaleItemEntity {
    return SaleItemEntity(
        id = id,
        saleId = saleId,
        productId = productId,
        productName = productName,
        unit = unit,
        unitPrice = unitPrice,
        costPrice = costPrice,
        quantity = quantity,
        discountAmount = discountAmount,
        subtotal = subtotal
    )
}

fun SaleItemEntity.toDomain(): SaleItem {
    return SaleItem(
        id = id,
        saleId = saleId,
        productId = productId,
        productName = productName,
        unit = unit,
        unitPrice = unitPrice,
        costPrice = costPrice,
        quantity = quantity,
        discountAmount = discountAmount,
        subtotal = subtotal
    )
}
