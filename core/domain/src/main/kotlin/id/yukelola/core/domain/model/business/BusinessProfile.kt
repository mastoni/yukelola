package id.yukelola.core.domain.model.business

/**
 * Operational metadata, contact information, and receipt settings scoped to a Branch.
 */
data class BusinessProfile(
    val name: String,
    val phone: String? = null,
    val address: String? = null,
    val receiptHeader: String? = null,
    val receiptFooter: String? = null,
    val currency: String = "IDR",
    val timezone: String = "Asia/Jakarta"
) {
    init {
        require(name.isNotBlank()) { "BusinessProfile name must not be blank" }
        require(currency.isNotBlank()) { "Currency must not be blank" }
        require(timezone.isNotBlank()) { "Timezone must not be blank" }
    }
}
