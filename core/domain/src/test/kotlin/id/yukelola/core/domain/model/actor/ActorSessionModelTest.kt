package id.yukelola.core.domain.model.actor

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ActorSessionModelTest {

    @Test
    fun `Role enum contains standard three tiers`() {
        val expectedRoles = setOf(Role.OWNER, Role.MANAGER, Role.CASHIER)
        assertEquals(3, Role.values().size)
        assertEquals(expectedRoles, Role.values().toSet())
    }

    @Test
    fun `Device entity initializes with branch pairing`() {
        val device = Device(
            id = "dev-tab-01",
            businessId = "biz-123",
            branchId = "branch-01",
            deviceName = "Tablet Kasir Utama",
            deviceType = DeviceType.MAIN_HOST_TABLET,
            isActive = true
        )

        assertEquals("dev-tab-01", device.id)
        assertEquals(DeviceType.MAIN_HOST_TABLET, device.deviceType)
        assertTrue(device.isActive)
    }

    @Test
    fun `CashierSession correctly manages shift state and balances`() {
        val session = CashierSession(
            id = "sess-20260924-001",
            branchId = "branch-01",
            userId = "user-kasir-01",
            deviceId = "dev-tab-01",
            openingBalance = 100000L,
            openedAt = 1711234000000L,
            status = SessionStatus.OPEN
        )

        assertTrue(session.isOpen)
        assertEquals(100000L, session.openingBalance)
    }

    @Test
    fun `TransactionAttribution accurately captures audit metadata`() {
        val attribution = TransactionAttribution(
            businessId = "biz-123",
            branchId = "branch-01",
            userId = "user-kasir-01",
            deviceId = "dev-tab-01",
            cashierSessionId = "sess-001",
            createdAt = 1711234567000L
        )

        assertEquals("biz-123", attribution.businessId)
        assertEquals("branch-01", attribution.branchId)
        assertEquals("user-kasir-01", attribution.userId)
        assertEquals("dev-tab-01", attribution.deviceId)
        assertEquals("sess-001", attribution.cashierSessionId)
    }
}
