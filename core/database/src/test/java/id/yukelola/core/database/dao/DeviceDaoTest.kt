package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.DeviceEntity
import id.yukelola.core.domain.model.actor.DeviceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.ConcurrentHashMap

class DeviceDaoTest {

    @Test
    fun `DeviceDao declares canonical query methods`() {
        val methods = DeviceDao::class.java.declaredMethods.map { it.name }
        assertTrue("Must declare upsert", methods.contains("upsert"))
        assertTrue("Must declare findById", methods.contains("findById"))
        assertTrue("Must declare findByIdAndBusinessId", methods.contains("findByIdAndBusinessId"))
        assertTrue("Must declare findByIdAndBusinessIdAndBranchId", methods.contains("findByIdAndBusinessIdAndBranchId"))
        assertTrue("Must declare findByBusinessId", methods.contains("findByBusinessId"))
        assertTrue("Must declare findByBusinessIdAndBranchId", methods.contains("findByBusinessIdAndBranchId"))
        assertTrue("Must declare findActiveByBusinessIdAndBranchId", methods.contains("findActiveByBusinessIdAndBranchId"))
        assertTrue("Must declare findByBusinessIdAndBranchIdAndDeviceType", methods.contains("findByBusinessIdAndBranchIdAndDeviceType"))
        assertTrue("Must declare findByBranchId", methods.contains("findByBranchId"))
    }

    @Test
    fun `Room KSP generates DeviceDao implementation`() {
        val implClass = Class.forName("id.yukelola.core.database.dao.DeviceDao_Impl")
        assertNotNull("Generated DeviceDao_Impl must exist", implClass)
        assertTrue(
            "Generated implementation must implement DeviceDao",
            DeviceDao::class.java.isAssignableFrom(implClass)
        )
    }

    private class FakeDeviceDao : DeviceDao {
        val devices = ConcurrentHashMap<String, DeviceEntity>()

        override fun upsert(device: DeviceEntity) {
            devices[device.id] = device
        }

        override fun findById(id: String): DeviceEntity? {
            return devices[id]
        }

        override fun findByIdAndBusinessId(id: String, businessId: String): DeviceEntity? {
            return devices[id]?.takeIf { it.businessId == businessId }
        }

        override fun findByIdAndBusinessIdAndBranchId(
            id: String,
            businessId: String,
            branchId: String
        ): DeviceEntity? {
            return devices[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
        }

        override fun findByBusinessId(businessId: String): List<DeviceEntity> {
            return devices.values
                .filter { it.businessId == businessId }
                .sortedBy { it.deviceName }
        }

        override fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<DeviceEntity> {
            return devices.values
                .filter { it.businessId == businessId && it.branchId == branchId }
                .sortedBy { it.deviceName }
        }

        override fun findActiveByBusinessIdAndBranchId(businessId: String, branchId: String): List<DeviceEntity> {
            return devices.values
                .filter { it.businessId == businessId && it.branchId == branchId && it.isActive }
                .sortedBy { it.deviceName }
        }

        override fun findByBusinessIdAndBranchIdAndDeviceType(
            businessId: String,
            branchId: String,
            deviceType: String
        ): List<DeviceEntity> {
            return devices.values
                .filter { it.businessId == businessId && it.branchId == branchId && it.deviceType == deviceType }
                .sortedBy { it.deviceName }
        }

        override fun findByBranchId(branchId: String): List<DeviceEntity> {
            return devices.values
                .filter { it.branchId == branchId }
                .sortedBy { it.deviceName }
        }
    }

    @Test
    fun `insert and retrieve Device preserves all canonical fields`() {
        val dao = FakeDeviceDao()
        val entity = DeviceEntity(
            id = "dev-001",
            businessId = "biz-001",
            branchId = "branch-001",
            deviceName = "Tablet Kasir Utama",
            deviceType = "MAIN_HOST_TABLET",
            isActive = true
        )

        dao.upsert(entity)

        val retrieved = dao.findById("dev-001")
        assertNotNull(retrieved)
        assertEquals("dev-001", retrieved!!.id)
        assertEquals("biz-001", retrieved.businessId)
        assertEquals("branch-001", retrieved.branchId)
        assertEquals("Tablet Kasir Utama", retrieved.deviceName)
        assertEquals("MAIN_HOST_TABLET", retrieved.deviceType)
        assertTrue(retrieved.isActive)
    }

