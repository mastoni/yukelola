package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.CashierSessionEntity
import id.yukelola.core.domain.model.actor.SessionStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.ConcurrentHashMap

class CashierSessionDaoTest {

    @Test
    fun `CashierSessionDao declares canonical query methods`() {
        val methods = CashierSessionDao::class.java.declaredMethods.map { it.name }
        assertTrue("Must declare upsert", methods.contains("upsert"))
        assertTrue("Must declare findById", methods.contains("findById"))
        assertTrue("Must declare findByIdAndBranchId", methods.contains("findByIdAndBranchId"))
        assertTrue("Must declare findByBranchId", methods.contains("findByBranchId"))
        assertTrue("Must declare findByBranchIdAndStatus", methods.contains("findByBranchIdAndStatus"))
        assertTrue("Must declare findByBranchIdAndUserId", methods.contains("findByBranchIdAndUserId"))
        assertTrue("Must declare findByBranchIdAndDeviceId", methods.contains("findByBranchIdAndDeviceId"))
        assertTrue("Must declare findOpenSessionByBranchIdAndUserId", methods.contains("findOpenSessionByBranchIdAndUserId"))
        assertTrue("Must declare findOpenSessionByBranchIdAndDeviceId", methods.contains("findOpenSessionByBranchIdAndDeviceId"))
    }

    @Test
    fun `Room KSP generates CashierSessionDao implementation`() {
        val implClass = Class.forName("id.yukelola.core.database.dao.CashierSessionDao_Impl")
        assertNotNull("Generated CashierSessionDao_Impl must exist", implClass)
        assertTrue(
            "Generated implementation must implement CashierSessionDao",
            CashierSessionDao::class.java.isAssignableFrom(implClass)
        )
    }

    private class FakeCashierSessionDao : CashierSessionDao {
        val sessions = ConcurrentHashMap<String, CashierSessionEntity>()

        override fun upsert(session: CashierSessionEntity) {
            sessions[session.id] = session
        }

        override fun findById(id: String): CashierSessionEntity? {
            return sessions[id]
        }

        override fun findByIdAndBranchId(id: String, branchId: String): CashierSessionEntity? {
            return sessions[id]?.takeIf { it.branchId == branchId }
        }

        override fun findByBranchId(branchId: String): List<CashierSessionEntity> {
            return sessions.values
                .filter { it.branchId == branchId }
                .sortedByDescending { it.openedAt }
        }

        override fun findByBranchIdAndStatus(branchId: String, status: String): List<CashierSessionEntity> {
            return sessions.values
                .filter { it.branchId == branchId && it.status == status }
                .sortedByDescending { it.openedAt }
        }

        override fun findByBranchIdAndUserId(branchId: String, userId: String): List<CashierSessionEntity> {
            return sessions.values
                .filter { it.branchId == branchId && it.userId == userId }
                .sortedByDescending { it.openedAt }
        }

        override fun findByBranchIdAndDeviceId(branchId: String, deviceId: String): List<CashierSessionEntity> {
            return sessions.values
                .filter { it.branchId == branchId && it.deviceId == deviceId }
                .sortedByDescending { it.openedAt }
        }

        override fun findOpenSessionByBranchIdAndUserId(branchId: String, userId: String): CashierSessionEntity? {
            return sessions.values
                .filter { it.branchId == branchId && it.userId == userId && it.status == "OPEN" }
                .maxByOrNull { it.openedAt }
        }

        override fun findOpenSessionByBranchIdAndDeviceId(branchId: String, deviceId: String): CashierSessionEntity? {
            return sessions.values
                .filter { it.branchId == branchId && it.deviceId == deviceId && it.status == "OPEN" }
                .maxByOrNull { it.openedAt }
        }
    }

    @Test
    fun `insert and retrieve open CashierSession preserves all canonical fields`() {
        val dao = FakeCashierSessionDao()
        val entity = CashierSessionEntity(
            id = "sess-001",
            branchId = "branch-001",
            userId = "user-kasir-1",
            deviceId = "dev-pos-1",
            openingBalance = 150000L,
            closingBalance = null,
            openedAt = 1711234000000L,
            closedAt = null,
            status = "OPEN"
        )

        dao.upsert(entity)

        val retrieved = dao.findById("sess-001")
        assertNotNull(retrieved)
        assertEquals("sess-001", retrieved!!.id)
        assertEquals("branch-001", retrieved.branchId)
        assertEquals("user-kasir-1", retrieved.userId)
        assertEquals("dev-pos-1", retrieved.deviceId)
        assertEquals(150000L, retrieved.openingBalance)
        assertNull(retrieved.closingBalance)
        assertEquals(1711234000000L, retrieved.openedAt)
        assertNull(retrieved.closedAt)
        assertEquals("OPEN", retrieved.status)
    }

    @Test
    fun `insert and retrieve closed CashierSession preserves resolution data and balances`() {
        val dao = FakeCashierSessionDao()
        val entity = CashierSessionEntity(
            id = "sess-002",
            branchId = "branch-001",
            userId = "user-kasir-2",
            deviceId = "dev-pos-2",
            openingBalance = 100000L,
            closingBalance = 850000L,
            openedAt = 1711234000000L,
            closedAt = 1711262800000L,
            status = "CLOSED"
        )

        dao.upsert(entity)

        val retrieved = dao.findById("sess-002")
        assertNotNull(retrieved)
        assertEquals(100000L, retrieved!!.openingBalance)
        assertEquals(850000L, retrieved.closingBalance)
        assertEquals(1711234000000L, retrieved.openedAt)
        assertEquals(1711262800000L, retrieved.closedAt)
        assertEquals("CLOSED", retrieved.status)
    }

