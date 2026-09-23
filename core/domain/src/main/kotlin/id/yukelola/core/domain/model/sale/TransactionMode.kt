package id.yukelola.core.domain.model.sale

/**
 * Operational transaction engine mode governing checkout flow, fulfillment, and financial ledgers.
 */
enum class TransactionMode {
    /**
     * Instant synchronous over-the-counter physical goods or service checkout.
     */
    RETAIL_TRANSACTION,

    /**
     * Electronic fulfillment backed by digital agent deposit and read-only inquiry.
     */
    DIGITAL_TRANSACTION,

    /**
     * Asynchronous service queue with down payment and pickup settlement.
     */
    SERVICE_ORDER_TRANSACTION
}
