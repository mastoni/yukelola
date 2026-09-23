package id.yukelola.core.domain.model.cash

/**
 * Operational reason/category for cash movement in a cash drawer.
 */
enum class CashMutationCategory {
    SALE,
    PURCHASE,
    CUSTOMER_DEBT_PAYMENT,
    SUPPLIER_DEBT_PAYMENT,
    EXPENSE,
    MANUAL_ADJUSTMENT,
    OPENING_BALANCE,
    OTHER
}
