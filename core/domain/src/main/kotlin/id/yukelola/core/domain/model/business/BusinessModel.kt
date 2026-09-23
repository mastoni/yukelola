package id.yukelola.core.domain.model.business

/**
 * Primary operational identity and workflow archetype of a Branch.
 * Governs default navigation priorities, transaction layout, and operational queues.
 *
 * Invariant: BusinessModel != Capability != ProductType != TransactionMode.
 */
enum class BusinessModel {
    RETAIL_WARUNG,
    DIGITAL_KIOSK,
    FOOD_BEVERAGE_CAFE,
    SERVICE_WORKSHOP,
    LAUNDRY,
    RETAIL_HEALTH,
    PERCETAKAN,
    FOTOCOPY,
    ATK,
    GENERAL_STORE
}
