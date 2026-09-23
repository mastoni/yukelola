package id.yukelola.core.domain.model.payment

/**
 * Commercial transaction category to which a financial Payment applies.
 */
enum class PaymentTransactionType {
    SALE,
    SERVICE_ORDER,
    PURCHASE,
    CUSTOMER_DEBT,
    SUPPLIER_DEBT
}
