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

    /**
     * Whether this user is an enterprise owner with business-wide authority.
     */
    val isOwner: Boolean
        get() = role == Role.OWNER

    /**
     * Whether this user is a branch manager.
     */
    val isManager: Boolean
        get() = role == Role.MANAGER

    /**
     * Whether this user is a shift cashier.
     */
    val isCashier: Boolean
        get() = role == Role.CASHIER

    /**
     * Checks if this user has authority to access or operate within a target branch.
     * Owners have enterprise-wide access; managers and cashiers are scoped to their assigned branch.
     */
    fun canAccessBranch(targetBranchId: String): Boolean {
        return isOwner || branchId == targetBranchId
    }
}
