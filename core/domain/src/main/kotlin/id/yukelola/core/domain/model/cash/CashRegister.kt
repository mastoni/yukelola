package id.yukelola.core.domain.model.cash

/**
 * Cash drawer aggregate tracking the actual physical cash balance and state for a Branch.
 */
data class CashRegister(
    val id: String,
    val businessId: String,
    val branchId: String,
    val name: String,
    val currentBalance: Long,
    val updatedAt: Long
) {
    init {
        require(id.isNotBlank()) { "CashRegister id must not be blank" }
        require(businessId.isNotBlank()) { "CashRegister businessId must not be blank" }
        require(branchId.isNotBlank()) { "CashRegister branchId must not be blank" }
        require(name.isNotBlank()) { "CashRegister name must not be blank" }
        require(currentBalance >= 0L) { "CashRegister currentBalance cannot be negative" }
        require(updatedAt > 0L) { "CashRegister updatedAt timestamp must be positive" }
    }

    /**
     * Applies a CashMutation to this register, updating the currentBalance deterministically.
     * Enforces strict branch, business, and register isolation.
     */
    fun applyMutation(mutation: CashMutation, newUpdatedAt: Long = mutation.createdAt): CashRegister {
        require(mutation.businessId == businessId) {
            "Mutation businessId ${mutation.businessId} does not match register businessId $businessId"
        }
        require(mutation.branchId == branchId) {
            "Mutation branchId ${mutation.branchId} does not match register branchId $branchId"
        }
        require(mutation.registerId == id) {
            "Mutation registerId ${mutation.registerId} does not match register id $id"
        }

        val newBalance = when (mutation.mutationType) {
            CashMutationType.INFLOW -> currentBalance + mutation.amount
            CashMutationType.OUTFLOW -> {
                val balance = currentBalance - mutation.amount
                require(balance >= 0L) {
                    "Cash outflow amount ${mutation.amount} exceeds current balance $currentBalance"
                }
                balance
            }
        }

        return copy(
            currentBalance = newBalance,
            updatedAt = newUpdatedAt
        )
    }
}
