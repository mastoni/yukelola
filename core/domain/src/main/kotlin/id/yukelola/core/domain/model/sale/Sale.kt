package id.yukelola.core.domain.model.sale

import id.yukelola.core.domain.model.attribution.TransactionAttribution

/**
 * Historical commercial retail transaction aggregate.
 *
 * Invariants:
 * 1. Completed sales are immutable historical records.
 * 2. Sale ownership is bound to attribution: sale.businessId == attribution.businessId, sale.branchId == attribution.branchId.
 * 3. Credit sales (paidAmount < totalAmount) require an identified Customer; anonymous credit is prohibited.
 * 4. Lifecycle follows: DRAFT -> COMPLETED -> CANCELLED.
 */
data class Sale(
    val id: String,
    val saleNumber: String,
    val transactionMode: TransactionMode = TransactionMode.RETAIL_TRANSACTION,
    val attribution: TransactionAttribution,
    val customerId: String? = null,
    val items: List<SaleItem> = emptyList(),
    val discountAmount: Long = 0L,
    val taxAmount: Long = 0L,
    val paidAmount: Long = 0L,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val status: SaleStatus = SaleStatus.DRAFT,
    val notes: String? = null,
    val createdAt: Long = attribution.createdAt,
    val completedAt: Long? = null,
    val cancelledAt: Long? = null
) {
    /**
     * Sum of all line item subtotals.
     */
    val subtotal: Long = items.sumOf { it.subtotal }

    /**
     * Final total amount after applying sale-level discount and tax:
     * max(0, subtotal - discountAmount + taxAmount)
     */
    val totalAmount: Long = maxOf(0L, subtotal - discountAmount + taxAmount)

    /**
     * Outstanding unpaid balance.
     */
    val remainingBalance: Long = maxOf(0L, totalAmount - paidAmount)

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

    val isDraft: Boolean
        get() = status == SaleStatus.DRAFT

    val isCompleted: Boolean
        get() = status == SaleStatus.COMPLETED

    val isCancelled: Boolean
        get() = status == SaleStatus.CANCELLED

    init {
        require(id.isNotBlank()) { "Sale id must not be blank" }
        require(saleNumber.isNotBlank()) { "Sale saleNumber must not be blank" }
        require(discountAmount >= 0L) { "Sale discountAmount must not be negative" }
        require(taxAmount >= 0L) { "Sale taxAmount must not be negative" }
        require(paidAmount >= 0L) { "Sale paidAmount must not be negative" }
        require(createdAt > 0L) { "Sale createdAt timestamp must be positive" }

        completedAt?.let {
            require(it >= createdAt) { "completedAt must not be earlier than createdAt" }
        }
        cancelledAt?.let {
            require(it >= createdAt) { "cancelledAt must not be earlier than createdAt" }
        }

        if (status == SaleStatus.COMPLETED) {
            require(items.isNotEmpty()) { "Completed sale must have at least one sale item" }
            if (paidAmount < totalAmount) {
                require(!customerId.isNullOrBlank()) {
                    "Credit sale with unpaid balance requires an identified customer"
                }
            }
        }
    }

    /**
     * Adds an item to a draft sale (immutable transition).
     */
    fun withItem(item: SaleItem): Sale {
        require(isDraft) { "Cannot add items to a non-draft sale" }
        require(item.saleId == id) { "SaleItem saleId does not match Sale id" }
        return copy(items = items + item)
    }

    /**
     * Removes an item from a draft sale by item id.
     */
    fun withoutItem(itemId: String): Sale {
        require(isDraft) { "Cannot remove items from a non-draft sale" }
        return copy(items = items.filterNot { it.id == itemId })
    }

    /**
     * Sets sale-level discount on a draft sale.
     */
    fun withDiscount(discountAmount: Long): Sale {
        require(isDraft) { "Cannot modify discount on a non-draft sale" }
        require(discountAmount >= 0L) { "discountAmount must not be negative" }
        return copy(discountAmount = discountAmount)
    }

    /**
     * Finalizes the sale from DRAFT to COMPLETED state.
     */
    fun complete(
        paidAmount: Long,
        completedAt: Long,
        paymentStatus: PaymentStatus = resolvePaymentStatus(paidAmount, totalAmount)
    ): Sale {
        require(isDraft) { "Cannot complete a sale that is already $status" }
        require(items.isNotEmpty()) { "Cannot complete a sale with zero items" }
        require(completedAt >= createdAt) { "completedAt must not be earlier than createdAt" }
        require(paidAmount >= 0L) { "paidAmount must not be negative" }

        if (paidAmount < totalAmount) {
            require(!customerId.isNullOrBlank()) {
                "Credit sale with unpaid balance requires an identified customer"
            }
        }

        return copy(
            paidAmount = paidAmount,
            paymentStatus = paymentStatus,
            status = SaleStatus.COMPLETED,
            completedAt = completedAt
        )
    }

    /**
     * Voids / cancels the sale to terminal CANCELLED state.
     */
    fun cancel(cancelledAt: Long): Sale {
        require(!isCancelled) { "Cannot cancel an already cancelled sale" }
        require(cancelledAt >= createdAt) { "cancelledAt must not be earlier than createdAt" }
        return copy(
            status = SaleStatus.CANCELLED,
            cancelledAt = cancelledAt
        )
    }

    companion object {
        fun resolvePaymentStatus(paidAmount: Long, totalAmount: Long): PaymentStatus {
            return when {
                paidAmount >= totalAmount -> PaymentStatus.PAID
                paidAmount > 0L -> PaymentStatus.PARTIALLY_PAID
                else -> PaymentStatus.UNPAID
            }
        }
    }
}
