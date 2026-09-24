package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.DigitalTransactionComplaintEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.digital.DigitalTransactionComplaint
import id.yukelola.core.domain.model.digital.DigitalTransactionComplaintReason
import id.yukelola.core.domain.model.digital.DigitalTransactionComplaintStatus

/**
 * Lossless mapping functions between canonical domain [DigitalTransactionComplaint] and persistence [DigitalTransactionComplaintEntity].
 */
fun DigitalTransactionComplaint.toEntity(): DigitalTransactionComplaintEntity {
    return DigitalTransactionComplaintEntity(
        id = id,
        digitalTransactionId = digitalTransactionId,
        businessId = businessId,
        branchId = branchId,
        userId = attribution.userId,
        deviceId = attribution.deviceId,
        cashierSessionId = attribution.cashierSessionId,
        reason = reason.name,
        description = description,
        status = status.name,
        resolutionNotes = resolutionNotes,
        resolvedAt = resolvedAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun DigitalTransactionComplaintEntity.toDomain(): DigitalTransactionComplaint {
    return DigitalTransactionComplaint(
        id = id,
        digitalTransactionId = digitalTransactionId,
        attribution = TransactionAttribution(
            businessId = businessId,
            branchId = branchId,
            userId = userId,
            deviceId = deviceId,
            cashierSessionId = cashierSessionId,
            createdAt = createdAt
        ),
        reason = DigitalTransactionComplaintReason.valueOf(reason),
        description = description,
        status = DigitalTransactionComplaintStatus.valueOf(status),
        resolutionNotes = resolutionNotes,
        resolvedAt = resolvedAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
