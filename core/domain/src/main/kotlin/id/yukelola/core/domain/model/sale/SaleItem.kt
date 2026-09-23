package id.yukelola.core.domain.model.sale

/**
 * Immutable line item snapshot representing a Product sold within a Sale transaction.
 *
 * Invariant: Preserves historical unit price, cost price, and name at the moment of sale.
 * Master catalog changes later must never mutate historical sale items.
 */
data class SaleItem(
    val id: String,
    val saleId: String,
    val productId: String,
    val productName: String,
    val unit: String = "PCS",
    val unitPrice: Long,
    val costPrice: Long = 0L,
    val quantity: Double = 1.0,
    val discountAmount: Long = 0L,
    val subtotal: Long = calculateSubtotal(quantity, unitPrice, discountAmount)
) {
    init {
        require(id.isNotBlank()) { "SaleItem id must not be blank" }
        require(saleId.isNotBlank()) { "SaleItem saleId must not be blank" }
        require(productId.isNotBlank()) { "SaleItem productId must not be blank" }
        require(productName.isNotBlank()) { "SaleItem productName must not be blank" }
        require(unit.isNotBlank()) { "SaleItem unit must not be blank" }
        require(quantity > 0.0) { "SaleItem quantity must be strictly positive" }
        require(unitPrice >= 0L) { "SaleItem unitPrice must not be negative" }
        require(costPrice >= 0L) { "SaleItem costPrice must not be negative" }
        require(discountAmount >= 0L) { "SaleItem discountAmount must not be negative" }
        require(subtotal >= 0L) { "SaleItem subtotal must not be negative" }
    }

    companion object {
        /**
         * Calculates deterministic line subtotal: (quantity * unitPrice) - discountAmount
         */
        fun calculateSubtotal(quantity: Double, unitPrice: Long, discountAmount: Long): Long {
            val gross = (quantity * unitPrice).toLong()
            return maxOf(0L, gross - discountAmount)
        }
    }
}
