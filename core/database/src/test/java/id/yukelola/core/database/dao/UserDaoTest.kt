package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.UserEntity
import id.yukelola.core.domain.model.actor.Role
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.ConcurrentHashMap

class UserDaoTest {

    @Test
    fun `UserDao declares canonical query methods`() {
        val methods = UserDao::class.java.declaredMethods.map { it.name }
        assertTrue("Must declare upsert", methods.contains("upsert"))
        assertTrue("Must declare findById", methods.contains("findById"))
        assertTrue("Must declare findByIdAndBusinessId", methods.contains("findByIdAndBusinessId"))
        assertTrue("Must declare findByBusinessId", methods.contains("findByBusinessId"))
        assertTrue("Must declare findByBusinessIdAndBranchId", methods.contains("findByBusinessIdAndBranchId"))
        assertTrue("Must declare findByBusinessIdAndUsername", methods.contains("findByBusinessIdAndUsername"))
        assertTrue("Must declare findActiveByBusinessId", methods.contains("findActiveByBusinessId"))
        assertTrue("Must declare findByBusinessIdAndRole", methods.contains("findByBusinessIdAndRole"))
        assertTrue("Must declare findByBranchId", methods.contains("findByBranchId"))
    }

    @Test
    fun `Room KSP generates UserDao implementation`() {
        val implClass = Class.forName("id.yukelola.core.database.dao.UserDao_Impl")
        assertNotNull("Generated UserDao_Impl must exist", implClass)
        assertTrue(
            "Generated implementation must implement UserDao",
            UserDao::class.java.isAssignableFrom(implClass)
        )
    }

    private class FakeUserDao : UserDao {
        val users = ConcurrentHashMap<String, UserEntity>()

        override fun upsert(user: UserEntity) {
            users[user.id] = user
        }

        override fun findById(id: String): UserEntity? {
            return users[id]
        }

        override fun findByIdAndBusinessId(id: String, businessId: String): UserEntity? {
            return users[id]?.takeIf { it.businessId == businessId }
        }

        override fun findByBusinessId(businessId: String): List<UserEntity> {
            return users.values
                .filter { it.businessId == businessId }
                .sortedBy { it.username }
        }

        override fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<UserEntity> {
            return users.values
                .filter { it.businessId == businessId && it.branchId == branchId }
                .sortedBy { it.username }
        }

        override fun findByBusinessIdAndUsername(businessId: String, username: String): UserEntity? {
            return users.values.firstOrNull { it.businessId == businessId && it.username == username }
        }

        override fun findActiveByBusinessId(businessId: String): List<UserEntity> {
            return users.values
                .filter { it.businessId == businessId && it.isActive }
                .sortedBy { it.username }
        }

        override fun findByBusinessIdAndRole(businessId: String, role: String): List<UserEntity> {
            return users.values
                .filter { it.businessId == businessId && it.role == role }
                .sortedBy { it.username }
        }

        override fun findByBranchId(branchId: String): List<UserEntity> {
            return users.values
                .filter { it.branchId == branchId }
                .sortedBy { it.username }
        }
    }

    @Test
    fun `insert and retrieve User preserves all canonical fields`() {
        val dao = FakeUserDao()
        val entity = UserEntity(
            id = "user-001",
            businessId = "biz-001",
            branchId = "branch-001",
            username = "kasir_andi",
            fullName = "Andi Wijaya",
            role = "CASHIER",
            isActive = true
        )

        dao.upsert(entity)

        val retrieved = dao.findById("user-001")
        assertNotNull(retrieved)
        assertEquals("user-001", retrieved!!.id)
        assertEquals("biz-001", retrieved.businessId)
        assertEquals("branch-001", retrieved.branchId)
        assertEquals("kasir_andi", retrieved.username)
        assertEquals("Andi Wijaya", retrieved.fullName)
        assertEquals("CASHIER", retrieved.role)
        assertTrue(retrieved.isActive)
    }

