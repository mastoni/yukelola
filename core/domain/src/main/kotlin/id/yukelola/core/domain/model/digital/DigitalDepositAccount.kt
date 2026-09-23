package id.yukelola.core.domain.model.digital

/**
 * Dedicated branch agent deposit balance aggregate used exclusively for digital transaction fulfillment.
 *
 * Core Invariants:
 * 1. DigitalDepositAccount != CashRegister.
 * 2. Deposit top-up is internal capital relocation, NOT sales revenue or gross profit.
 * 3. Digital sales debit deposit by distributor costPrice.
 * 4. Compensating reversals restore deposit funds without destructive balance overwrites.
 */
data class DigitalDepositAccount(
    val id: String,
    val businessId: String,
    val branchId: String,
    val currentBalance: Long,
    val updatedAt: Long
) {
    init {
        require(id.isNotBlank()) { "DigitalDepositAccount id must not be blank" }
        require(businessId.isNotBlank()) { "DigitalDepositAccount businessId must not be blank" }
        require(branchId.isNotBlank()) { "DigitalDepositAccount branchId must not be blank" }
        require(currentBalance >= 0L) { "DigitalDepositAccount currentBalance cannot be negative" }
        require(updatedAt > 0L) { "DigitalDepositAccount updatedAt timestamp must be positive" }
    }

    /**
     * Applies a validated DigitalDepositMutation to this account.
     * Enforces strict branch, business, and balance continuity.
     */
    fun applyMutation(
        mutation: DigitalDepositMutation,
        newUpdatedAt: Long = mutation.createdAt
    ): DigitalDepositAccount {
        require(mutation.businessId == businessId) {
            "Mutation businessId ${mutation.businessId} does not match account businessId $businessId"
        }
        require(mutation.branchId == branchId) {
            "Mutation branchId ${mutation.branchId} does not match account branchId $branchId"
        }
        require(mutation.accountId == id) {
            "Mutation accountId ${mutation.accountId} does not match account id $id"
        }
        require(mutation.balanceBefore == currentBalance) {
            "Mutation balanceBefore (${mutation.balanceBefore}) does not match currentBalance ($currentBalance)"
        }
        require(newUpdatedAt >= updatedAt) {
            "newUpdatedAt must not be earlier than account updatedAt"
        }

        return copy(
            currentBalance = mutation.balanceAfter,
            updatedAt = newUpdatedAt
        )
    }
}
