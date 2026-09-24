package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.CashRegisterEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.cash.CashRegister
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CashRegisterDaoTest {

    @Test
    fun `CashRegisterDao interface defines required persistence operations`() {
        val upsertMethod = CashRegisterDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("CashRegisterDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(CashRegisterEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = CashRegisterDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("CashRegisterDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(CashRegisterEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            CashRegisterDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("CashRegisterDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(CashRegisterEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByIdAndBusinessIdAndBranchIdMethod =
            CashRegisterDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessIdAndBranchId" }
        assertNotNull(
            "CashRegisterDao must declare findByIdAndBusinessIdAndBranchId method",
            findByIdAndBusinessIdAndBranchIdMethod
        )
        assertEquals(3, findByIdAndBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[2])
        assertEquals(CashRegisterEntity::class.java, findByIdAndBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdMethod =
            CashRegisterDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("CashRegisterDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            CashRegisterDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull("CashRegisterDao must declare findByBusinessIdAndBranchId method", findByBusinessIdAndBranchIdMethod)
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates CashRegisterDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.CashRegisterDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement CashRegisterDao",
            CashRegisterDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : CashRegisterDao {
            private val storage = mutableMapOf<String, CashRegisterEntity>()

            override fun upsert(cashRegister: CashRegisterEntity) {
                storage[cashRegister.id] = cashRegister
            }

            override fun findById(id: String): CashRegisterEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): CashRegisterEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): CashRegisterEntity? {
                return storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
            }

            override fun findByBusinessId(businessId: String): List<CashRegisterEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedBy { it.name }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<CashRegisterEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedBy { it.name }
            }
        }

        val domainRegister = CashRegister(
            id = "reg-test-01",
            businessId = "biz-101",
            branchId = "branch-101",
            name = "Laci Utama",
            currentBalance = 350000L,
            updatedAt = 1700000000000L
        )

        // 1. Insert/Upsert
        fakeDao.upsert(domainRegister.toEntity())

        // 2. Retrieve by ID
        val retrievedEntity = fakeDao.findById("reg-test-01")
        assertNotNull("Retrieved entity must not be null", retrievedEntity)
        assertEquals("reg-test-01", retrievedEntity!!.id)
        assertEquals("biz-101", retrievedEntity.businessId)
        assertEquals("branch-101", retrievedEntity.branchId)
        assertEquals("Laci Utama", retrievedEntity.name)
        assertEquals(350000L, retrievedEntity.currentBalance)
        assertEquals(1700000000000L, retrievedEntity.updatedAt)

        // 3. Map back to domain
        val retrievedDomain = retrievedEntity.toDomain()
        assertEquals(domainRegister, retrievedDomain)

        // 4. Non-existent returns null
        assertNull(fakeDao.findById("missing-reg"))
    }

    @Test
    fun `business and branch isolation ensures cash registers remain strictly partitioned`() {
        val fakeDao = object : CashRegisterDao {
            private val storage = mutableMapOf<String, CashRegisterEntity>()

            override fun upsert(cashRegister: CashRegisterEntity) {
                storage[cashRegister.id] = cashRegister
            }

            override fun findById(id: String): CashRegisterEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): CashRegisterEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): CashRegisterEntity? {
                return storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
            }

            override fun findByBusinessId(businessId: String): List<CashRegisterEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedBy { it.name }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<CashRegisterEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedBy { it.name }
            }
        }

        val regA1 = CashRegister(
            id = "reg-A1",
            businessId = "biz-A",
            branchId = "branch-A1",
            name = "Kasir A1",
            currentBalance = 100000L,
            updatedAt = 1000L
        )

        val regA2 = CashRegister(
            id = "reg-A2",
            businessId = "biz-A",
            branchId = "branch-A2",
            name = "Kasir A2",
            currentBalance = 200000L,
            updatedAt = 2000L
        )

        val regB1 = CashRegister(
            id = "reg-B1",
            businessId = "biz-B",
            branchId = "branch-B1",
            name = "Kasir B1",
            currentBalance = 300000L,
            updatedAt = 3000L
        )

        fakeDao.upsert(regA1.toEntity())
        fakeDao.upsert(regA2.toEntity())
        fakeDao.upsert(regB1.toEntity())

        // Business A total
        val bizARegisters = fakeDao.findByBusinessId("biz-A")
        assertEquals(2, bizARegisters.size)

        // Branch A1 only
        val branchA1Registers = fakeDao.findByBusinessIdAndBranchId("biz-A", "branch-A1")
        assertEquals(1, branchA1Registers.size)
        assertEquals("reg-A1", branchA1Registers[0].id)

        // Branch A2 only
        val branchA2Registers = fakeDao.findByBusinessIdAndBranchId("biz-A", "branch-A2")
        assertEquals(1, branchA2Registers.size)
        assertEquals("reg-A2", branchA2Registers[0].id)

        // Cross-business and cross-branch checks
        assertNull(fakeDao.findByIdAndBusinessId("reg-A1", "biz-B"))
        assertNull(fakeDao.findByIdAndBusinessIdAndBranchId("reg-A1", "biz-A", "branch-A2"))
    }

    @Test
    fun `cash register entity does NOT contain CashMutation event fields`() {
        val fields = CashRegisterEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("CashRegisterEntity must NOT contain mutationType", !fields.contains("mutationType"))
        assertTrue("CashRegisterEntity must NOT contain category", !fields.contains("category"))
        assertTrue("CashRegisterEntity must NOT contain source", !fields.contains("source"))
        assertTrue("CashRegisterEntity must NOT contain referenceId", !fields.contains("referenceId"))
        assertTrue("CashRegisterEntity must NOT contain notes", !fields.contains("notes"))
        assertTrue("CashRegisterEntity must NOT contain mutations", !fields.contains("mutations"))
    }

    @Test
    fun `cash register entity does NOT contain DigitalDeposit or general ledger fields`() {
        val fields = CashRegisterEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("CashRegisterEntity must NOT contain depositBalance", !fields.contains("depositBalance"))
        assertTrue("CashRegisterEntity must NOT contain providerName", !fields.contains("providerName"))
        assertTrue("CashRegisterEntity must NOT contain profit", !fields.contains("profit"))
        assertTrue("CashRegisterEntity must NOT contain revenue", !fields.contains("revenue"))
        assertTrue("CashRegisterEntity must NOT contain expense", !fields.contains("expense"))
    }
}
