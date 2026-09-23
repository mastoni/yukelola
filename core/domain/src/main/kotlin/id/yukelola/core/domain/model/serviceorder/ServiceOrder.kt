package id.yukelola.core.domain.model.serviceorder

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.sale.TransactionMode

/**
 * Unified asynchronous service order aggregate serving Laundry, Workshop, Printing, and Photocopy businesses.
 *
 * Invariants:
 * 1. Single aggregate root adapting to business verticals via [orderType] and contextual metadata.
 * 2. Scoped to Branch and Business via immutable [attribution].
 * 3. Lifecycle transitions:
 *    - Universal: RECEIVED -> IN_PROGRESS -> READY -> COMPLETED (or CANCELLED)
 *    - Workshop: RECEIVED -> INSPECTION -> IN_PROGRESS -> READY -> COMPLETED (or CANCELLED)
 * 4. Completed orders are immutable historical records.
 * 5. Down payments must not exceed total amount.
 */
data class ServiceOrder(
    val id: String,
    val orderNumber: String,
    val orderType: ServiceOrderType,
    val transactionMode: TransactionMode = TransactionMode.SERVICE_ORDER_TRANSACTION,
    val attribution: TransactionAttribution,
    val customerId: String? = null,
    val items: List<ServiceOrderItem> = emptyList(),
    val downPayments: List<DownPaymentRecord> = emptyList(),
    val discountAmount: Long = 0L,
    val taxAmount: Long = 0L,
    val status: ServiceOrderStatus = ServiceOrderStatus.RECEIVED,
    val contextMetadataJson: String? = null,
    val estimatedCompletionDate: Long? = null,
    val notes: String? = null,
    val createdAt: Long = attribution.createdAt,
    val updatedAt: Long = createdAt,
    val completedAt: Long? = null,
    val cancelledAt: Long? = null
) {
    /**
     * Sum of all line item subtotals.
     */
    val subtotal: Long = items.sumOf { it.subtotal }

    /**
     * Final total amount after applying order-level discount and tax:
     * max(0, subtotal - discountAmount + taxAmount)
     */
    val totalAmount: Long = maxOf(0L, subtotal - discountAmount + taxAmount)

    /**
     * Total upfront down payments recorded.
     */
    val downPaymentAmount: Long = downPayments.sumOf { it.amount }

    /**
     * Outstanding balance to be settled upon pickup / completion.
     */
    val remainingBalance: Long = maxOf(0L, totalAmount - downPaymentAmount)

    /**
     * Tenant Business identifier derived from immutable attribution.
     */
    val businessId: String
        get() = attribution.businessId

    /**
     * Operational Branch identifier derived from immutable attribution.
     */
    val branchId: String
        get() = attribution.branchId

    val isCompleted: Boolean
        get() = status == ServiceOrderStatus.COMPLETED

    val isCancelled: Boolean
        get() = status == ServiceOrderStatus.CANCELLED

    val isActive: Boolean
        get() = !isCompleted && !isCancelled

    init {
        require(id.isNotBlank()) { "ServiceOrder id must not be blank" }
        require(orderNumber.isNotBlank()) { "ServiceOrder orderNumber must not be blank" }
        require(discountAmount >= 0L) { "ServiceOrder discountAmount must not be negative" }
        require(taxAmount >= 0L) { "ServiceOrder taxAmount must not be negative" }
        require(createdAt > 0L) { "ServiceOrder createdAt timestamp must be positive" }
        require(updatedAt >= createdAt) { "ServiceOrder updatedAt must not be earlier than createdAt" }

        completedAt?.let {
            require(it >= createdAt) { "completedAt must not be earlier than createdAt" }
        }
        cancelledAt?.let {
            require(it >= createdAt) { "cancelledAt must not be earlier than createdAt" }
        }

        if (status == ServiceOrderStatus.COMPLETED) {
            require(items.isNotEmpty()) { "Completed service order must have at least one line item" }
        }

        if (items.isNotEmpty()) {
            require(downPaymentAmount <= totalAmount) {
                "Total down payment amount $downPaymentAmount cannot exceed total order amount $totalAmount"
            }
        }
    }

    /**
     * Adds an item to an active service order.
     */
    fun withItem(item: ServiceOrderItem, timestamp: Long = updatedAt): ServiceOrder {
        require(isActive) { "Cannot modify items in a $status service order" }
        require(item.orderId == id) { "ServiceOrderItem orderId does not match ServiceOrder id" }
        return copy(
            items = items + item,
            updatedAt = maxOf(updatedAt, timestamp)
        )
    }

    /**
     * Removes an item from an active service order.
     */
    fun withoutItem(itemId: String, timestamp: Long = updatedAt): ServiceOrder {
        require(isActive) { "Cannot modify items in a $status service order" }
        return copy(
            items = items.filterNot { it.id == itemId },
            updatedAt = maxOf(updatedAt, timestamp)
        )
    }

    /**
     * Records a down payment deposit for this service order.
     */
    fun withDownPayment(downPayment: DownPaymentRecord, timestamp: Long = downPayment.createdAt): ServiceOrder {
        require(isActive) { "Cannot add down payment to a $status service order" }
        require(downPayment.businessId == businessId) {
            "Down payment businessId ${downPayment.businessId} does not match order businessId $businessId"
        }
        require(downPayment.branchId == branchId) {
            "Down payment branchId ${downPayment.branchId} does not match order branchId $branchId"
        }
        require(downPayment.orderId == id) {
            "Down payment orderId ${downPayment.orderId} does not match order id $id"
        }
        require(downPaymentAmount + downPayment.amount <= totalAmount) {
            "Total down payment ${downPaymentAmount + downPayment.amount} would exceed order total $totalAmount"
        }

        return copy(
            downPayments = downPayments + downPayment,
            updatedAt = maxOf(updatedAt, timestamp)
        )
    }

    /**
     * Updates order-level discount.
     */
    fun withDiscount(discountAmount: Long, timestamp: Long = updatedAt): ServiceOrder {
        require(isActive) { "Cannot modify discount on a $status service order" }
        require(discountAmount >= 0L) { "discountAmount must not be negative" }
        return copy(
            discountAmount = discountAmount,
            updatedAt = maxOf(updatedAt, timestamp)
        )
    }

    /**
     * Transitions status following strict deterministic lifecycle invariants.
     */
    fun transitionTo(newStatus: ServiceOrderStatus, timestamp: Long): ServiceOrder {
        require(timestamp >= updatedAt) { "Transition timestamp must not be earlier than updatedAt" }
        require(!isCompleted) { "Cannot transition an already completed service order" }
        require(!isCancelled) { "Cannot transition an already cancelled service order" }

        val isValidTransition = when (status) {
            ServiceOrderStatus.RECEIVED -> newStatus in setOf(
                ServiceOrderStatus.INSPECTION,
                ServiceOrderStatus.IN_PROGRESS,
                ServiceOrderStatus.CANCELLED
            )
            ServiceOrderStatus.INSPECTION -> newStatus in setOf(
                ServiceOrderStatus.IN_PROGRESS,
                ServiceOrderStatus.CANCELLED
            )
            ServiceOrderStatus.IN_PROGRESS -> newStatus in setOf(
                ServiceOrderStatus.READY,
                ServiceOrderStatus.CANCELLED
            )
            ServiceOrderStatus.READY -> newStatus in setOf(
                ServiceOrderStatus.COMPLETED,
                ServiceOrderStatus.CANCELLED
            )
            ServiceOrderStatus.COMPLETED,
            ServiceOrderStatus.CANCELLED -> false
        }

        require(isValidTransition) {
            "Illegal status transition from $status to $newStatus for service order $id"
        }

        if (newStatus == ServiceOrderStatus.COMPLETED) {
            require(items.isNotEmpty()) { "Cannot complete a service order without items" }
        }

        return copy(
            status = newStatus,
            updatedAt = timestamp,
            completedAt = if (newStatus == ServiceOrderStatus.COMPLETED) timestamp else completedAt,
            cancelledAt = if (newStatus == ServiceOrderStatus.CANCELLED) timestamp else cancelledAt
        )
    }

    /**
     * Finalizes the service order to COMPLETED state.
     */
    fun complete(completedAt: Long): ServiceOrder {
        return transitionTo(ServiceOrderStatus.COMPLETED, completedAt)
    }

    /**
     * Voids / cancels the service order to CANCELLED state.
     */
    fun cancel(cancelledAt: Long): ServiceOrder {
        return transitionTo(ServiceOrderStatus.CANCELLED, cancelledAt)
    }
}
