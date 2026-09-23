package id.yukelola.core.domain.model.payment

/**
 * Financial settlement record applied to a Sale, ServiceOrder, Purchase, or Debt reduction.
 */
data class Payment(
    val id: String,
    val businessId: String,
    val branchId: String,
    val transactionType: PaymentTransactionType,
    val referenceId: String,
    val paymentMethod: PaymentMethod,
    val amount: Long,
    val createdAt: Long
) {
    init {
        require(id.isNotBlank()) { "Payment id must not be blank" }
        require(businessId.isNotBlank()) { "Payment businessId must not be blank" }
        require(branchId.isNotBlank()) { "Payment branchId must not be blank" }
        require(referenceId.isNotBlank()) { "Payment referenceId must not be blank" }
        require(amount > 0L) { "Payment amount must be strictly positive" }
        require(createdAt > 0L) { "Payment createdAt timestamp must be positive" }
    }
}
