package id.yukelola.core.domain.model.debt

/**
 * Outstanding credit receivable owed by a customer to a branch (Piutang Pelanggan / Kasbon).
 */
data class CustomerDebt(
    val id: String,
    val businessId: String,
    val branchId: String,
    val customerId: String,
    val referenceType: DebtReferenceType,
    val referenceId: String,
    val originalAmount: Long,
    val remainingAmount: Long,
    val status: DebtStatus,
    val dueDate: Long? = null,
    val createdAt: Long
) {
    init {
        require(id.isNotBlank()) { "CustomerDebt id must not be blank" }
        require(businessId.isNotBlank()) { "CustomerDebt businessId must not be blank" }
        require(branchId.isNotBlank()) { "CustomerDebt branchId must not be blank" }
        require(customerId.isNotBlank()) { "CustomerDebt customerId must not be blank" }
        require(referenceId.isNotBlank()) { "CustomerDebt referenceId must not be blank" }
        require(originalAmount > 0L) { "CustomerDebt originalAmount must be strictly positive" }
        require(remainingAmount in 0L..originalAmount) {
            "CustomerDebt remainingAmount ($remainingAmount) must be between 0 and originalAmount ($originalAmount)"
        }
        require(createdAt > 0L) { "CustomerDebt createdAt timestamp must be positive" }

        val expectedStatus = when {
            remainingAmount == originalAmount -> DebtStatus.UNPAID
            remainingAmount == 0L -> DebtStatus.SETTLED
            else -> DebtStatus.PARTIALLY_PAID
        }
        require(status == expectedStatus) {
            "CustomerDebt status $status does not match computed status $expectedStatus for remainingAmount $remainingAmount"
        }
    }

    /**
     * Applies a DebtPayment reducing the remainingAmount and updating the lifecycle status.
     */
    fun applyPayment(payment: DebtPayment): CustomerDebt {
        require(payment.businessId == businessId) {
            "Payment businessId ${payment.businessId} does not match debt businessId $businessId"
        }
        require(payment.branchId == branchId) {
            "Payment branchId ${payment.branchId} does not match debt branchId $branchId"
        }
        require(payment.debtType == DebtType.CUSTOMER) {
            "Payment debtType must be CUSTOMER"
        }
        require(payment.debtId == id) {
            "Payment debtId ${payment.debtId} does not match debt id $id"
        }
        require(status != DebtStatus.SETTLED) {
            "Cannot apply payment to already settled customer debt"
        }
        require(payment.amount <= remainingAmount) {
            "Payment amount ${payment.amount} exceeds remaining debt amount $remainingAmount"
        }

        val newRemaining = remainingAmount - payment.amount
        val newStatus = if (newRemaining == 0L) DebtStatus.SETTLED else DebtStatus.PARTIALLY_PAID

        return copy(
            remainingAmount = newRemaining,
            status = newStatus
        )
    }

    companion object {
        fun create(
            id: String,
            businessId: String,
            branchId: String,
            customerId: String,
            referenceType: DebtReferenceType,
            referenceId: String,
            originalAmount: Long,
            dueDate: Long? = null,
            createdAt: Long
        ): CustomerDebt {
            return CustomerDebt(
                id = id,
                businessId = businessId,
                branchId = branchId,
                customerId = customerId,
                referenceType = referenceType,
                referenceId = referenceId,
                originalAmount = originalAmount,
                remainingAmount = originalAmount,
                status = DebtStatus.UNPAID,
                dueDate = dueDate,
                createdAt = createdAt
            )
        }
    }
}
