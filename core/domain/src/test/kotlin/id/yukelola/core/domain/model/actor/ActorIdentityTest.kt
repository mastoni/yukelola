package id.yukelola.core.domain.model.actor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ActorIdentityTest {

    @Test
    fun `User identity is strictly distinct from Device identity`() {
        val user = User(
            id = "user-101",
            businessId = "biz-01",
            branchId = "br-01",
            username = "budi_kasir",
            fullName = "Budi Santoso",
            role = Role.CASHIER
        )

        val device = Device(
            id = "device-terminal-01",
            businessId = "biz-01",
            branchId = "br-01",
            deviceName = "POS Terminal 1",
            deviceType = DeviceType.SECONDARY_CASHIER_TERMINAL
        )

        assertNotEquals(user.id, device.id)
        assertTrue(user.isCashier)
        assertFalse(user.isOwner)
        assertFalse(user.isManager)
    }

    @Test
    fun `Role enum contains standard three tiers`() {
        val expected = setOf(Role.OWNER, Role.MANAGER, Role.CASHIER)
        assertEquals(3, Role.values().size)
        assertEquals(expected, Role.values().toSet())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `User with blank username throws exception`() {
        User(
            id = "user-01",
            businessId = "biz-01",
            username = "",
            fullName = "Tanpa Nama",
            role = Role.CASHIER
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Device with blank deviceName throws exception`() {
        Device(
            id = "dev-01",
            businessId = "biz-01",
            branchId = "br-01",
            deviceName = "   ",
            deviceType = DeviceType.MAIN_HOST_TABLET
        )
    }
}
