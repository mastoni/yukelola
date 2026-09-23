package id.yukelola.core.domain.model.actor

/**
 * Vendor / Supplier profile owned at the Business tenant level.
 *
 * Invariant:
 * Tracks business-scoped vendor profile and cumulative debt payable balance.
 */
data class Supplier(
    val id: String,
    val businessId: String,
    val name: String,
    val phone: String? = null,
    val debtBalance: Long = 0L,
    val isActive: Boolean = true
) {
    init {
        require(id.isNotBlank()) { "Supplier id must not be blank" }
        require(businessId.isNotBlank()) { "Supplier businessId must not be blank" }
        require(name.isNotBlank()) { "Supplier name must not be blank" }
        require(debtBalance >= 0L) { "Supplier debtBalance must not be negative" }
    }
}
