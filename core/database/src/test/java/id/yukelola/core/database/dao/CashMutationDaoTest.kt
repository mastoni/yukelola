package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.CashMutationEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.cash.CashMutation
import id.yukelola.core.domain.model.cash.CashMutationCategory
import id.yukelola.core.domain.model.cash.CashMutationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CashMutationDaoTest {

    @Test
    fun `CashMutationDao interface defines required persistence operations`() {
        val upsertMethod = CashMutationDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("CashMutationDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(CashMutationEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = CashMutationDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("CashMutationDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(CashMutationEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            CashMutationDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("CashMutationDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(CashMutationEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByIdAndBusinessIdAndBranchIdMethod =
            CashMutationDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessIdAndBranchId" }
        assertNotNull(
            "CashMutationDao must declare findByIdAndBusinessIdAndBranchId method",
            findByIdAndBusinessIdAndBranchIdMethod
        )
        assertEquals(3, findByIdAndBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[2])
        assertEquals(CashMutationEntity::class.java, findByIdAndBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdMethod =
            CashMutationDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("CashMutationDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            CashMutationDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull("CashMutationDao must declare findByBusinessIdAndBranchId method", findByBusinessIdAndBranchIdMethod)
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdAndRegisterIdMethod =
            CashMutationDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndRegisterId" }
        assertNotNull("CashMutationDao must declare findByBusinessIdAndRegisterId method", findByBusinessIdAndRegisterIdMethod)
        assertEquals(2, findByBusinessIdAndRegisterIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndRegisterIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndRegisterIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndRegisterIdMethod.returnType)

        val findByBusinessIdAndBranchIdAndRegisterIdMethod =
            CashMutationDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndRegisterId" }
        assertNotNull(
            "CashMutationDao must declare findByBusinessIdAndBranchIdAndRegisterId method",
            findByBusinessIdAndBranchIdAndRegisterIdMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndRegisterIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndRegisterIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndRegisterIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndRegisterIdMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndRegisterIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates CashMutationDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.CashMutationDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement CashMutationDao",
            CashMutationDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : CashMutationDao {
            private val storage = mutableMapOf<String, CashMutationEntity>()

            override fun upsert(cashMutation: CashMutationEntity) {
                storage[cashMutation.id] = cashMutation
            }

            override fun findById(id: String): CashMutationEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): CashMutationEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): CashMutationEntity? {
                return storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
            }

            override fun findByBusinessId(businessId: String): List<CashMutationEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<CashMutationEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndRegisterId(
                businessId: String,
                registerId: String
            ): List<CashMutationEntity> {
                return storage.values.filter { it.businessId == businessId && it.registerId == registerId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchIdAndRegisterId(
                businessId: String,
                branchId: String,
                registerId: String
            ): List<CashMutationEntity> {
                return storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.registerId == registerId
                }.sortedByDescending { it.createdAt }
            }
        }

        val domainMutation = CashMutation(
            id = "mut-test-01",
            businessId = "biz-101",
            branchId = "branch-101",
            registerId = "reg-101",
            cashierSessionId = "sess-101",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.SALE,
            amount = 250000L,
            source = "SALE-101",
            referenceId = "sale-101",
            notes = "Penjualan tunai",
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
        assertEquals("reg-101", retrievedEntity.registerId)
        assertEquals("sess-101", retrievedEntity.cashierSessionId)
        assertEquals("INFLOW", retrievedEntity.mutationType)
        assertEquals("SALE", retrievedEntity.category)
        assertEquals(250000L, retrievedEntity.amount)
        assertEquals("SALE-101", retrievedEntity.source)
        assertEquals("sale-101", retrievedEntity.referenceId)
        assertEquals("Penjualan tunai", retrievedEntity.notes)
        assertEquals(1700000000000L, retrievedEntity.createdAt)

        // 3. Map back to domain
        val retrievedDomain = retrievedEntity.toDomain()
        assertEquals(domainMutation, retrievedDomain)

        // 4. Non-existent returns null
        assertNull(fakeDao.findById("missing-mut"))
    }

    @Test
    fun `business, branch, and register isolation ensures cash mutations remain strictly partitioned`() {
        val fakeDao = object : CashMutationDao {
            private val storage = mutableMapOf<String, CashMutationEntity>()

            override fun upsert(cashMutation: CashMutationEntity) {
                storage[cashMutation.id] = cashMutation
            }

            override fun findById(id: String): CashMutationEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): CashMutationEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): CashMutationEntity? {
                return storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
            }

            override fun findByBusinessId(businessId: String): List<CashMutationEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<CashMutationEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndRegisterId(
                businessId: String,
                registerId: String
            ): List<CashMutationEntity> {
                return storage.values.filter { it.businessId == businessId && it.registerId == registerId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchIdAndRegisterId(
                businessId: String,
                branchId: String,
                registerId: String
            ): List<CashMutationEntity> {
                return storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.registerId == registerId
                }.sortedByDescending { it.createdAt }
            }
        }

        val mutA1 = CashMutation(
            id = "mut-A1",
            businessId = "biz-A",
            branchId = "branch-A1",
            registerId = "reg-A1",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.SALE,
            amount = 50000L,
            source = "SALE-A1",
            createdAt = 1000L
        )

        val mutA2 = CashMutation(
            id = "mut-A2",
            businessId = "biz-A",
            branchId = "branch-A2",
            registerId = "reg-A2",
            mutationType = CashMutationType.OUTFLOW,
            category = CashMutationCategory.EXPENSE,
            amount = 20000L,
            source = "EXPENSE-A2",
            createdAt = 2000L
        )

        val mutB1 = CashMutation(
            id = "mut-B1",
            businessId = "biz-B",
            branchId = "branch-B1",
            registerId = "reg-B1",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.SALE,
            amount = 80000L,
            source = "SALE-B1",
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

        // Register A1 only
        val regA1Mutations = fakeDao.findByBusinessIdAndRegisterId("biz-A", "reg-A1")
        assertEquals(1, regA1Mutations.size)
        assertEquals("mut-A1", regA1Mutations[0].id)

        // Cross-business and cross-branch checks
        assertNull(fakeDao.findByIdAndBusinessId("mut-A1", "biz-B"))
        assertNull(fakeDao.findByIdAndBusinessIdAndBranchId("mut-A1", "biz-A", "branch-A2"))
    }

    @Test
    fun `cash mutation entity does NOT duplicate CashRegister fields`() {
        val fields = CashMutationEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("CashMutationEntity must NOT contain name", !fields.contains("name"))
        assertTrue("CashMutationEntity must NOT contain currentBalance", !fields.contains("currentBalance"))
    }

    @Test
    fun `cash mutation entity does NOT contain DigitalDeposit or general ledger fields`() {
        val fields = CashMutationEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("CashMutationEntity must NOT contain depositBalance", !fields.contains("depositBalance"))
        assertTrue("CashMutationEntity must NOT contain providerName", !fields.contains("providerName"))
        assertTrue("CashMutationEntity must NOT contain profit", !fields.contains("profit"))
        assertTrue("CashMutationEntity must NOT contain revenue", !fields.contains("revenue"))
    }

    @Test
    fun `cash mutation entity does NOT embed CustomerDebt, SupplierDebt, or DebtPayment fields`() {
        val fields = CashMutationEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("CashMutationEntity must NOT contain originalAmount", !fields.contains("originalAmount"))
        assertTrue("CashMutationEntity must NOT contain remainingAmount", !fields.contains("remainingAmount"))
        assertTrue("CashMutationEntity must NOT contain dueDate", !fields.contains("dueDate"))
        assertTrue("CashMutationEntity must NOT contain debtType", !fields.contains("debtType"))
    }
}
