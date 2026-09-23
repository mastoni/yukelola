package id.yukelola.core.domain.model.debt

import id.yukelola.core.domain.model.payment.PaymentMethod

/**
 * Financial event recording the settlement or partial reduction of an outstanding CustomerDebt or SupplierDebt.
 */
data class DebtPayment(
    val id: String,
    val businessId: String,
    val branchId: String,
    val debtType: DebtType,
    val debtId: String,
    val amount: Long,
    val paymentMethod: PaymentMethod,
    val notes: String? = null,
    val createdAt: Long
) {
    init {
        require(id.isNotBlank()) { "DebtPayment id must not be blank" }
        require(businessId.isNotBlank()) { "DebtPayment businessId must not be blank" }
        require(branchId.isNotBlank()) { "DebtPayment branchId must not be blank" }
        require(debtId.isNotBlank()) { "DebtPayment debtId must not be blank" }
        require(amount > 0L) { "DebtPayment amount must be strictly positive" }
        require(createdAt > 0L) { "DebtPayment createdAt timestamp must be positive" }
    }
}
