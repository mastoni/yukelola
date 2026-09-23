package id.yukelola.core.domain.model.digital

/**
 * Fulfillment lifecycle status of an electronic digital transaction.
 */
enum class DigitalTransactionStatus {
    INITIATED,
    PENDING,
    SUCCESS,
    FAILED,
    REVERSED
}
