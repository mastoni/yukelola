package id.yukelola.core.domain.model.business

/**
 * Modular functional feature sets enabled dynamically for a Branch.
 * Capabilities extend branch functionality without altering its primary BusinessModel identity.
 */
enum class Capability {
    RETAIL,
    INVENTORY,
    PURCHASE,
    DIGITAL_SERVICE,
    DIGITAL_DEPOSIT,
    PPOB,
    FUEL,
    FOOD_BEVERAGE,
    SERVICE,
    CUSTOMER_DEBT,
    SUPPLIER_DEBT,
    CASH,
    REPORTING
}
