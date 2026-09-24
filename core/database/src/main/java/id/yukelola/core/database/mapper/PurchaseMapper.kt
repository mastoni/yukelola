package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.PurchaseEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.purchase.Purchase
import id.yukelola.core.domain.model.purchase.PurchaseItem
import id.yukelola.core.domain.model.sale.PaymentStatus

/**
 * Lossless mapping functions between canonical domain [Purchase] and persistence [PurchaseEntity].
 */
fun Purchase.toEntity(): PurchaseEntity {
    return PurchaseEntity(
        id = id,
        businessId = businessId,
        branchId = branchId,
        userId = attribution.userId,
        deviceId = attribution.deviceId,
        cashierSessionId = attribution.cashierSessionId,
        purchaseNumber = purchaseNumber,
        supplierId = supplierId,
        paidAmount = paidAmount,
        paymentStatus = paymentStatus.name,
        notes = notes,
        createdAt = createdAt
    )
}

fun PurchaseEntity.toDomain(items: List<PurchaseItem> = emptyList()): Purchase {
    return Purchase(
        id = id,
        purchaseNumber = purchaseNumber,
        attribution = TransactionAttribution(
            businessId = businessId,
            branchId = branchId,
            userId = userId,
            deviceId = deviceId,
            cashierSessionId = cashierSessionId,
            createdAt = createdAt
        ),
        supplierId = supplierId,
        items = items,
        paidAmount = paidAmount,
        paymentStatus = PaymentStatus.valueOf(paymentStatus),
        notes = notes,
        createdAt = createdAt
    )
}
