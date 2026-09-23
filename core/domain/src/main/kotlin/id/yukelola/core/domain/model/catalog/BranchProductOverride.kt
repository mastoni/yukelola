package id.yukelola.core.domain.model.catalog

/**
 * Branch-specific operational product state and pricing ledger.
 * Governs local on-hand inventory, reorder thresholds, and localized price overrides for a Branch.
 *
 * Invariant: Stock, local pricing, and availability belong strictly to the Branch context.
 */
data class BranchProductOverride(
    val branchId: String,
    val productId: String,
    val stock: Double = 0.0,
    val minStock: Double = 0.0,
    val localCostPrice: Long? = null,
    val localSellingPrice: Long? = null,
    val isAvailable: Boolean = true
) {
    init {
        require(branchId.isNotBlank()) { "BranchProductOverride branchId must not be blank" }
        require(productId.isNotBlank()) { "BranchProductOverride productId must not be blank" }
        require(minStock >= 0.0) { "minStock must not be negative" }
        localCostPrice?.let { require(it >= 0L) { "localCostPrice must not be negative" } }
        localSellingPrice?.let { require(it >= 0L) { "localSellingPrice must not be negative" } }
    }

    /**
     * Resolves the effective selling price, prioritizing branch override over global default.
     */
    fun getEffectiveSellingPrice(defaultPrice: Long): Long = localSellingPrice ?: defaultPrice

    /**
     * Resolves the effective cost price, prioritizing branch override over global default.
     */
    fun getEffectiveCostPrice(defaultCost: Long): Long = localCostPrice ?: defaultCost

    /**
     * Checks if stock has fallen below the minimum reorder threshold.
     */
    val isLowStock: Boolean
        get() = stock <= minStock

    /**
     * Produces a branch product override with updated stock count (immutable copy).
     * Rejects negative stock if allowNegativeStock is false.
     */
    fun withStock(newStock: Double, allowNegativeStock: Boolean = false): BranchProductOverride {
        if (!allowNegativeStock) {
            require(newStock >= 0.0) {
                "Stock quantity cannot be negative ($newStock) when allowNegativeStock is false"
            }
        }
        return copy(stock = newStock)
    }

    /**
     * Produces a branch product override with stock adjusted by delta.
     * Rejects negative stock if allowNegativeStock is false.
     */
    fun withStockDelta(delta: Double, allowNegativeStock: Boolean = false): BranchProductOverride {
        val newStock = stock + delta
        return withStock(newStock, allowNegativeStock)
    }

    /**
     * Produces a branch product override with updated local pricing.
     */
    fun withPricing(localSellingPrice: Long?, localCostPrice: Long?): BranchProductOverride =
        copy(localSellingPrice = localSellingPrice, localCostPrice = localCostPrice)

    /**
     * Produces a branch product override with updated availability.
     */
    fun withAvailability(isAvailable: Boolean): BranchProductOverride = copy(isAvailable = isAvailable)
}
