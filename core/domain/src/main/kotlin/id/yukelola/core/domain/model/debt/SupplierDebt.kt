package id.yukelola.core.domain.model.debt

/**
 * Outstanding credit payable owed by a branch to a vendor / supplier (Hutang Pemasok).
 */
data class SupplierDebt(
    val id: String,
    val businessId: String,
    val branchId: String,
    val supplierId: String,
    val purchaseId: String,
    val originalAmount: Long,
    val remainingAmount: Long,
    val status: DebtStatus,
    val dueDate: Long? = null,
    val createdAt: Long
) {
    init {
        require(id.isNotBlank()) { "SupplierDebt id must not be blank" }
        require(businessId.isNotBlank()) { "SupplierDebt businessId must not be blank" }
        require(branchId.isNotBlank()) { "SupplierDebt branchId must not be blank" }
        require(supplierId.isNotBlank()) { "SupplierDebt supplierId must not be blank" }
        require(purchaseId.isNotBlank()) { "SupplierDebt purchaseId must not be blank" }
        require(originalAmount > 0L) { "SupplierDebt originalAmount must be strictly positive" }
        require(remainingAmount in 0L..originalAmount) {
            "SupplierDebt remainingAmount ($remainingAmount) must be between 0 and originalAmount ($originalAmount)"
        }
        require(createdAt > 0L) { "SupplierDebt createdAt timestamp must be positive" }

        val expectedStatus = when {
            remainingAmount == originalAmount -> DebtStatus.UNPAID
            remainingAmount == 0L -> DebtStatus.SETTLED
            else -> DebtStatus.PARTIALLY_PAID
        }
        require(status == expectedStatus) {
            "SupplierDebt status $status does not match computed status $expectedStatus for remainingAmount $remainingAmount"
        }
    }

    /**
     * Applies a DebtPayment reducing the remainingAmount and updating the lifecycle status.
     */
    fun applyPayment(payment: DebtPayment): SupplierDebt {
        require(payment.businessId == businessId) {
            "Payment businessId ${payment.businessId} does not match debt businessId $businessId"
        }
        require(payment.branchId == branchId) {
            "Payment branchId ${payment.branchId} does not match debt branchId $branchId"
        }
        require(payment.debtType == DebtType.SUPPLIER) {
            "Payment debtType must be SUPPLIER"
        }
        require(payment.debtId == id) {
            "Payment debtId ${payment.debtId} does not match debt id $id"
        }
        require(status != DebtStatus.SETTLED) {
            "Cannot apply payment to already settled supplier debt"
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
            supplierId: String,
            purchaseId: String,
            originalAmount: Long,
            dueDate: Long? = null,
            createdAt: Long
        ): SupplierDebt {
            return SupplierDebt(
                id = id,
                businessId = businessId,
                branchId = branchId,
                supplierId = supplierId,
                purchaseId = purchaseId,
                originalAmount = originalAmount,
                remainingAmount = originalAmount,
                status = DebtStatus.UNPAID,
                dueDate = dueDate,
                createdAt = createdAt
            )
        }
    }
}