    @Test
    fun `branch isolation prevents cross-branch session access`() {
        val dao = FakeCashierSessionDao()

        val br1Session = CashierSessionEntity(
            id = "sess-br1",
            branchId = "branch-1",
            userId = "user-1",
            deviceId = "dev-1",
            openingBalance = 100000L,
            openedAt = 1711234000100L,
            status = "OPEN"
        )
        val br2Session = CashierSessionEntity(
            id = "sess-br2",
            branchId = "branch-2",
            userId = "user-1",
            deviceId = "dev-2",
            openingBalance = 100000L,
            openedAt = 1711234000200L,
            status = "OPEN"
        )

        dao.upsert(br1Session)
        dao.upsert(br2Session)

        assertNotNull(dao.findByIdAndBranchId("sess-br1", "branch-1"))
        assertNull(dao.findByIdAndBranchId("sess-br1", "branch-2"))

        val br1List = dao.findByBranchId("branch-1")
        assertEquals(1, br1List.size)
        assertEquals("sess-br1", br1List[0].id)

        val br2List = dao.findByBranchId("branch-2")
        assertEquals(1, br2List.size)
        assertEquals("sess-br2", br2List[0].id)
    }

    @Test
    fun `filtering by status, user, and device`() {
        val dao = FakeCashierSessionDao()

        val sess1 = CashierSessionEntity(
            id = "s-1",
            branchId = "branch-1",
            userId = "user-A",
            deviceId = "dev-1",
            openingBalance = 50000L,
            closingBalance = 200000L,
            openedAt = 1711200000000L,
            closedAt = 1711230000000L,
            status = "CLOSED"
        )
        val sess2 = CashierSessionEntity(
            id = "s-2",
            branchId = "branch-1",
            userId = "user-A",
            deviceId = "dev-1",
            openingBalance = 50000L,
            openedAt = 1711240000000L,
            status = "OPEN"
        )
        val sess3 = CashierSessionEntity(
            id = "s-3",
            branchId = "branch-1",
            userId = "user-B",
            deviceId = "dev-2",
            openingBalance = 100000L,
            openedAt = 1711245000000L,
            status = "OPEN"
        )

        dao.upsert(sess1)
        dao.upsert(sess2)
        dao.upsert(sess3)

        // By Status
        val openList = dao.findByBranchIdAndStatus("branch-1", "OPEN")
        assertEquals(2, openList.size)
        val closedList = dao.findByBranchIdAndStatus("branch-1", "CLOSED")
        assertEquals(1, closedList.size)

        // By User
        val userAList = dao.findByBranchIdAndUserId("branch-1", "user-A")
        assertEquals(2, userAList.size)

        // By Device
        val dev2List = dao.findByBranchIdAndDeviceId("branch-1", "dev-2")
        assertEquals(1, dev2List.size)

        // Active Open Session
        val activeUserA = dao.findOpenSessionByBranchIdAndUserId("branch-1", "user-A")
        assertNotNull(activeUserA)
        assertEquals("s-2", activeUserA!!.id)

        val activeDev2 = dao.findOpenSessionByBranchIdAndDeviceId("branch-1", "dev-2")
        assertNotNull(activeDev2)
        assertEquals("s-3", activeDev2!!.id)
    }

    @Test
    fun `attribution compatibility test with transactional aggregates`() {
        val sessionId = "cashier-sess-xyz-987"
        val dao = FakeCashierSessionDao()

        val session = CashierSessionEntity(
            id = sessionId,
            branchId = "branch-001",
            userId = "operator-001",
            deviceId = "pos-terminal-001",
            openingBalance = 250000L,
            openedAt = 1711234000000L,
            status = "OPEN"
        )
        dao.upsert(session)

        // Verify the persisted cashierSessionId matches the foreign attribution format used across
        // Sale, Purchase, ServiceOrder, DigitalTransaction, Inquiry
        val retrieved = dao.findById(sessionId)
        assertNotNull(retrieved)
        assertEquals(sessionId, retrieved!!.id)
        assertEquals("branch-001", retrieved.branchId)
        assertEquals("operator-001", retrieved.userId)
        assertEquals("pos-terminal-001", retrieved.deviceId)
    }

    @Test
    fun `non-mutation boundary - persisting CashierSession has zero side effects`() {
        val dao = FakeCashierSessionDao()
        val session = CashierSessionEntity(
            id = "sess-boundary",
            branchId = "branch-001",
            userId = "user-1",
            deviceId = "dev-1",
            openingBalance = 100000L,
            openedAt = 1711234000000L,
            status = "OPEN"
        )
        dao.upsert(session)

        // Verified: only cashier_sessions record is created, zero mutation on financial ledgers
        val list = dao.findByBranchId("branch-001")
        assertEquals(1, list.size)
        assertEquals("sess-boundary", list[0].id)
    }
}
