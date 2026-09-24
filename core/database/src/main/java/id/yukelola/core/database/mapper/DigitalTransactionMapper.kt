package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.DigitalTransactionEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.digital.DigitalTransaction
import id.yukelola.core.domain.model.digital.DigitalTransactionStatus

/**
 * Lossless mapping functions between canonical domain [DigitalTransaction] and persistence [DigitalTransactionEntity].
 */
fun DigitalTransaction.toEntity(): DigitalTransactionEntity {
    return DigitalTransactionEntity(
        id = id,
        businessId = businessId,
        branchId = branchId,
        userId = attribution.userId,
        deviceId = attribution.deviceId,
        cashierSessionId = attribution.cashierSessionId,
        targetNumber = targetNumber,
        productCode = productCode,
        denomination = denomination,
        costPrice = costPrice,
        sellingPrice = sellingPrice,
        saleId = saleId,
        depositMutationId = depositMutationId,
        fulfillmentStatus = fulfillmentStatus.name,
        providerReference = providerReference,
        failureReason = failureReason,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun DigitalTransactionEntity.toDomain(): DigitalTransaction {
    return DigitalTransaction(
        id = id,
        attribution = TransactionAttribution(
            businessId = businessId,
            branchId = branchId,
            userId = userId,
            deviceId = deviceId,
            cashierSessionId = cashierSessionId,
            createdAt = createdAt
        ),
        targetNumber = targetNumber,
        productCode = productCode,
        denomination = denomination,
        costPrice = costPrice,
        sellingPrice = sellingPrice,
        saleId = saleId,
        depositMutationId = depositMutationId,
        fulfillmentStatus = DigitalTransactionStatus.valueOf(fulfillmentStatus),
        providerReference = providerReference,
        failureReason = failureReason,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
