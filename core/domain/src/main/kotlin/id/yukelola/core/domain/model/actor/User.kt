package id.yukelola.core.domain.model.actor

/**
 * System operator or staff member within a Business / Branch.
 */
data class User(
    val id: String,
    val businessId: String,
    val branchId: String? = null,
    val username: String,
    val fullName: String,
    val role: Role,
    val isActive: Boolean = true
) {
    init {
        require(id.isNotBlank()) { "User id must not be blank" }
        require(businessId.isNotBlank()) { "User businessId must not be blank" }
        require(username.isNotBlank()) { "User username must not be blank" }
        require(fullName.isNotBlank()) { "User fullName must not be blank" }
    }
}
