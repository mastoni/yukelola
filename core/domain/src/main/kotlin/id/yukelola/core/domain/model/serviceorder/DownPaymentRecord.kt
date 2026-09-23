package id.yukelola.core.domain.model.serviceorder

import id.yukelola.core.domain.model.payment.PaymentMethod

/**
 * Upfront financial deposit collected upon service intake.
 *
 * Invariant:
 * Down payment represents customer unearned deposit / advance payment.
 * It is NOT recognized immediately as sales profit.
 */
data class DownPaymentRecord(
    val id: String,
    val businessId: String,
    val branchId: String,
    val orderId: String,
    val amount: Long,
    val paymentMethod: PaymentMethod,
    val notes: String? = null,
    val createdAt: Long
) {
    init {
        require(id.isNotBlank()) { "DownPaymentRecord id must not be blank" }
        require(businessId.isNotBlank()) { "DownPaymentRecord businessId must not be blank" }
        require(branchId.isNotBlank()) { "DownPaymentRecord branchId must not be blank" }
        require(orderId.isNotBlank()) { "DownPaymentRecord orderId must not be blank" }
        require(amount > 0L) { "DownPaymentRecord amount must be strictly positive" }
        require(createdAt > 0L) { "DownPaymentRecord createdAt timestamp must be positive" }
    }
}
