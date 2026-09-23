package id.yukelola.core.domain.model.catalog

/**
 * Global Master Catalog definition owned by a Business tenant.
 * Defines standard product metadata, SKU, barcode, base measurement unit, and baseline prices.
 *
 * Invariant: Master Product does NOT store mutable branch stock.
 */
data class Product(
    val id: String,
    val businessId: String,
    val categoryId: String? = null,
    val sku: String? = null,
    val barcode: String? = null,
    val name: String,
    val productType: ProductType,
    val baseUnit: String = "PCS",
    val defaultCostPrice: Long = 0L,
    val defaultSellingPrice: Long = 0L,
    val trackStock: Boolean = true,
    val isActive: Boolean = true
) {
    init {
        require(id.isNotBlank()) { "Product id must not be blank" }
        require(businessId.isNotBlank()) { "Product businessId must not be blank" }
        require(name.isNotBlank()) { "Product name must not be blank" }
        require(baseUnit.isNotBlank()) { "Product baseUnit must not be blank" }
        require(defaultCostPrice >= 0L) { "defaultCostPrice must not be negative" }
        require(defaultSellingPrice >= 0L) { "defaultSellingPrice must not be negative" }
    }

    /**
     * Evaluates whether physical inventory should be tracked for this product.
     * True only for PHYSICAL product type with trackStock enabled.
     */
    val isInventoryTracked: Boolean
        get() = productType.requiresPhysicalStock && trackStock
}
