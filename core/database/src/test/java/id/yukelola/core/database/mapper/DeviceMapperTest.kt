package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.DeviceEntity
import id.yukelola.core.domain.model.actor.Device
import id.yukelola.core.domain.model.actor.DeviceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceMapperTest {

    @Test
    fun `toEntity maps Device domain fields losslessly`() {
        val domain = Device(
            id = "dev-pos-001",
            businessId = "biz-001",
            branchId = "branch-001",
            deviceName = "Tablet Kasir Utama",
            deviceType = DeviceType.MAIN_HOST_TABLET,
            isActive = true
        )

        val entity = DeviceMapper.toEntity(domain)

        assertEquals("dev-pos-001", entity.id)
        assertEquals("biz-001", entity.businessId)
        assertEquals("branch-001", entity.branchId)
        assertEquals("Tablet Kasir Utama", entity.deviceName)
        assertEquals("MAIN_HOST_TABLET", entity.deviceType)
        assertTrue(entity.isActive)
    }

    @Test
    fun `toDomain maps DeviceEntity fields losslessly`() {
        val entity = DeviceEntity(
            id = "dev-sec-002",
            businessId = "biz-001",
            branchId = "branch-002",
            deviceName = "Terminal Kasir 2",
            deviceType = "SECONDARY_CASHIER_TERMINAL",
            isActive = false
        )

        val domain = DeviceMapper.toDomain(entity)

        assertEquals("dev-sec-002", domain.id)
        assertEquals("biz-001", domain.businessId)
        assertEquals("branch-002", domain.branchId)
        assertEquals("Terminal Kasir 2", domain.deviceName)
        assertEquals(DeviceType.SECONDARY_CASHIER_TERMINAL, domain.deviceType)
        assertFalse(domain.isActive)
    }

    @Test
    fun `preserves all 4 canonical DeviceType enum values`() {
        val allTypes = DeviceType.values()
        assertEquals(4, allTypes.size)

        for (deviceType in allTypes) {
            val domain = Device(
                id = "dev-${deviceType.name}",
                businessId = "biz-001",
                branchId = "branch-001",
                deviceName = "Device ${deviceType.name}",
                deviceType = deviceType,
                isActive = true
            )

            val entity = DeviceMapper.toEntity(domain)
            assertEquals(deviceType.name, entity.deviceType)

            val roundTrip = DeviceMapper.toDomain(entity)
            assertEquals(deviceType, roundTrip.deviceType)
        }
    }
}
