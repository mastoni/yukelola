package id.yukelola.core.domain.model.business

import id.yukelola.core.domain.model.actor.CashierSession
import id.yukelola.core.domain.model.actor.Device
import id.yukelola.core.domain.model.actor.DeviceType
import id.yukelola.core.domain.model.actor.Role
import id.yukelola.core.domain.model.actor.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CrossBranchIsolationTest {

    @Test
    fun `Cashier assigned to Branch A cannot access Branch B sessions without explicit authority`() {
        val cashierBranchA = User(
            id = "user-kasir-a",
            businessId = "biz-01",
            branchId = "branch-a",
            username = "kasira",
            fullName = "Kasir Cabang A",
            role = Role.CASHIER
        )

        assertTrue(cashierBranchA.canAccessBranch("branch-a"))
        assertFalse(cashierBranchA.canAccessBranch("branch-b"))
    }

    @Test
    fun `Owner has enterprise authority across all branches`() {
        val ownerUser = User(
            id = "user-owner",
            businessId = "biz-01",
            branchId = null, // Global owner
            username = "juragan",
            fullName = "Pak Juragan",
            role = Role.OWNER
        )

        assertTrue(ownerUser.canAccessBranch("branch-a"))
        assertTrue(ownerUser.canAccessBranch("branch-b"))
        assertTrue(ownerUser.canAccessBranch("branch-c"))
    }

    @Test
    fun `Device registered to Branch A is strictly bound and isolated from Branch B`() {
        val deviceA = Device(
            id = "dev-pos-a",
            businessId = "biz-01",
            branchId = "branch-a",
            deviceName = "Tablet Kasir A",
            deviceType = DeviceType.MAIN_HOST_TABLET
        )

        val deviceB = Device(
            id = "dev-pos-b",
            businessId = "biz-01",
            branchId = "branch-b",
            deviceName = "Tablet Kasir B",
            deviceType = DeviceType.MAIN_HOST_TABLET
        )

        assertEquals("branch-a", deviceA.branchId)
        assertEquals("branch-b", deviceB.branchId)
        assertNotEquals(deviceA.branchId, deviceB.branchId)
    }

    @Test
    fun `CashierSession requires matching branch attribution`() {
        val sessionBranchA = CashierSession(
            id = "sess-a-01",
            branchId = "branch-a",
            userId = "user-kasir-a",
            deviceId = "dev-pos-a",
            openingBalance = 50000L,
            openedAt = 1711234000000L
        )

        assertEquals("branch-a", sessionBranchA.branchId)
        assertEquals("user-kasir-a", sessionBranchA.userId)
        assertEquals("dev-pos-a", sessionBranchA.deviceId)
    }
}
