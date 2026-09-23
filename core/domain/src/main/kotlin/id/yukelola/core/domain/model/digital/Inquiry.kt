package id.yukelola.core.domain.model.digital

import id.yukelola.core.domain.model.attribution.TransactionAttribution

/**
 * Read-only validation and bill inquiry aggregate before digital settlement.
 *
 * Core Invariant:
 * Inquiry checks are strictly read-only and idempotent.
 * They NEVER debit cash, debit digital deposit, create sales, mutate stock, or create debts.
 */
data class Inquiry(
    val id: String,
    val attribution: TransactionAttribution,
    val targetNumber: String,
    val productCode: String,
    val customerName: String? = null,
    val billAmount: Long? = null,
    val adminFee: Long = 0L,
    val inquiryDataJson: String? = null,
    val status: InquiryStatus = InquiryStatus.INITIATED,
    val inquiryReference: String? = null,
    val failureReason: String? = null,
    val createdAt: Long = attribution.createdAt,
    val expiresAt: Long? = null,
    val updatedAt: Long = createdAt
) {
    val businessId: String
        get() = attribution.businessId

    val branchId: String
        get() = attribution.branchId

    val userId: String
        get() = attribution.userId

    val deviceId: String
        get() = attribution.deviceId

    /**
     * Total bill settlement amount (billAmount + adminFee).
     */
    val totalBillAmount: Long?
        get() = billAmount?.let { it + adminFee }

    val isTerminal: Boolean
        get() = status == InquiryStatus.FAILED || status == InquiryStatus.EXPIRED

    init {
        require(id.isNotBlank()) { "Inquiry id must not be blank" }
        require(targetNumber.isNotBlank()) { "Inquiry targetNumber must not be blank" }
        require(productCode.isNotBlank()) { "Inquiry productCode must not be blank" }
        require(adminFee >= 0L) { "Inquiry adminFee must not be negative" }
        billAmount?.let {
            require(it >= 0L) { "Inquiry billAmount must not be negative" }
        }
        require(createdAt > 0L) { "Inquiry createdAt timestamp must be positive" }
        require(updatedAt >= createdAt) { "Inquiry updatedAt must not be earlier than createdAt" }
        expiresAt?.let {
            require(it >= createdAt) { "Inquiry expiresAt must not be earlier than createdAt" }
        }
    }

    /**
     * Transitions inquiry to SUCCESS with fetched billing details.
     */
    fun markSuccess(
        customerName: String,
        billAmount: Long,
        adminFee: Long = 0L,
        inquiryReference: String? = null,
        expiresAt: Long? = null,
        timestamp: Long
    ): Inquiry {
        require(customerName.isNotBlank()) { "customerName must not be blank for successful inquiry" }
        require(billAmount >= 0L) { "billAmount must not be negative" }
        return transitionTo(
            newStatus = InquiryStatus.SUCCESS,
            customerName = customerName,
            billAmount = billAmount,
            adminFee = adminFee,
            inquiryReference = inquiryReference,
            expiresAt = expiresAt,
            timestamp = timestamp
        )
    }

    /**
     * Transitions inquiry to FAILED upon provider validation error.
     */
    fun markFailed(
        failureReason: String,
        timestamp: Long
    ): Inquiry {
        require(failureReason.isNotBlank()) { "failureReason must not be blank for failed inquiry" }
        return transitionTo(
            newStatus = InquiryStatus.FAILED,
            failureReason = failureReason,
            timestamp = timestamp
        )
    }

    /**
     * Transitions inquiry from SUCCESS to EXPIRED when validity window closes.
     */
    fun markExpired(timestamp: Long): Inquiry {
        return transitionTo(
            newStatus = InquiryStatus.EXPIRED,
            timestamp = timestamp
        )
    }

    /**
     * Core deterministic state transition engine for Inquiry.
     */
    fun transitionTo(
        newStatus: InquiryStatus,
        customerName: String? = null,
        billAmount: Long? = null,
        adminFee: Long = this.adminFee,
        inquiryReference: String? = null,
        failureReason: String? = null,
        expiresAt: Long? = this.expiresAt,
        timestamp: Long
    ): Inquiry {
        require(timestamp >= updatedAt) { "Transition timestamp must not be earlier than updatedAt" }
        require(!isTerminal) { "Cannot transition an inquiry already in terminal state $status" }

        val isValid = when (status) {
            InquiryStatus.INITIATED -> newStatus in setOf(
                InquiryStatus.SUCCESS,
                InquiryStatus.FAILED
            )
            InquiryStatus.SUCCESS -> newStatus == InquiryStatus.EXPIRED
            InquiryStatus.FAILED,
            InquiryStatus.EXPIRED -> false
        }

        require(isValid) {
            "Illegal status transition from $status to $newStatus for Inquiry $id"
        }

        return copy(
            status = newStatus,
            customerName = customerName ?: this.customerName,
            billAmount = billAmount ?: this.billAmount,
            adminFee = adminFee,
            inquiryReference = inquiryReference ?: this.inquiryReference,
            failureReason = failureReason ?: this.failureReason,
            expiresAt = expiresAt,
            updatedAt = timestamp
        )
    }
}
