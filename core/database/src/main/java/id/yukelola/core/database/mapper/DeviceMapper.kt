package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.DeviceEntity
import id.yukelola.core.domain.model.actor.Device
import id.yukelola.core.domain.model.actor.DeviceType

/**
 * Lossless bidirectional mapper between [Device] domain aggregate and [DeviceEntity].
 * Storage only: contains no Android API calls, discovery, pairing, or licensing logic.
 */
object DeviceMapper {

    fun toEntity(domain: Device): DeviceEntity {
        return DeviceEntity(
            id = domain.id,
            businessId = domain.businessId,
            branchId = domain.branchId,
            deviceName = domain.deviceName,
            deviceType = domain.deviceType.name,
            isActive = domain.isActive
        )
    }

    fun toDomain(entity: DeviceEntity): Device {
        return Device(
            id = entity.id,
            businessId = entity.businessId,
            branchId = entity.branchId,
            deviceName = entity.deviceName,
            deviceType = DeviceType.valueOf(entity.deviceType),
            isActive = entity.isActive
        )
    }
}
