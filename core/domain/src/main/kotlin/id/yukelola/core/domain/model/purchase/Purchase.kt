package id.yukelola.core.domain.model.purchase

import id.yukelola.core.domain.model.actor.Supplier
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.sale.PaymentStatus

/**
 * Inventory replenishment procurement aggregate record from a supplier.
 *
 * Invariants:
 * 1. Scoped to Branch and Business via immutable [attribution].
 * 2. References optional [supplierId] (business-scoped vendor).
 * 3. Credit purchases (paidAmount < totalAmount) with tracked payables require a supplierId.
 * 4. Preserves historical line-item snapshots in [items].
 * 5. Purchase does NOT automatically mutate stock, cash drawers, or debt ledgers.
 */
data class Purchase(
    val id: String,
    val purchaseNumber: String,
    val attribution: TransactionAttribution,
    val supplierId: String? = null,
    val items: List<PurchaseItem> = emptyList(),
    val paidAmount: Long = 0L,
    val paymentStatus: PaymentStatus = resolvePaymentStatus(paidAmount, items.sumOf { it.subtotal }),
    val notes: String? = null,
    val createdAt: Long = attribution.createdAt
) {
    val totalAmount: Long = items.sumOf { it.subtotal }

    val remainingBalance: Long = maxOf(0L, totalAmount - paidAmount)

    val businessId: String
        get() = attribution.businessId

    val branchId: String
        get() = attribution.branchId

    val userId: String
        get() = attribution.userId

    val deviceId: String
        get() = attribution.deviceId

    init {
        require(id.isNotBlank()) { "Purchase id must not be blank" }
        require(purchaseNumber.isNotBlank()) { "Purchase purchaseNumber must not be blank" }
        require(paidAmount >= 0L) { "Purchase paidAmount must not be negative" }
        require(createdAt > 0L) { "Purchase createdAt timestamp must be positive" }

        if (items.isNotEmpty() && paidAmount < totalAmount) {
            require(!supplierId.isNullOrBlank()) {
                "Credit purchase with unpaid balance requires an identified supplier"
            }
        }
    }

    /**
     * Adds an item to this purchase.
     */
    fun withItem(item: PurchaseItem): Purchase {
        require(item.purchaseId == id) { "PurchaseItem purchaseId does not match Purchase id" }
        val newItems = items + item
        val newTotal = newItems.sumOf { it.subtotal }
        return copy(
            items = newItems,
            paymentStatus = resolvePaymentStatus(paidAmount, newTotal)
        )
    }

    /**
     * Removes an item from this purchase by item ID.
     */
    fun withoutItem(itemId: String): Purchase {
        val newItems = items.filterNot { it.id == itemId }
        val newTotal = newItems.sumOf { it.subtotal }
        return copy(
            items = newItems,
            paymentStatus = resolvePaymentStatus(paidAmount, newTotal)
        )
    }

    /**
     * Sets the settled payment amount for this purchase.
     */
    fun withPayment(paidAmount: Long): Purchase {
        require(paidAmount >= 0L) { "paidAmount must not be negative" }
        return copy(
            paidAmount = paidAmount,
            paymentStatus = resolvePaymentStatus(paidAmount, totalAmount)
        )
    }

    companion object {
        fun resolvePaymentStatus(paidAmount: Long, totalAmount: Long): PaymentStatus {
            return when {
                totalAmount == 0L -> PaymentStatus.PAID
                paidAmount >= totalAmount -> PaymentStatus.PAID
                paidAmount > 0L -> PaymentStatus.PARTIALLY_PAID
                else -> PaymentStatus.UNPAID
            }
        }

        /**
         * Validates supplier business consistency when linking a Supplier to a Purchase.
         */
        fun validateSupplierOwnership(supplier: Supplier, purchase: Purchase) {
            require(supplier.businessId == purchase.businessId) {
                "Supplier businessId ${supplier.businessId} does not match Purchase businessId ${purchase.businessId}"
            }
        }
    }
}
