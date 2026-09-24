package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.ServiceOrderEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.sale.TransactionMode
import id.yukelola.core.domain.model.serviceorder.DownPaymentRecord
import id.yukelola.core.domain.model.serviceorder.ServiceOrder
import id.yukelola.core.domain.model.serviceorder.ServiceOrderItem
import id.yukelola.core.domain.model.serviceorder.ServiceOrderStatus
import id.yukelola.core.domain.model.serviceorder.ServiceOrderType

/**
 * Lossless mapping functions between canonical domain [ServiceOrder] and persistence [ServiceOrderEntity].
 */
fun ServiceOrder.toEntity(): ServiceOrderEntity {
    return ServiceOrderEntity(
        id = id,
        businessId = businessId,
        branchId = branchId,
        userId = attribution.userId,
        deviceId = attribution.deviceId,
        cashierSessionId = attribution.cashierSessionId,
        orderNumber = orderNumber,
        orderType = orderType.name,
        transactionMode = transactionMode.name,
        customerId = customerId,
        discountAmount = discountAmount,
        taxAmount = taxAmount,
        status = status.name,
        contextMetadataJson = contextMetadataJson,
        estimatedCompletionDate = estimatedCompletionDate,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        completedAt = completedAt,
        cancelledAt = cancelledAt
    )
}

fun ServiceOrderEntity.toDomain(
    items: List<ServiceOrderItem> = emptyList(),
    downPayments: List<DownPaymentRecord> = emptyList()
): ServiceOrder {
    return ServiceOrder(
        id = id,
        orderNumber = orderNumber,
        orderType = ServiceOrderType.valueOf(orderType),
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
        downPayments = downPayments,
        discountAmount = discountAmount,
        taxAmount = taxAmount,
        status = ServiceOrderStatus.valueOf(status),
        contextMetadataJson = contextMetadataJson,
        estimatedCompletionDate = estimatedCompletionDate,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        completedAt = completedAt,
        cancelledAt = cancelledAt
    )
}
