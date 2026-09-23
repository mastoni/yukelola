package id.yukelola.core.domain.model.serviceorder

/**
 * Immutable historical line item within a ServiceOrder.
 *
 * Invariant:
 * Preserves historical unit price, cost price, and item descriptions.
 * Master catalog changes later must never mutate historical service order items.
 */
data class ServiceOrderItem(
    val id: String,
    val orderId: String,
    val productId: String,
    val productName: String,
    val itemType: ServiceOrderItemType = ServiceOrderItemType.SERVICE_LABOR,
    val unit: String = "PCS",
    val unitPrice: Long,
    val costPrice: Long = 0L,
    val quantity: Double = 1.0,
    val discountAmount: Long = 0L,
    val subtotal: Long = calculateSubtotal(quantity, unitPrice, discountAmount)
) {
    init {
        require(id.isNotBlank()) { "ServiceOrderItem id must not be blank" }
        require(orderId.isNotBlank()) { "ServiceOrderItem orderId must not be blank" }
        require(productId.isNotBlank()) { "ServiceOrderItem productId must not be blank" }
        require(productName.isNotBlank()) { "ServiceOrderItem productName must not be blank" }
        require(unit.isNotBlank()) { "ServiceOrderItem unit must not be blank" }
        require(quantity > 0.0) { "ServiceOrderItem quantity must be strictly positive" }
        require(unitPrice >= 0L) { "ServiceOrderItem unitPrice must not be negative" }
        require(costPrice >= 0L) { "ServiceOrderItem costPrice must not be negative" }
        require(discountAmount >= 0L) { "ServiceOrderItem discountAmount must not be negative" }
        require(subtotal >= 0L) { "ServiceOrderItem subtotal must not be negative" }
    }

    companion object {
        fun calculateSubtotal(quantity: Double, unitPrice: Long, discountAmount: Long): Long {
            val gross = (quantity * unitPrice).toLong()
            return maxOf(0L, gross - discountAmount)
        }
    }
}
