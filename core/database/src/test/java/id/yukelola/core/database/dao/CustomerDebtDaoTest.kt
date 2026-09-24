package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.CustomerDebtEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.debt.DebtReferenceType
import id.yukelola.core.domain.model.debt.DebtStatus
import id.yukelola.core.domain.model.debt.CustomerDebt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomerDebtDaoTest {

    @Test
    fun `CustomerDebtDao interface defines required persistence operations`() {
        val upsertMethod = CustomerDebtDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("CustomerDebtDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(CustomerDebtEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = CustomerDebtDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("CustomerDebtDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(CustomerDebtEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            CustomerDebtDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("CustomerDebtDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(CustomerDebtEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByIdAndBusinessIdAndBranchIdMethod =
            CustomerDebtDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessIdAndBranchId" }
        assertNotNull("CustomerDebtDao must declare findByIdAndBusinessIdAndBranchId method", findByIdAndBusinessIdAndBranchIdMethod)
        assertEquals(3, findByIdAndBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[2])
        assertEquals(CustomerDebtEntity::class.java, findByIdAndBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdMethod =
            CustomerDebtDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("CustomerDebtDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            CustomerDebtDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull("CustomerDebtDao must declare findByBusinessIdAndBranchId method", findByBusinessIdAndBranchIdMethod)
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdAndCustomerIdMethod =
            CustomerDebtDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndCustomerId" }
        assertNotNull("CustomerDebtDao must declare findByBusinessIdAndCustomerId method", findByBusinessIdAndCustomerIdMethod)
        assertEquals(2, findByBusinessIdAndCustomerIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndCustomerIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndCustomerIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndCustomerIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates CustomerDebtDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.CustomerDebtDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement CustomerDebtDao",
            CustomerDebtDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : CustomerDebtDao {
            private val storage = mutableMapOf<String, CustomerDebtEntity>()

            override fun upsert(customerDebt: CustomerDebtEntity) {
                storage[customerDebt.id] = customerDebt
            }

            override fun findById(id: String): CustomerDebtEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): CustomerDebtEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): CustomerDebtEntity? {
                return storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
            }

            override fun findByBusinessId(businessId: String): List<CustomerDebtEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<CustomerDebtEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndCustomerId(
                businessId: String,
                customerId: String
            ): List<CustomerDebtEntity> {
                return storage.values.filter { it.businessId == businessId && it.customerId == customerId }
                    .sortedByDescending { it.createdAt }
            }
        }

        val domainDebt = CustomerDebt.create(
            id = "cust-debt-101",
            businessId = "biz-101",
            branchId = "branch-101",
            customerId = "cust-101",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-101",
            originalAmount = 180000L,
            dueDate = 1750000000000L,
            createdAt = 1700000000000L
        )

        // 1. Insert/Upsert
        fakeDao.upsert(domainDebt.toEntity())

        // 2. Retrieve by ID
        val retrievedEntity = fakeDao.findById("cust-debt-101")
        assertNotNull("Retrieved entity must not be null", retrievedEntity)
        assertEquals("cust-debt-101", retrievedEntity!!.id)
        assertEquals("biz-101", retrievedEntity.businessId)
        assertEquals("branch-101", retrievedEntity.branchId)
        assertEquals("cust-101", retrievedEntity.customerId)
        assertEquals("SALE", retrievedEntity.referenceType)
        assertEquals("sale-101", retrievedEntity.referenceId)
        assertEquals(180000L, retrievedEntity.originalAmount)
        assertEquals(180000L, retrievedEntity.remainingAmount)
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
    fun `business and branch isolation ensures customer debts remain strictly partitioned`() {
        val fakeDao = object : CustomerDebtDao {
            private val storage = mutableMapOf<String, CustomerDebtEntity>()

            override fun upsert(customerDebt: CustomerDebtEntity) {
                storage[customerDebt.id] = customerDebt
            }

            override fun findById(id: String): CustomerDebtEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): CustomerDebtEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): CustomerDebtEntity? {
                return storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
            }

            override fun findByBusinessId(businessId: String): List<CustomerDebtEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<CustomerDebtEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndCustomerId(
                businessId: String,
                customerId: String
            ): List<CustomerDebtEntity> {
                return storage.values.filter { it.businessId == businessId && it.customerId == customerId }
                    .sortedByDescending { it.createdAt }
            }
        }

        val debtBranchA1 = CustomerDebt.create(
            id = "cdebt-A1",
            businessId = "biz-A",
            branchId = "branch-A1",
            customerId = "cust-A",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-A1",
            originalAmount = 50000L,
            createdAt = 1000L
        )

        val debtBranchA2 = CustomerDebt.create(
            id = "cdebt-A2",
            businessId = "biz-A",
            branchId = "branch-A2",
            customerId = "cust-A",
            referenceType = DebtReferenceType.SERVICE_ORDER,
            referenceId = "srv-A2",
            originalAmount = 120000L,
            createdAt = 2000L
        )

        val debtBranchB1 = CustomerDebt.create(
            id = "cdebt-B1",
            businessId = "biz-B",
            branchId = "branch-B1",
            customerId = "cust-B",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-B1",
            originalAmount = 80000L,
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
        assertEquals("cdebt-A1", branchA1Debts[0].id)

        // Branch A2 only
        val branchA2Debts = fakeDao.findByBusinessIdAndBranchId("biz-A", "branch-A2")
        assertEquals(1, branchA2Debts.size)
        assertEquals("cdebt-A2", branchA2Debts[0].id)

        // Cross-business check
        assertNull(fakeDao.findByIdAndBusinessId("cdebt-A1", "biz-B"))
        assertNull(fakeDao.findByIdAndBusinessIdAndBranchId("cdebt-A1", "biz-A", "branch-A2"))
    }

    @Test
    fun `customer debt entity does NOT contain Sale or DebtPayment fields`() {
        val fields = CustomerDebtEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("CustomerDebtEntity must NOT contain paymentMethod", !fields.contains("paymentMethod"))
        assertTrue("CustomerDebtEntity must NOT contain paymentAmount", !fields.contains("paymentAmount"))
        assertTrue("CustomerDebtEntity must NOT contain saleNumber", !fields.contains("saleNumber"))
        assertTrue("CustomerDebtEntity must NOT contain saleItems", !fields.contains("saleItems"))
        assertTrue("CustomerDebtEntity must NOT contain totalAmount", !fields.contains("totalAmount"))
    }
}
