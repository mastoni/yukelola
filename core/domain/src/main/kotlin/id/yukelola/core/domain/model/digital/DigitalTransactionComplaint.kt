package id.yukelola.core.domain.model.digital

import id.yukelola.core.domain.model.attribution.TransactionAttribution

/**
 * Formal customer or operator complaint / issue report linked to a specific DigitalTransaction.
 *
 * Invariants:
 * 1. Must reference a valid DigitalTransaction via [digitalTransactionId].
 * 2. Scoped to Branch and Business via immutable [attribution] (matching the referenced transaction).
 * 3. Lifecycle transitions:
 *    - Happy Path: OPEN -> INVESTIGATING -> RESOLVED
 *    - Rejection Path: INVESTIGATING -> REJECTED (or OPEN -> REJECTED)
 * 4. Terminal states: RESOLVED, REJECTED.
 * 5. Complaint is NOT reversal or refund: Opening, investigating, or resolving a complaint
 *    does NOT mutate cash drawer, does NOT mutate digital deposit, does NOT create debt,
 *    and does NOT automatically alter DigitalTransactionStatus.
 */
data class DigitalTransactionComplaint(
    val id: String,
    val digitalTransactionId: String,
    val attribution: TransactionAttribution,
    val reason: DigitalTransactionComplaintReason,
    val description: String,
    val status: DigitalTransactionComplaintStatus = DigitalTransactionComplaintStatus.OPEN,
    val resolutionNotes: String? = null,
    val resolvedAt: Long? = null,
    val createdAt: Long = attribution.createdAt,
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

    val isTerminal: Boolean
        get() = status == DigitalTransactionComplaintStatus.RESOLVED ||
                status == DigitalTransactionComplaintStatus.REJECTED

    init {
        require(id.isNotBlank()) { "DigitalTransactionComplaint id must not be blank" }
        require(digitalTransactionId.isNotBlank()) { "digitalTransactionId must not be blank" }
        require(description.isNotBlank()) { "description must not be blank" }
        require(createdAt > 0L) { "createdAt timestamp must be positive" }
        require(updatedAt >= createdAt) { "updatedAt must not be earlier than createdAt" }
        resolvedAt?.let {
            require(it >= createdAt) { "resolvedAt must not be earlier than createdAt" }
        }
    }

    /**
     * Transitions complaint to INVESTIGATING status.
     */
    fun markInvestigating(timestamp: Long): DigitalTransactionComplaint {
        return transitionTo(
            newStatus = DigitalTransactionComplaintStatus.INVESTIGATING,
            timestamp = timestamp
        )
    }

    /**
     * Finalizes complaint to RESOLVED status with investigation outcome.
     */
    fun markResolved(resolutionNotes: String, timestamp: Long): DigitalTransactionComplaint {
        require(resolutionNotes.isNotBlank()) { "resolutionNotes must not be blank when resolving complaint" }
        return transitionTo(
            newStatus = DigitalTransactionComplaintStatus.RESOLVED,
            resolutionNotes = resolutionNotes,
            resolvedAt = timestamp,
            timestamp = timestamp
        )
    }

    /**
     * Finalizes complaint to REJECTED status.
     */
    fun markRejected(rejectionReason: String, timestamp: Long): DigitalTransactionComplaint {
        require(rejectionReason.isNotBlank()) { "rejectionReason must not be blank when rejecting complaint" }
        return transitionTo(
            newStatus = DigitalTransactionComplaintStatus.REJECTED,
            resolutionNotes = rejectionReason,
            resolvedAt = timestamp,
            timestamp = timestamp
        )
    }

    /**
     * Core deterministic state transition engine for Complaint.
     */
    fun transitionTo(
        newStatus: DigitalTransactionComplaintStatus,
        resolutionNotes: String? = null,
        resolvedAt: Long? = null,
        timestamp: Long
    ): DigitalTransactionComplaint {
        require(timestamp >= updatedAt) { "Transition timestamp must not be earlier than updatedAt" }
        require(!isTerminal) { "Cannot transition a complaint already in terminal state $status" }

        val isValid = when (status) {
            DigitalTransactionComplaintStatus.OPEN -> newStatus in setOf(
                DigitalTransactionComplaintStatus.INVESTIGATING,
                DigitalTransactionComplaintStatus.REJECTED
            )
            DigitalTransactionComplaintStatus.INVESTIGATING -> newStatus in setOf(
                DigitalTransactionComplaintStatus.RESOLVED,
                DigitalTransactionComplaintStatus.REJECTED
            )
            DigitalTransactionComplaintStatus.RESOLVED,
            DigitalTransactionComplaintStatus.REJECTED -> false
        }

        require(isValid) {
            "Illegal status transition from $status to $newStatus for complaint $id"
        }

        return copy(
            status = newStatus,
            resolutionNotes = resolutionNotes ?: this.resolutionNotes,
            resolvedAt = resolvedAt ?: this.resolvedAt,
            updatedAt = timestamp
        )
    }

    companion object {
        /**
         * Factory function to create a complaint linked to a specific transaction.
         * Enforces strict branch and business attribution consistency.
         */
        fun createForTransaction(
            id: String,
            transaction: DigitalTransaction,
            attribution: TransactionAttribution,
            reason: DigitalTransactionComplaintReason,
            description: String,
            createdAt: Long = attribution.createdAt
        ): DigitalTransactionComplaint {
            require(attribution.businessId == transaction.businessId) {
                "Complaint businessId ${attribution.businessId} does not match transaction businessId ${transaction.businessId}"
            }
            require(attribution.branchId == transaction.branchId) {
                "Complaint branchId ${attribution.branchId} does not match transaction branchId ${transaction.branchId}"
            }

            return DigitalTransactionComplaint(
                id = id,
                digitalTransactionId = transaction.id,
                attribution = attribution,
                reason = reason,
                description = description,
                status = DigitalTransactionComplaintStatus.OPEN,
                createdAt = createdAt,
                updatedAt = createdAt
            )
        }
    }
}