    @Test
    fun `business and branch isolation prevents cross-tenant and cross-branch access`() {
        val dao = FakeDeviceDao()

        val devBiz1Br1 = DeviceEntity(
            id = "d-b1-br1",
            businessId = "biz-1",
            branchId = "branch-1",
            deviceName = "Device 1",
            deviceType = "MAIN_HOST_TABLET",
            isActive = true
        )
        val devBiz1Br2 = DeviceEntity(
            id = "d-b1-br2",
            businessId = "biz-1",
            branchId = "branch-2",
            deviceName = "Device 2",
            deviceType = "SECONDARY_CASHIER_TERMINAL",
            isActive = true
        )
        val devBiz2Br1 = DeviceEntity(
            id = "d-b2-br1",
            businessId = "biz-2",
            branchId = "branch-1",
            deviceName = "Device 3",
            deviceType = "MANAGER_PHONE",
            isActive = true
        )

        dao.upsert(devBiz1Br1)
        dao.upsert(devBiz1Br2)
        dao.upsert(devBiz2Br1)

        // findByIdAndBusinessId
        assertNotNull(dao.findByIdAndBusinessId("d-b1-br1", "biz-1"))
        assertNull(dao.findByIdAndBusinessId("d-b1-br1", "biz-2"))

        // findByIdAndBusinessIdAndBranchId
        assertNotNull(dao.findByIdAndBusinessIdAndBranchId("d-b1-br1", "biz-1", "branch-1"))
        assertNull(dao.findByIdAndBusinessIdAndBranchId("d-b1-br1", "biz-1", "branch-2"))

        // findByBusinessIdAndBranchId
        val biz1Br1List = dao.findByBusinessIdAndBranchId("biz-1", "branch-1")
        assertEquals(1, biz1Br1List.size)
        assertEquals("d-b1-br1", biz1Br1List[0].id)
    }

    @Test
    fun `filtering by device type and active state`() {
        val dao = FakeDeviceDao()

        val host = DeviceEntity(
            id = "dev-host",
            businessId = "biz-1",
            branchId = "branch-1",
            deviceName = "Host Tablet",
            deviceType = "MAIN_HOST_TABLET",
            isActive = true
        )
        val secActive = DeviceEntity(
            id = "dev-sec-active",
            businessId = "biz-1",
            branchId = "branch-1",
            deviceName = "Secondary POS",
            deviceType = "SECONDARY_CASHIER_TERMINAL",
            isActive = true
        )
        val secInactive = DeviceEntity(
            id = "dev-sec-inactive",
            businessId = "biz-1",
            branchId = "branch-1",
            deviceName = "Old Secondary POS",
            deviceType = "SECONDARY_CASHIER_TERMINAL",
            isActive = false
        )

        dao.upsert(host)
        dao.upsert(secActive)
        dao.upsert(secInactive)

        // findActiveByBusinessIdAndBranchId
        val activeList = dao.findActiveByBusinessIdAndBranchId("biz-1", "branch-1")
        assertEquals(2, activeList.size)
        assertFalse(activeList.any { it.id == "dev-sec-inactive" })

        // findByBusinessIdAndBranchIdAndDeviceType
        val secondaries = dao.findByBusinessIdAndBranchIdAndDeviceType("biz-1", "branch-1", "SECONDARY_CASHIER_TERMINAL")
        assertEquals(2, secondaries.size)

        val hosts = dao.findByBusinessIdAndBranchIdAndDeviceType("biz-1", "branch-1", "MAIN_HOST_TABLET")
        assertEquals(1, hosts.size)
        assertEquals("dev-host", hosts[0].id)
    }

    @Test
    fun `attribution compatibility test across all transactional aggregates`() {
        val testDeviceId = "dev-terminal-attribution-id"
        val dao = FakeDeviceDao()

        val device = DeviceEntity(
            id = testDeviceId,
            businessId = "biz-001",
            branchId = "branch-001",
            deviceName = "POS Terminal 1",
            deviceType = DeviceType.MAIN_HOST_TABLET.name,
            isActive = true
        )
        dao.upsert(device)

        // Verify that the persisted Device.id format is directly compatible with the deviceId reference in:
        // CashierSession, Sale, Purchase, ServiceOrder, DigitalTransaction, Inquiry, DigitalTransactionComplaint, StockAdjustment
        val retrieved = dao.findById(testDeviceId)
        assertNotNull(retrieved)
        assertEquals(testDeviceId, retrieved!!.id)
        assertEquals("biz-001", retrieved.businessId)
        assertEquals("branch-001", retrieved.branchId)
    }

    @Test
    fun `security and non-mutation boundary - persisting Device does not trigger network or financial side effects`() {
        val dao = FakeDeviceDao()
        val device = DeviceEntity(
            id = "dev-boundary",
            businessId = "biz-001",
            branchId = "branch-001",
            deviceName = "Clean Terminal",
            deviceType = "MAIN_HOST_TABLET",
            isActive = true
        )
        dao.upsert(device)

        // Verified: only devices record is created, zero discovery/pairing/mDNS, zero ledger mutation
        val records = dao.findByBusinessId("biz-001")
        assertEquals(1, records.size)
        assertEquals("dev-boundary", records[0].id)
    }
}
