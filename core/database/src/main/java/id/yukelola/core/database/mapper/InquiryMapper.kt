package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.InquiryEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.digital.Inquiry
import id.yukelola.core.domain.model.digital.InquiryStatus

/**
 * Lossless mapping functions between canonical domain [Inquiry] and persistence [InquiryEntity].
 */
fun Inquiry.toEntity(): InquiryEntity {
    return InquiryEntity(
        id = id,
        businessId = businessId,
        branchId = branchId,
        userId = attribution.userId,
        deviceId = attribution.deviceId,
        cashierSessionId = attribution.cashierSessionId,
        targetNumber = targetNumber,
        productCode = productCode,
        customerName = customerName,
        billAmount = billAmount,
        adminFee = adminFee,
        inquiryDataJson = inquiryDataJson,
        status = status.name,
        inquiryReference = inquiryReference,
        failureReason = failureReason,
        createdAt = createdAt,
        expiresAt = expiresAt,
        updatedAt = updatedAt
    )
}

fun InquiryEntity.toDomain(): Inquiry {
    return Inquiry(
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
        customerName = customerName,
        billAmount = billAmount,
        adminFee = adminFee,
        inquiryDataJson = inquiryDataJson,
        status = InquiryStatus.valueOf(status),
        inquiryReference = inquiryReference,
        failureReason = failureReason,
        createdAt = createdAt,
        expiresAt = expiresAt,
        updatedAt = updatedAt
    )
}