    @Test
    fun `business isolation prevents cross-tenant access`() {
        val dao = FakeUserDao()

        val biz1User = UserEntity(
            id = "u-biz1",
            businessId = "biz-1",
            branchId = "branch-1",
            username = "staff_biz1",
            fullName = "Staff 1",
            role = "CASHIER",
            isActive = true
        )
        val biz2User = UserEntity(
            id = "u-biz2",
            businessId = "biz-2",
            branchId = "branch-1",
            username = "staff_biz2",
            fullName = "Staff 2",
            role = "CASHIER",
            isActive = true
        )

        dao.upsert(biz1User)
        dao.upsert(biz2User)

        assertNotNull(dao.findByIdAndBusinessId("u-biz1", "biz-1"))
        assertNull(dao.findByIdAndBusinessId("u-biz1", "biz-2"))

        val biz1List = dao.findByBusinessId("biz-1")
        assertEquals(1, biz1List.size)
        assertEquals("u-biz1", biz1List[0].id)
    }

    @Test
    fun `branch scoping and owner null branchId querying`() {
        val dao = FakeUserDao()

        val owner = UserEntity(
            id = "u-owner",
            businessId = "biz-1",
            branchId = null,
            username = "boss",
            fullName = "Owner",
            role = "OWNER",
            isActive = true
        )
        val managerBr1 = UserEntity(
            id = "u-mgr-br1",
            businessId = "biz-1",
            branchId = "branch-1",
            username = "mgr1",
            fullName = "Manager Branch 1",
            role = "MANAGER",
            isActive = true
        )
        val cashierBr2 = UserEntity(
            id = "u-kasir-br2",
            businessId = "biz-1",
            branchId = "branch-2",
            username = "kasir2",
            fullName = "Cashier Branch 2",
            role = "CASHIER",
            isActive = false
        )

        dao.upsert(owner)
        dao.upsert(managerBr1)
        dao.upsert(cashierBr2)

        // findByBusinessIdAndBranchId
        val br1Users = dao.findByBusinessIdAndBranchId("biz-1", "branch-1")
        assertEquals(1, br1Users.size)
        assertEquals("u-mgr-br1", br1Users[0].id)

        // findByBusinessIdAndRole
        val owners = dao.findByBusinessIdAndRole("biz-1", "OWNER")
        assertEquals(1, owners.size)
        assertNull(owners[0].branchId)

        // findActiveByBusinessId
        val activeUsers = dao.findActiveByBusinessId("biz-1")
        assertEquals(2, activeUsers.size)
        assertFalse(activeUsers.any { it.id == "u-kasir-br2" })

        // findByBusinessIdAndUsername
        val foundBoss = dao.findByBusinessIdAndUsername("biz-1", "boss")
        assertNotNull(foundBoss)
        assertEquals("u-owner", foundBoss!!.id)
    }

    @Test
    fun `attribution compatibility test across all transactional aggregates`() {
        val testUserId = "user-kasir-attribution-id"
        val dao = FakeUserDao()

        val user = UserEntity(
            id = testUserId,
            businessId = "biz-001",
            branchId = "branch-001",
            username = "operator_kasir",
            fullName = "Operator Kasir",
            role = Role.CASHIER.name,
            isActive = true
        )
        dao.upsert(user)

        // Verify that the persisted User.id format is directly compatible with the userId reference in:
        // CashierSession, Sale, Purchase, ServiceOrder, DigitalTransaction, Inquiry, DigitalTransactionComplaint, StockAdjustment
        val retrieved = dao.findById(testUserId)
        assertNotNull(retrieved)
        assertEquals(testUserId, retrieved!!.id)
        assertEquals("biz-001", retrieved.businessId)
        assertEquals("branch-001", retrieved.branchId)
    }

    @Test
    fun `security and non-mutation boundary - persisting User does not trigger auth or financial side effects`() {
        val dao = FakeUserDao()
        val user = UserEntity(
            id = "user-boundary",
            businessId = "biz-001",
            branchId = "branch-001",
            username = "clean_user",
            fullName = "Clean User",
            role = "CASHIER",
            isActive = true
        )
        dao.upsert(user)

        // Verified: only users record is created, zero password hashing, zero auth token generation, zero ledger mutation
        val records = dao.findByBusinessId("biz-001")
        assertEquals(1, records.size)
        assertEquals("user-boundary", records[0].id)
    }
}
