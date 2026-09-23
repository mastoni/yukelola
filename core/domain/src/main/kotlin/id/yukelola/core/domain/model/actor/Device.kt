package id.yukelola.core.domain.model.actor

/**
 * Physical Android terminal device registered and paired to an operational Branch.
 */
data class Device(
    val id: String,
    val businessId: String,
    val branchId: String,
    val deviceName: String,
    val deviceType: DeviceType,
    val isActive: Boolean = true
) {
    init {
        require(id.isNotBlank()) { "Device id must not be blank" }
        require(businessId.isNotBlank()) { "Device businessId must not be blank" }
        require(branchId.isNotBlank()) { "Device branchId must not be blank" }
        require(deviceName.isNotBlank()) { "Device deviceName must not be blank" }
    }
}
