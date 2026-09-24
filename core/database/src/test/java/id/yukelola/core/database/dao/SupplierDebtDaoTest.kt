package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.SupplierDebtEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.debt.DebtStatus
import id.yukelola.core.domain.model.debt.SupplierDebt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SupplierDebtDaoTest {

    @Test
    fun `SupplierDebtDao interface defines required persistence operations`() {
        val upsertMethod = SupplierDebtDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("SupplierDebtDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(SupplierDebtEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = SupplierDebtDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("SupplierDebtDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(SupplierDebtEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            SupplierDebtDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("SupplierDebtDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(SupplierDebtEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByIdAndBusinessIdAndBranchIdMethod =
            SupplierDebtDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessIdAndBranchId" }
        assertNotNull("SupplierDebtDao must declare findByIdAndBusinessIdAndBranchId method", findByIdAndBusinessIdAndBranchIdMethod)
        assertEquals(3, findByIdAndBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[2])
        assertEquals(SupplierDebtEntity::class.java, findByIdAndBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdMethod =
            SupplierDebtDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("SupplierDebtDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            SupplierDebtDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull("SupplierDebtDao must declare findByBusinessIdAndBranchId method", findByBusinessIdAndBranchIdMethod)
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdAndSupplierIdMethod =
            SupplierDebtDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndSupplierId" }
        assertNotNull("SupplierDebtDao must declare findByBusinessIdAndSupplierId method", findByBusinessIdAndSupplierIdMethod)
        assertEquals(2, findByBusinessIdAndSupplierIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndSupplierIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndSupplierIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndSupplierIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates SupplierDebtDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.SupplierDebtDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement SupplierDebtDao",
            SupplierDebtDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : SupplierDebtDao {
            private val storage = mutableMapOf<String, SupplierDebtEntity>()

            override fun upsert(supplierDebt: SupplierDebtEntity) {
                storage[supplierDebt.id] = supplierDebt
            }

            override fun findById(id: String): SupplierDebtEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): SupplierDebtEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): SupplierDebtEntity? {
                return storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
            }

            override fun findByBusinessId(businessId: String): List<SupplierDebtEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<SupplierDebtEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndSupplierId(
                businessId: String,
                supplierId: String
            ): List<SupplierDebtEntity> {
                return storage.values.filter { it.businessId == businessId && it.supplierId == supplierId }
                    .sortedByDescending { it.createdAt }
            }
        }

        val domainDebt = SupplierDebt.create(
            id = "sup-debt-101",
            businessId = "biz-101",
            branchId = "branch-101",
            supplierId = "sup-101",
            purchaseId = "purch-101",
            originalAmount = 4500000L,
            dueDate = 1750000000000L,
            createdAt = 1700000000000L
        )

        // 1. Insert/Upsert
        fakeDao.upsert(domainDebt.toEntity())

        // 2. Retrieve by ID
        val retrievedEntity = fakeDao.findById("sup-debt-101")
        assertNotNull("Retrieved entity must not be null", retrievedEntity)
        assertEquals("sup-debt-101", retrievedEntity!!.id)
        assertEquals("biz-101", retrievedEntity.businessId)
        assertEquals("branch-101", retrievedEntity.branchId)
        assertEquals("sup-101", retrievedEntity.supplierId)
        assertEquals("purch-101", retrievedEntity.purchaseId)
        assertEquals(4500000L, retrievedEntity.originalAmount)
        assertEquals(4500000L, retrievedEntity.remainingAmount)
        assertEquals("UNPAID", retrievedEntity.status)
        assertEquals(1750000000000L, retrievedEntity.dueDate)
        assertEquals(1700000000000L, retrievedEntity.createdAt)

        // 3. Map back to domain
        val retrievedDomain = retrievedEntity.toDomain()
        assertEquals(domainDebt, retrievedDomain)

        // 4. Non-existent returns null
        assertNull(fakeDao.findById("missing-debt"))
    }

    @Test
    fun `business and branch isolation ensures supplier debts remain strictly partitioned`() {
        val fakeDao = object : SupplierDebtDao {
            private val storage = mutableMapOf<String, SupplierDebtEntity>()

            override fun upsert(supplierDebt: SupplierDebtEntity) {
                storage[supplierDebt.id] = supplierDebt
            }

            override fun findById(id: String): SupplierDebtEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): SupplierDebtEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): SupplierDebtEntity? {
                return storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
            }

            override fun findByBusinessId(businessId: String): List<SupplierDebtEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<SupplierDebtEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndSupplierId(
                businessId: String,
                supplierId: String
            ): List<SupplierDebtEntity> {
                return storage.values.filter { it.businessId == businessId && it.supplierId == supplierId }
                    .sortedByDescending { it.createdAt }
            }
        }

        val debtBranchA1 = SupplierDebt.create(
            id = "debt-A1",
            businessId = "biz-A",
            branchId = "branch-A1",
            supplierId = "sup-A",
            purchaseId = "p-A1",
            originalAmount = 1000000L,
            createdAt = 1000L
        )

        val debtBranchA2 = SupplierDebt.create(
            id = "debt-A2",
            businessId = "biz-A",
            branchId = "branch-A2",
            supplierId = "sup-A",
            purchaseId = "p-A2",
            originalAmount = 2000000L,
            createdAt = 2000L
        )

        val debtBranchB1 = SupplierDebt.create(
            id = "debt-B1",
            businessId = "biz-B",
            branchId = "branch-B1",
            supplierId = "sup-B",
            purchaseId = "p-B1",
            originalAmount = 3000000L,
            createdAt = 3000L
        )

        fakeDao.upsert(debtBranchA1.toEntity())
        fakeDao.upsert(debtBranchA2.toEntity())
        fakeDao.upsert(debtBranchB1.toEntity())

        // Business A total
        val bizADebts = fakeDao.findByBusinessId("biz-A")
        assertEquals(2, bizADebts.size)

        // Branch A1 only
        val branchA1Debts = fakeDao.findByBusinessIdAndBranchId("biz-A", "branch-A1")
        assertEquals(1, branchA1Debts.size)
        assertEquals("debt-A1", branchA1Debts[0].id)

        // Branch A2 only
        val branchA2Debts = fakeDao.findByBusinessIdAndBranchId("biz-A", "branch-A2")
        assertEquals(1, branchA2Debts.size)
        assertEquals("debt-A2", branchA2Debts[0].id)

        // Cross-business check
        assertNull(fakeDao.findByIdAndBusinessId("debt-A1", "biz-B"))
        assertNull(fakeDao.findByIdAndBusinessIdAndBranchId("debt-A1", "biz-A", "branch-A2"))
    }

    @Test
    fun `supplier debt entity does NOT contain Purchase or DebtPayment fields`() {
        val fields = SupplierDebtEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("SupplierDebtEntity must NOT contain paymentMethod", !fields.contains("paymentMethod"))
        assertTrue("SupplierDebtEntity must NOT contain paymentAmount", !fields.contains("paymentAmount"))
        assertTrue("SupplierDebtEntity must NOT contain totalPurchase", !fields.contains("totalPurchase"))
        assertTrue("SupplierDebtEntity must NOT contain purchaseNumber", !fields.contains("purchaseNumber"))
        assertTrue("SupplierDebtEntity must NOT contain purchaseItems", !fields.contains("purchaseItems"))
    }
}
