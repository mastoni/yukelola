package id.yukelola.core.domain.model.business

/**
 * Operational store, outlet, or branch location entity under a Business tenant.
 * Represents the boundary of local operational data sovereignty (isolated stock, cash drawer, and sessions).
 */
data class Branch(
    val id: String,
    val businessId: String,
    val code: String,
    val name: String,
    val businessProfile: BusinessProfile,
    val businessModel: BusinessModel,
    val enabledCapabilities: Set<Capability> = emptySet(),
    val isActive: Boolean = true
) {
    init {
        require(id.isNotBlank()) { "Branch id must not be blank" }
        require(businessId.isNotBlank()) { "Branch businessId must not be blank" }
        require(code.isNotBlank()) { "Branch code must not be blank" }
        require(name.isNotBlank()) { "Branch name must not be blank" }
    }

    /**
     * Checks if a specific capability is enabled for this branch.
     */
    fun hasCapability(capability: Capability): Boolean = enabledCapabilities.contains(capability)

    /**
     * Produces a branch copy with an enabled capability added.
     */
    fun withCapability(capability: Capability): Branch = copy(enabledCapabilities = enabledCapabilities + capability)

    /**
     * Produces a branch copy with a capability removed.
     */
    fun withoutCapability(capability: Capability): Branch = copy(enabledCapabilities = enabledCapabilities - capability)
}
