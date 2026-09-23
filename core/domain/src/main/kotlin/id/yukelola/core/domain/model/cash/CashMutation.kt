package id.yukelola.core.domain.model.cash

/**
 * Historical cash drawer movement record representing physical cash inflow or outflow.
 */
data class CashMutation(
    val id: String,
    val businessId: String,
    val branchId: String,
    val registerId: String,
    val cashierSessionId: String? = null,
    val mutationType: CashMutationType,
    val category: CashMutationCategory,
    val amount: Long,
    val source: String,
    val referenceId: String? = null,
    val notes: String? = null,
    val createdAt: Long
) {
    init {
        require(id.isNotBlank()) { "CashMutation id must not be blank" }
        require(businessId.isNotBlank()) { "CashMutation businessId must not be blank" }
        require(branchId.isNotBlank()) { "CashMutation branchId must not be blank" }
        require(registerId.isNotBlank()) { "CashMutation registerId must not be blank" }
        require(source.isNotBlank()) { "CashMutation source must not be blank" }
        require(amount > 0L) { "CashMutation amount must be strictly positive" }
        require(createdAt > 0L) { "CashMutation createdAt timestamp must be positive" }
    }
}
