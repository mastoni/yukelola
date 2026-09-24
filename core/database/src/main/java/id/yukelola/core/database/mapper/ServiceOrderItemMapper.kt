package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.ServiceOrderItemEntity
import id.yukelola.core.domain.model.serviceorder.ServiceOrderItem
import id.yukelola.core.domain.model.serviceorder.ServiceOrderItemType

/**
 * Lossless mapping functions between canonical domain [ServiceOrderItem] and persistence [ServiceOrderItemEntity].
 */
fun ServiceOrderItem.toEntity(): ServiceOrderItemEntity {
    return ServiceOrderItemEntity(
        id = id,
        orderId = orderId,
        productId = productId,
        productName = productName,
        itemType = itemType.name,
        unit = unit,
        unitPrice = unitPrice,
        costPrice = costPrice,
        quantity = quantity,
        discountAmount = discountAmount,
        subtotal = subtotal
    )
}

fun ServiceOrderItemEntity.toDomain(): ServiceOrderItem {
    return ServiceOrderItem(
        id = id,
        orderId = orderId,
        productId = productId,
        productName = productName,
        itemType = ServiceOrderItemType.valueOf(itemType),
        unit = unit,
        unitPrice = unitPrice,
        costPrice = costPrice,
        quantity = quantity,
        discountAmount = discountAmount,
        subtotal = subtotal
    )
}
