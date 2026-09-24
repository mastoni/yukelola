package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.PurchaseItemEntity
import id.yukelola.core.domain.model.purchase.PurchaseItem

/**
 * Lossless mapping functions between canonical domain [PurchaseItem] and persistence [PurchaseItemEntity].
 */
fun PurchaseItem.toEntity(): PurchaseItemEntity {
    return PurchaseItemEntity(
        id = id,
        purchaseId = purchaseId,
        productId = productId,
        productName = productName,
        unit = unit,
        unitCost = unitCost,
        quantity = quantity,
        subtotal = subtotal
    )
}

fun PurchaseItemEntity.toDomain(): PurchaseItem {
    return PurchaseItem(
        id = id,
        purchaseId = purchaseId,
        productId = productId,
        productName = productName,
        unit = unit,
        unitCost = unitCost,
        quantity = quantity,
        subtotal = subtotal
    )
}
