package id.yukelola.core.domain.model.stock

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.catalog.Product
import id.yukelola.core.domain.model.catalog.ProductType

/**
 * Record of a manual inventory count correction (Penyesuaian Stok).
 *
 * Invariants:
 * 1. Strictly isolated to Branch and Business context.
 * 2. Only applicable to ProductType.PHYSICAL items.
 * 3. Requires an explicit, controlled [StockAdjustmentReason].
 * 4. Enforces non-negative resulting stock unless allowNegativeStock is enabled.
 * 5. Stock adjustment does NOT mutate cash, debt, or digital deposit ledgers.
 */
data class StockAdjustment(
    val id: String,
    val businessId: String,
    val branchId: String,
    val productId: String,
    val previousStock: Double,
    val adjustedStock: Double,
    val reason: StockAdjustmentReason,
    val userId: String,
    val deviceId: String,
    val notes: String? = null,
    val createdAt: Long
) {
    val deltaQuantity: Double
        get() = adjustedStock - previousStock

    init {
        require(id.isNotBlank()) { "StockAdjustment id must not be blank" }
        require(businessId.isNotBlank()) { "StockAdjustment businessId must not be blank" }
        require(branchId.isNotBlank()) { "StockAdjustment branchId must not be blank" }
        require(productId.isNotBlank()) { "StockAdjustment productId must not be blank" }
        require(userId.isNotBlank()) { "StockAdjustment userId must not be blank" }
        require(deviceId.isNotBlank()) { "StockAdjustment deviceId must not be blank" }
        require(createdAt > 0L) { "StockAdjustment createdAt timestamp must be positive" }
    }

    companion object {
        /**
         * Factory method to create and validate a StockAdjustment against domain rules.
         */
        fun create(
            id: String,
            businessId: String,
            branchId: String,
            productId: String,
            productType: ProductType,
            previousStock: Double,
            adjustedStock: Double,
            reason: StockAdjustmentReason,
            userId: String,
            deviceId: String,
            createdAt: Long,
            notes: String? = null,
            allowNegativeStock: Boolean = false
        ): StockAdjustment {
            require(productType == ProductType.PHYSICAL) {
                "Stock adjustment is only permitted for PHYSICAL products, but was $productType"
            }
            if (!allowNegativeStock) {
                require(adjustedStock >= 0.0) {
                    "Adjusted stock ($adjustedStock) cannot be negative when allowNegativeStock is false"
                }
            }
            return StockAdjustment(
                id = id,
                businessId = businessId,
                branchId = branchId,
                productId = productId,
                previousStock = previousStock,
                adjustedStock = adjustedStock,
                reason = reason,
                userId = userId,
                deviceId = deviceId,
                notes = notes,
                createdAt = createdAt
            )
        }

        /**
         * Factory method using [Product] master and [TransactionAttribution].
         */
        fun create(
            id: String,
            product: Product,
            previousStock: Double,
            adjustedStock: Double,
            reason: StockAdjustmentReason,
            attribution: TransactionAttribution,
            notes: String? = null,
            allowNegativeStock: Boolean = false
        ): StockAdjustment {
            require(product.businessId == attribution.businessId) {
                "Product businessId (${product.businessId}) does not match attribution businessId (${attribution.businessId})"
            }
            return create(
                id = id,
                businessId = attribution.businessId,
                branchId = attribution.branchId,
                productId = product.id,
                productType = product.productType,
                previousStock = previousStock,
                adjustedStock = adjustedStock,
                reason = reason,
                userId = attribution.userId,
                deviceId = attribution.deviceId,
                createdAt = attribution.createdAt,
                notes = notes,
                allowNegativeStock = allowNegativeStock
            )
        }
    }
}
