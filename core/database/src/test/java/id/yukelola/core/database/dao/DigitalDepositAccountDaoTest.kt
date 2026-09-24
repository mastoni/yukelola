package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.DigitalDepositAccountEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.digital.DigitalDepositAccount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DigitalDepositAccountDaoTest {

    @Test
    fun `DigitalDepositAccountDao interface defines required persistence operations`() {
        val upsertMethod = DigitalDepositAccountDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("DigitalDepositAccountDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(DigitalDepositAccountEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = DigitalDepositAccountDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("DigitalDepositAccountDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(DigitalDepositAccountEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            DigitalDepositAccountDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("DigitalDepositAccountDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(DigitalDepositAccountEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByIdAndBusinessIdAndBranchIdMethod =
            DigitalDepositAccountDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessIdAndBranchId" }
        assertNotNull(
            "DigitalDepositAccountDao must declare findByIdAndBusinessIdAndBranchId method",
            findByIdAndBusinessIdAndBranchIdMethod
        )
        assertEquals(3, findByIdAndBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[2])
        assertEquals(DigitalDepositAccountEntity::class.java, findByIdAndBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdMethod =
            DigitalDepositAccountDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("DigitalDepositAccountDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            DigitalDepositAccountDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull(
            "DigitalDepositAccountDao must declare findByBusinessIdAndBranchId method",
            findByBusinessIdAndBranchIdMethod
        )
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates DigitalDepositAccountDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.DigitalDepositAccountDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement DigitalDepositAccountDao",
            DigitalDepositAccountDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : DigitalDepositAccountDao {
            private val storage = mutableMapOf<String, DigitalDepositAccountEntity>()

            override fun upsert(account: DigitalDepositAccountEntity) {
                storage[account.id] = account
            }

            override fun findById(id: String): DigitalDepositAccountEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): DigitalDepositAccountEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): DigitalDepositAccountEntity? {
                return storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
            }

            override fun findByBusinessId(businessId: String): List<DigitalDepositAccountEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedBy { it.id }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<DigitalDepositAccountEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedBy { it.id }
            }
        }

        val domainAccount = DigitalDepositAccount(
            id = "dep-test-01",
            businessId = "biz-101",
            branchId = "branch-101",
            currentBalance = 1500000L,
            updatedAt = 1700000000000L
        )

        // 1. Insert/Upsert
        fakeDao.upsert(domainAccount.toEntity())

        // 2. Retrieve by ID
        val retrievedEntity = fakeDao.findById("dep-test-01")
        assertNotNull("Retrieved entity must not be null", retrievedEntity)
        assertEquals("dep-test-01", retrievedEntity!!.id)
        assertEquals("biz-101", retrievedEntity.businessId)
        assertEquals("branch-101", retrievedEntity.branchId)
        assertEquals(1500000L, retrievedEntity.currentBalance)
        assertEquals(1700000000000L, retrievedEntity.updatedAt)

        // 3. Map back to domain
        val retrievedDomain = retrievedEntity.toDomain()
        assertEquals(domainAccount, retrievedDomain)

        // 4. Non-existent returns null
        assertNull(fakeDao.findById("missing-dep"))
    }

    @Test
    fun `business and branch isolation ensures digital deposit accounts remain strictly partitioned`() {
        val fakeDao = object : DigitalDepositAccountDao {
            private val storage = mutableMapOf<String, DigitalDepositAccountEntity>()

            override fun upsert(account: DigitalDepositAccountEntity) {
                storage[account.id] = account
            }

            override fun findById(id: String): DigitalDepositAccountEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): DigitalDepositAccountEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): DigitalDepositAccountEntity? {
                return storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
            }

            override fun findByBusinessId(businessId: String): List<DigitalDepositAccountEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedBy { it.id }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<DigitalDepositAccountEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedBy { it.id }
            }
        }

        val depA1 = DigitalDepositAccount(
            id = "dep-A1",
            businessId = "biz-A",
            branchId = "branch-A1",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        val depA2 = DigitalDepositAccount(
            id = "dep-A2",
            businessId = "biz-A",
            branchId = "branch-A2",
            currentBalance = 750000L,
            updatedAt = 2000L
        )

        val depB1 = DigitalDepositAccount(
            id = "dep-B1",
            businessId = "biz-B",
            branchId = "branch-B1",
            currentBalance = 1000000L,
            updatedAt = 3000L
        )

        fakeDao.upsert(depA1.toEntity())
        fakeDao.upsert(depA2.toEntity())
        fakeDao.upsert(depB1.toEntity())

        // Business A total
        val bizAAccounts = fakeDao.findByBusinessId("biz-A")
        assertEquals(2, bizAAccounts.size)

        // Branch A1 only
        val branchA1Accounts = fakeDao.findByBusinessIdAndBranchId("biz-A", "branch-A1")
        assertEquals(1, branchA1Accounts.size)
        assertEquals("dep-A1", branchA1Accounts[0].id)

        // Branch A2 only
        val branchA2Accounts = fakeDao.findByBusinessIdAndBranchId("biz-A", "branch-A2")
        assertEquals(1, branchA2Accounts.size)
        assertEquals("dep-A2", branchA2Accounts[0].id)

        // Cross-business and cross-branch checks
        assertNull(fakeDao.findByIdAndBusinessId("dep-A1", "biz-B"))
        assertNull(fakeDao.findByIdAndBusinessIdAndBranchId("dep-A1", "biz-A", "branch-A2"))
    }

    @Test
    fun `digital deposit account entity does NOT contain CashRegister or CashMutation fields`() {
        val fields = DigitalDepositAccountEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("DigitalDepositAccountEntity must NOT contain name", !fields.contains("name"))
        assertTrue("DigitalDepositAccountEntity must NOT contain registerId", !fields.contains("registerId"))
        assertTrue("DigitalDepositAccountEntity must NOT contain mutationType", !fields.contains("mutationType"))
        assertTrue("DigitalDepositAccountEntity must NOT contain category", !fields.contains("category"))
        assertTrue("DigitalDepositAccountEntity must NOT contain source", !fields.contains("source"))
    }

    @Test
    fun `digital deposit account entity does NOT contain DigitalTransaction or provider fields`() {
        val fields = DigitalDepositAccountEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("DigitalDepositAccountEntity must NOT contain providerName", !fields.contains("providerName"))
        assertTrue("DigitalDepositAccountEntity must NOT contain providerBalance", !fields.contains("providerBalance"))
        assertTrue("DigitalDepositAccountEntity must NOT contain transactionId", !fields.contains("transactionId"))
        assertTrue("DigitalDepositAccountEntity must NOT contain productCode", !fields.contains("productCode"))
        assertTrue("DigitalDepositAccountEntity must NOT contain profit", !fields.contains("profit"))
        assertTrue("DigitalDepositAccountEntity must NOT contain revenue", !fields.contains("revenue"))
    }
}
