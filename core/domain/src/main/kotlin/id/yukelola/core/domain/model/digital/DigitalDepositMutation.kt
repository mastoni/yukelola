package id.yukelola.core.domain.model.digital

/**
 * Traceable historical movement record of digital deposit funds.
 */
data class DigitalDepositMutation(
    val id: String,
    val businessId: String,
    val branchId: String,
    val accountId: String,
    val mutationType: DigitalDepositMutationType,
    val amount: Long,
    val balanceBefore: Long,
    val balanceAfter: Long,
    val referenceId: String? = null,
    val notes: String? = null,
    val createdAt: Long
) {
    init {
        require(id.isNotBlank()) { "DigitalDepositMutation id must not be blank" }
        require(businessId.isNotBlank()) { "DigitalDepositMutation businessId must not be blank" }
        require(branchId.isNotBlank()) { "DigitalDepositMutation branchId must not be blank" }
        require(accountId.isNotBlank()) { "DigitalDepositMutation accountId must not be blank" }
        require(amount > 0L) { "DigitalDepositMutation amount must be strictly positive" }
        require(balanceBefore >= 0L) { "DigitalDepositMutation balanceBefore must not be negative" }
        require(balanceAfter >= 0L) { "DigitalDepositMutation balanceAfter must not be negative" }
        require(createdAt > 0L) { "DigitalDepositMutation createdAt timestamp must be positive" }

        val isValidMath = when (mutationType) {
            DigitalDepositMutationType.TOP_UP,
            DigitalDepositMutationType.REFUND,
            DigitalDepositMutationType.REVERSAL -> balanceAfter == balanceBefore + amount

            DigitalDepositMutationType.DIGITAL_SALE -> balanceAfter == balanceBefore - amount

            DigitalDepositMutationType.ADJUSTMENT ->
                balanceAfter == balanceBefore + amount || balanceAfter == balanceBefore - amount
        }

        require(isValidMath) {
            "Mathematical inconsistency in deposit mutation: type=$mutationType, before=$balanceBefore, amount=$amount, after=$balanceAfter"
        }
    }
}
