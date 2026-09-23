package id.yukelola.core.domain.model.business

/**
 * Top-level Aggregate Root representing the enterprise or legal tenant entity.
 * Owns business-wide subscriptions, licenses, and global product catalog templates.
 */
data class Business(
    val id: String,
    val legalName: String,
    val ownerUserId: String,
    val createdAt: Long,
    val isActive: Boolean = true
) {
    init {
        require(id.isNotBlank()) { "Business id must not be blank" }
        require(legalName.isNotBlank()) { "Business legalName must not be blank" }
        require(ownerUserId.isNotBlank()) { "Business ownerUserId must not be blank" }
        require(createdAt > 0L) { "createdAt timestamp must be positive" }
    }
}
