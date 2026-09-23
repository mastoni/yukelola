package id.yukelola.core.domain.model.payment

/**
 * Monetary settlement channels accepted for commercial transactions.
 */
enum class PaymentMethod {
    CASH,
    QRIS,
    TRANSFER,
    DEBT,
    OTHER
}
