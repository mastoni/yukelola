package id.yukelola.core.domain.model.digital

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.catalog.ProductType

/**
 * Electronic digital transaction aggregate record for vouchers, tokens, and bill payments.
 *
 * Invariants:
 * 1. Transaction applies strictly to [ProductType.DIGITAL].
 * 2. Scope is bound to Branch and Business via immutable [attribution].
 * 3. Lifecycle progression:
 *    - Happy Path: INITIATED -> PENDING -> SUCCESS -> REVERSED
 *    - Failure Path: PENDING -> FAILED
 * 4. Terminal states: FAILED, REVERSED. Cannot transition out of terminal states.
 * 5. Profit is defined strictly as: sellingPrice - costPrice.
 */
data class DigitalTransaction(
    val id: String,
    val attribution: TransactionAttribution,
    val targetNumber: String,
    val productCode: String,
    val denomination: Long,
    val costPrice: Long,
    val sellingPrice: Long,
    val saleId: String? = null,
    val depositMutationId: String? = null,
    val fulfillmentStatus: DigitalTransactionStatus = DigitalTransactionStatus.INITIATED,
    val providerReference: String? = null,
    val failureReason: String? = null,
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

    /**
     * Gross profit margin realized upon successful digital dispatch.
     */
    val grossProfit: Long = sellingPrice - costPrice

    val isTerminal: Boolean
        get() = fulfillmentStatus == DigitalTransactionStatus.FAILED ||
                fulfillmentStatus == DigitalTransactionStatus.REVERSED

    init {
        require(id.isNotBlank()) { "DigitalTransaction id must not be blank" }
        require(targetNumber.isNotBlank()) { "DigitalTransaction targetNumber must not be blank" }
        require(productCode.isNotBlank()) { "DigitalTransaction productCode must not be blank" }
        require(denomination > 0L) { "DigitalTransaction denomination must be strictly positive" }
        require(costPrice >= 0L) { "DigitalTransaction costPrice must not be negative" }
        require(sellingPrice >= 0L) { "DigitalTransaction sellingPrice must not be negative" }
        require(createdAt > 0L) { "DigitalTransaction createdAt timestamp must be positive" }
        require(updatedAt >= createdAt) { "DigitalTransaction updatedAt must not be earlier than createdAt" }
    }

    /**
     * Transitions status to PENDING when dispatched to provider.
     */
    fun markPending(timestamp: Long): DigitalTransaction {
        return transitionTo(
            newStatus = DigitalTransactionStatus.PENDING,
            timestamp = timestamp
        )
    }

    /**
     * Transitions status to SUCCESS upon successful provider fulfillment.
     */
    fun markSuccess(
        providerReference: String,
        depositMutationId: String? = null,
        timestamp: Long
    ): DigitalTransaction {
        require(providerReference.isNotBlank()) { "Provider reference must not be blank for successful transaction" }
        return transitionTo(
            newStatus = DigitalTransactionStatus.SUCCESS,
            providerReference = providerReference,
            depositMutationId = depositMutationId ?: this.depositMutationId,
            timestamp = timestamp
        )
    }

    /**
     * Transitions status to FAILED upon provider fulfillment failure.
     */
    fun markFailed(
        failureReason: String,
        timestamp: Long
    ): DigitalTransaction {
        require(failureReason.isNotBlank()) { "Failure reason must not be blank for failed transaction" }
        return transitionTo(
            newStatus = DigitalTransactionStatus.FAILED,
            failureReason = failureReason,
            timestamp = timestamp
        )
    }

    /**
     * Transitions status to REVERSED for traceable compensating reversal of a previously successful dispatch.
     */
    fun markReversed(
        reversalReason: String? = null,
        timestamp: Long
    ): DigitalTransaction {
        return transitionTo(
            newStatus = DigitalTransactionStatus.REVERSED,
            failureReason = reversalReason,
            timestamp = timestamp
        )
    }

    /**
     * Core deterministic state transition engine.
     */
    fun transitionTo(
        newStatus: DigitalTransactionStatus,
        providerReference: String? = null,
        failureReason: String? = null,
        depositMutationId: String? = null,
        timestamp: Long
    ): DigitalTransaction {
        require(timestamp >= updatedAt) { "Transition timestamp must not be earlier than updatedAt" }
        require(!isTerminal) { "Cannot transition a transaction already in terminal state $fulfillmentStatus" }

        val isValid = when (fulfillmentStatus) {
            DigitalTransactionStatus.INITIATED -> newStatus == DigitalTransactionStatus.PENDING
            DigitalTransactionStatus.PENDING -> newStatus in setOf(
                DigitalTransactionStatus.SUCCESS,
                DigitalTransactionStatus.FAILED
            )
            DigitalTransactionStatus.SUCCESS -> newStatus == DigitalTransactionStatus.REVERSED
            DigitalTransactionStatus.FAILED,
            DigitalTransactionStatus.REVERSED -> false
        }

        require(isValid) {
            "Illegal status transition from $fulfillmentStatus to $newStatus for DigitalTransaction $id"
        }

        return copy(
            fulfillmentStatus = newStatus,
            providerReference = providerReference ?: this.providerReference,
            failureReason = failureReason ?: this.failureReason,
            depositMutationId = depositMutationId ?: this.depositMutationId,
            updatedAt = timestamp
        )
    }
}
