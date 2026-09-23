package id.yukelola.core.domain.model.purchase

/**
 * Immutable historical line item representing physical goods procured within a Purchase record.
 *
 * Invariant:
 * Preserves historical unit cost, quantity, and product snapshot.
 * Master catalog changes later must never mutate historical purchase items.
 */
data class PurchaseItem(
    val id: String,
    val purchaseId: String,
    val productId: String,
    val productName: String,
    val unit: String = "PCS",
    val unitCost: Long,
    val quantity: Double = 1.0,
    val subtotal: Long = calculateSubtotal(quantity, unitCost)
) {
    init {
        require(id.isNotBlank()) { "PurchaseItem id must not be blank" }
        require(purchaseId.isNotBlank()) { "PurchaseItem purchaseId must not be blank" }
        require(productId.isNotBlank()) { "PurchaseItem productId must not be blank" }
        require(productName.isNotBlank()) { "PurchaseItem productName must not be blank" }
        require(unit.isNotBlank()) { "PurchaseItem unit must not be blank" }
        require(quantity > 0.0) { "PurchaseItem quantity must be strictly positive" }
        require(unitCost >= 0L) { "PurchaseItem unitCost must not be negative" }
        require(subtotal >= 0L) { "PurchaseItem subtotal must not be negative" }
    }

    companion object {
        fun calculateSubtotal(quantity: Double, unitCost: Long): Long {
            return (quantity * unitCost).toLong()
        }
    }
}
