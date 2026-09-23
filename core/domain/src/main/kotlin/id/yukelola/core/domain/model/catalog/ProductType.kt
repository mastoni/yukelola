package id.yukelola.core.domain.model.catalog

/**
 * Classification of goods fulfillment behavior.
 */
enum class ProductType {
    /**
     * Tangible physical goods requiring on-hand inventory count and physical handover.
     */
    PHYSICAL,

    /**
     * Intangible service labor or customized operational work; no physical warehouse stock.
     */
    SERVICE,

    /**
     * Electronic items (pulsa, PLN token, e-wallet); fulfills via digital agent deposit.
     */
    DIGITAL;

    /**
     * Whether this product type maintains and mutates physical on-hand inventory.
     */
    val requiresPhysicalStock: Boolean
        get() = this == PHYSICAL
}
