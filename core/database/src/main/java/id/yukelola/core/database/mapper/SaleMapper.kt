package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.SaleEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.sale.PaymentStatus
import id.yukelola.core.domain.model.sale.Sale
import id.yukelola.core.domain.model.sale.SaleItem
import id.yukelola.core.domain.model.sale.SaleStatus
import id.yukelola.core.domain.model.sale.TransactionMode

/**
 * Lossless mapping functions between canonical domain [Sale] and persistence [SaleEntity].
 */
fun Sale.toEntity(): SaleEntity {
    return SaleEntity(
        id = id,
        businessId = businessId,
        branchId = branchId,
        userId = attribution.userId,
        deviceId = attribution.deviceId,
        cashierSessionId = attribution.cashierSessionId,
        saleNumber = saleNumber,
        transactionMode = transactionMode.name,
        customerId = customerId,
        discountAmount = discountAmount,
        taxAmount = taxAmount,
        paidAmount = paidAmount,
        paymentStatus = paymentStatus.name,
        status = status.name,
        notes = notes,
        createdAt = createdAt,
        completedAt = completedAt,
        cancelledAt = cancelledAt
    )
}

fun SaleEntity.toDomain(items: List<SaleItem> = emptyList()): Sale {
    return Sale(
        id = id,
        saleNumber = saleNumber,
        transactionMode = TransactionMode.valueOf(transactionMode),
        attribution = TransactionAttribution(
            businessId = businessId,
            branchId = branchId,
            userId = userId,
            deviceId = deviceId,
            cashierSessionId = cashierSessionId,
            createdAt = createdAt
        ),
        customerId = customerId,
        items = items,
        discountAmount = discountAmount,
        taxAmount = taxAmount,
        paidAmount = paidAmount,
        paymentStatus = PaymentStatus.valueOf(paymentStatus),
        status = SaleStatus.valueOf(status),
        notes = notes,
        createdAt = createdAt,
        completedAt = completedAt,
        cancelledAt = cancelledAt
    )
}
