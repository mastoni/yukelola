package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.DigitalDepositMutationEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.digital.DigitalDepositMutation
import id.yukelola.core.domain.model.digital.DigitalDepositMutationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DigitalDepositMutationDaoTest {

    @Test
    fun `DigitalDepositMutationDao interface defines required persistence operations`() {
        val upsertMethod = DigitalDepositMutationDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("DigitalDepositMutationDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(DigitalDepositMutationEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = DigitalDepositMutationDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("DigitalDepositMutationDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(DigitalDepositMutationEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            DigitalDepositMutationDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("DigitalDepositMutationDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(DigitalDepositMutationEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByIdAndBusinessIdAndBranchIdMethod =
            DigitalDepositMutationDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessIdAndBranchId" }
        assertNotNull(
            "DigitalDepositMutationDao must declare findByIdAndBusinessIdAndBranchId method",
            findByIdAndBusinessIdAndBranchIdMethod
        )
        assertEquals(3, findByIdAndBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[2])
        assertEquals(DigitalDepositMutationEntity::class.java, findByIdAndBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdMethod =
            DigitalDepositMutationDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("DigitalDepositMutationDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            DigitalDepositMutationDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull("DigitalDepositMutationDao must declare findByBusinessIdAndBranchId method", findByBusinessIdAndBranchIdMethod)
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdAndAccountIdMethod =
            DigitalDepositMutationDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndAccountId" }
        assertNotNull("DigitalDepositMutationDao must declare findByBusinessIdAndAccountId method", findByBusinessIdAndAccountIdMethod)
        assertEquals(2, findByBusinessIdAndAccountIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndAccountIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndAccountIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndAccountIdMethod.returnType)

        val findByBusinessIdAndBranchIdAndAccountIdMethod =
            DigitalDepositMutationDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndAccountId" }
        assertNotNull(
            "DigitalDepositMutationDao must declare findByBusinessIdAndBranchIdAndAccountId method",
            findByBusinessIdAndBranchIdAndAccountIdMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndAccountIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndAccountIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndAccountIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndAccountIdMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndAccountIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates DigitalDepositMutationDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.DigitalDepositMutationDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement DigitalDepositMutationDao",
            DigitalDepositMutationDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : DigitalDepositMutationDao {
            private val storage = mutableMapOf<String, DigitalDepositMutationEntity>()

            override fun upsert(mutation: DigitalDepositMutationEntity) {
                storage[mutation.id] = mutation
            }

            override fun findById(id: String): DigitalDepositMutationEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): DigitalDepositMutationEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): DigitalDepositMutationEntity? {
                return storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
            }

            override fun findByBusinessId(businessId: String): List<DigitalDepositMutationEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<DigitalDepositMutationEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndAccountId(
                businessId: String,
                accountId: String
            ): List<DigitalDepositMutationEntity> {
                return storage.values.filter { it.businessId == businessId && it.accountId == accountId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchIdAndAccountId(
                businessId: String,
                branchId: String,
                accountId: String
            ): List<DigitalDepositMutationEntity> {
                return storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.accountId == accountId
                }.sortedByDescending { it.createdAt }
            }
        }

        val domainMutation = DigitalDepositMutation(
            id = "mut-test-01",
            businessId = "biz-101",
            branchId = "branch-101",
            accountId = "dep-101",
            mutationType = DigitalDepositMutationType.TOP_UP,
            amount = 500000L,
            balanceBefore = 1000000L,
            balanceAfter = 1500000L,
            referenceId = "TOPUP-101",
            notes = "Top up saldo kasir",
            createdAt = 1700000000000L
        )

        // 1. Insert/Upsert
        fakeDao.upsert(domainMutation.toEntity())

        // 2. Retrieve by ID
        val retrievedEntity = fakeDao.findById("mut-test-01")
        assertNotNull("Retrieved entity must not be null", retrievedEntity)
        assertEquals("mut-test-01", retrievedEntity!!.id)
        assertEquals("biz-101", retrievedEntity.businessId)
        assertEquals("branch-101", retrievedEntity.branchId)
        assertEquals("dep-101", retrievedEntity.accountId)
        assertEquals("TOP_UP", retrievedEntity.mutationType)
        assertEquals(500000L, retrievedEntity.amount)
        assertEquals(1000000L, retrievedEntity.balanceBefore)
        assertEquals(1500000L, retrievedEntity.balanceAfter)
        assertEquals("TOPUP-101", retrievedEntity.referenceId)
        assertEquals("Top up saldo kasir", retrievedEntity.notes)
        assertEquals(1700000000000L, retrievedEntity.createdAt)

        // 3. Map back to domain
        val retrievedDomain = retrievedEntity.toDomain()
        assertEquals(domainMutation, retrievedDomain)

        // 4. Non-existent returns null
        assertNull(fakeDao.findById("missing-mut"))
    }

    @Test
    fun `business, branch, and account isolation ensures digital deposit mutations remain strictly partitioned`() {
        val fakeDao = object : DigitalDepositMutationDao {
            private val storage = mutableMapOf<String, DigitalDepositMutationEntity>()

            override fun upsert(mutation: DigitalDepositMutationEntity) {
                storage[mutation.id] = mutation
            }

            override fun findById(id: String): DigitalDepositMutationEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): DigitalDepositMutationEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): DigitalDepositMutationEntity? {
                return storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
            }

            override fun findByBusinessId(businessId: String): List<DigitalDepositMutationEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<DigitalDepositMutationEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndAccountId(
                businessId: String,
                accountId: String
            ): List<DigitalDepositMutationEntity> {
                return storage.values.filter { it.businessId == businessId && it.accountId == accountId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchIdAndAccountId(
                businessId: String,
                branchId: String,
                accountId: String
            ): List<DigitalDepositMutationEntity> {
                return storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.accountId == accountId
                }.sortedByDescending { it.createdAt }
            }
        }

        val mutA1 = DigitalDepositMutation(
            id = "mut-A1",
            businessId = "biz-A",
            branchId = "branch-A1",
            accountId = "dep-A1",
            mutationType = DigitalDepositMutationType.TOP_UP,
            amount = 100000L,
            balanceBefore = 500000L,
            balanceAfter = 600000L,
            createdAt = 1000L
        )

        val mutA2 = DigitalDepositMutation(
            id = "mut-A2",
            businessId = "biz-A",
            branchId = "branch-A2",
            accountId = "dep-A2",
            mutationType = DigitalDepositMutationType.DIGITAL_SALE,
            amount = 49000L,
            balanceBefore = 750000L,
            balanceAfter = 701000L,
            createdAt = 2000L
        )

        val mutB1 = DigitalDepositMutation(
            id = "mut-B1",
            businessId = "biz-B",
            branchId = "branch-B1",
            accountId = "dep-B1",
            mutationType = DigitalDepositMutationType.TOP_UP,
            amount = 200000L,
            balanceBefore = 1000000L,
            balanceAfter = 1200000L,
            createdAt = 3000L
        )

        fakeDao.upsert(mutA1.toEntity())
        fakeDao.upsert(mutA2.toEntity())
        fakeDao.upsert(mutB1.toEntity())

        // Business A total
        val bizAMutations = fakeDao.findByBusinessId("biz-A")
        assertEquals(2, bizAMutations.size)

        // Branch A1 only
        val branchA1Mutations = fakeDao.findByBusinessIdAndBranchId("biz-A", "branch-A1")
        assertEquals(1, branchA1Mutations.size)
        assertEquals("mut-A1", branchA1Mutations[0].id)

        // Account A1 only
        val accountA1Mutations = fakeDao.findByBusinessIdAndAccountId("biz-A", "dep-A1")
        assertEquals(1, accountA1Mutations.size)
        assertEquals("mut-A1", accountA1Mutations[0].id)

        // Cross-business and cross-branch checks
        assertNull(fakeDao.findByIdAndBusinessId("mut-A1", "biz-B"))
        assertNull(fakeDao.findByIdAndBusinessIdAndBranchId("mut-A1", "biz-A", "branch-A2"))
    }

    @Test
    fun `digital deposit mutation entity does NOT contain CashRegister or CashMutation fields`() {
        val fields = DigitalDepositMutationEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("DigitalDepositMutationEntity must NOT contain registerId", !fields.contains("registerId"))
        assertTrue("DigitalDepositMutationEntity must NOT contain cashierSessionId", !fields.contains("cashierSessionId"))
        assertTrue("DigitalDepositMutationEntity must NOT contain cashBalance", !fields.contains("cashBalance"))
    }

    @Test
    fun `digital deposit mutation entity does NOT contain DigitalTransaction or provider fields`() {
        val fields = DigitalDepositMutationEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("DigitalDepositMutationEntity must NOT contain providerName", !fields.contains("providerName"))
        assertTrue("DigitalDepositMutationEntity must NOT contain providerBalance", !fields.contains("providerBalance"))
        assertTrue("DigitalDepositMutationEntity must NOT contain transactionId", !fields.contains("transactionId"))
        assertTrue("DigitalDepositMutationEntity must NOT contain productCode", !fields.contains("productCode"))
        assertTrue("DigitalDepositMutationEntity must NOT contain profit", !fields.contains("profit"))
        assertTrue("DigitalDepositMutationEntity must NOT contain revenue", !fields.contains("revenue"))
    }

    @Test
    fun `digital deposit mutation entity does NOT embed CustomerDebt, SupplierDebt, or DebtPayment fields`() {
        val fields = DigitalDepositMutationEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("DigitalDepositMutationEntity must NOT contain originalAmount", !fields.contains("originalAmount"))
        assertTrue("DigitalDepositMutationEntity must NOT contain remainingAmount", !fields.contains("remainingAmount"))
        assertTrue("DigitalDepositMutationEntity must NOT contain dueDate", !fields.contains("dueDate"))
        assertTrue("DigitalDepositMutationEntity must NOT contain debtType", !fields.contains("debtType"))
    }
}
