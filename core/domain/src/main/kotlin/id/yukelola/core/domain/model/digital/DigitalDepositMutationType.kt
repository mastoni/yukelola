package id.yukelola.core.domain.model.digital

/**
 * Directional category of movement for digital agent deposit funds.
 */
enum class DigitalDepositMutationType {
    TOP_UP,
    DIGITAL_SALE,
    REFUND,
    REVERSAL,
    ADJUSTMENT
}
