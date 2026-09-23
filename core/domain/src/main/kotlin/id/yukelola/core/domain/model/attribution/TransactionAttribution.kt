package id.yukelola.core.domain.model.attribution

/**
 * Standard transactional attribution value object guaranteeing end-to-end accountability.
 * Attribution metadata is captured on all persistent transactional aggregates.
 */
data class TransactionAttribution(
    val businessId: String,
    val branchId: String,
    val userId: String,
    val deviceId: String,
    val cashierSessionId: String? = null,
    val createdAt: Long
) {
    init {
        require(businessId.isNotBlank()) { "Attribution businessId must not be blank" }
        require(branchId.isNotBlank()) { "Attribution branchId must not be blank" }
        require(userId.isNotBlank()) { "Attribution userId must not be blank" }
        require(deviceId.isNotBlank()) { "Attribution deviceId must not be blank" }
        require(createdAt > 0L) { "Attribution createdAt timestamp must be positive" }
    }
}
