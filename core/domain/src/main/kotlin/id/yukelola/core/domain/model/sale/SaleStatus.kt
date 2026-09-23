package id.yukelola.core.domain.model.sale

/**
 * Lifecycle state of a retail commercial transaction.
 */
enum class SaleStatus {
    /**
     * Active draft or cart undergoing item selection.
     */
    DRAFT,

    /**
     * Finalized commercial transaction record.
     */
    COMPLETED,

    /**
     * Terminal void/cancellation state preserving historical record.
     */
    CANCELLED
}
