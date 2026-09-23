package id.yukelola.core.domain.model.catalog

/**
 * Branch-specific operational product state and pricing ledger.
 * Governs local on-hand inventory, reorder thresholds, and localized price overrides for a Branch.
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
}
