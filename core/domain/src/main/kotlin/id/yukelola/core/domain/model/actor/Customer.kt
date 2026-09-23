package id.yukelola.core.domain.model.actor

/**
 * Buyer / Customer profile owned at the Business tenant level with optional home branch scope.
 *
 * Invariants:
 * 1. Scoped to Business tenant ([businessId]).
 * 2. Optional [branchId] specifies the customer's primary or registered branch location.
 * 3. Tracks non-negative [debtBalance] (piutang / kasbon).
 * 4. Customer does NOT directly mutate CustomerDebt, CashRegister, or Sale aggregates.
 */
data class Customer(
    val id: String,
    val businessId: String,
    val branchId: String? = null,
    val name: String,
    val phone: String? = null,
    val debtBalance: Long = 0L,
    val isActive: Boolean = true
) {
    init {
        require(id.isNotBlank()) { "Customer id must not be blank" }
        require(businessId.isNotBlank()) { "Customer businessId must not be blank" }
        branchId?.let { require(it.isNotBlank()) { "Customer branchId must not be blank when provided" } }
        require(name.isNotBlank()) { "Customer name must not be blank" }
        require(debtBalance >= 0L) { "Customer debtBalance must not be negative" }
    }

    companion object {
        /**
         * Validates customer business consistency when referencing Customer in transactions.
         */
        fun validateBusinessOwnership(customer: Customer, targetBusinessId: String) {
            require(customer.businessId == targetBusinessId) {
                "Customer businessId (${customer.businessId}) does not match target businessId ($targetBusinessId)"
            }
        }
    }
}
