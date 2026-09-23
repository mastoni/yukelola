package id.yukelola.core.domain.model.serviceorder

/**
 * Line item classification within a ServiceOrder: labor service vs physical material / spare part.
 */
enum class ServiceOrderItemType {
    SERVICE_LABOR,
    PHYSICAL_PART
}
