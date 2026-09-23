package id.yukelola.core.domain.model.serviceorder

/**
 * Operational fulfillment lifecycle states for a ServiceOrder.
 */
enum class ServiceOrderStatus {
    RECEIVED,
    INSPECTION,
    IN_PROGRESS,
    READY,
    COMPLETED,
    CANCELLED
}
