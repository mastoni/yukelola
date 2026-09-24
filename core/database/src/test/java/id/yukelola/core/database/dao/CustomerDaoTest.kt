package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.CustomerEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.actor.Customer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomerDaoTest {

    @Test
    fun `CustomerDao interface defines required persistence operations`() {
        val upsertMethod = CustomerDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("CustomerDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(CustomerEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = CustomerDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("CustomerDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(CustomerEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            CustomerDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("CustomerDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(CustomerEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByBusinessIdMethod =
            CustomerDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("CustomerDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            CustomerDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull("CustomerDao must declare findByBusinessIdAndBranchId method", findByBusinessIdAndBranchIdMethod)
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates CustomerDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.CustomerDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement CustomerDao",
            CustomerDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : CustomerDao {
            private val storage = mutableMapOf<String, CustomerEntity>()

            override fun upsert(customer: CustomerEntity) {
                storage[customer.id] = customer
            }

            override fun findById(id: String): CustomerEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): CustomerEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByBusinessId(businessId: String): List<CustomerEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedBy { it.name }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<CustomerEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedBy { it.name }
            }
        }

        val domainCustomer = Customer(
            id = "cust-101",
            businessId = "biz-101",
            branchId = "branch-101",
            name = "Rahmat Hidayat",
            phone = "081122334455",
            debtBalance = 75000L,
            isActive = true
        )

        // 1. Insert/Upsert
        fakeDao.upsert(domainCustomer.toEntity())

        // 2. Retrieve by ID
        val retrievedEntity = fakeDao.findById("cust-101")
        assertNotNull("Retrieved entity must not be null", retrievedEntity)
        assertEquals("cust-101", retrievedEntity!!.id)
        assertEquals("biz-101", retrievedEntity.businessId)
        assertEquals("branch-101", retrievedEntity.branchId)
        assertEquals("Rahmat Hidayat", retrievedEntity.name)
        assertEquals("081122334455", retrievedEntity.phone)
        assertEquals(75000L, retrievedEntity.debtBalance)
        assertTrue(retrievedEntity.isActive)

        // 3. Map back to domain
        val retrievedDomain = retrievedEntity.toDomain()
        assertEquals(domainCustomer, retrievedDomain)

        // 4. Non-existent returns null
        assertNull(fakeDao.findById("missing-cust"))
    }

    @Test
    fun `business isolation ensures customers belonging to different businesses remain distinct`() {
        val fakeDao = object : CustomerDao {
            private val storage = mutableMapOf<String, CustomerEntity>()

            override fun upsert(customer: CustomerEntity) {
                storage[customer.id] = customer
            }

            override fun findById(id: String): CustomerEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): CustomerEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByBusinessId(businessId: String): List<CustomerEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedBy { it.name }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<CustomerEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedBy { it.name }
            }
        }

        val customerA = Customer(
            id = "cust-A",
            businessId = "biz-A",
            name = "Customer Alpha"
        )

        val customerB = Customer(
            id = "cust-B",
            businessId = "biz-B",
            name = "Customer Beta"
        )

        fakeDao.upsert(customerA.toEntity())
        fakeDao.upsert(customerB.toEntity())

        val retrievedA = fakeDao.findById("cust-A")
        val retrievedB = fakeDao.findById("cust-B")

        assertNotNull(retrievedA)
        assertNotNull(retrievedB)
        assertEquals("biz-A", retrievedA!!.businessId)
        assertEquals("biz-B", retrievedB!!.businessId)

        val businessACustomers = fakeDao.findByBusinessId("biz-A")
        assertEquals(1, businessACustomers.size)
        assertEquals("cust-A", businessACustomers[0].id)

        val businessBCustomers = fakeDao.findByBusinessId("biz-B")
        assertEquals(1, businessBCustomers.size)
        assertEquals("cust-B", businessBCustomers[0].id)

        assertNull(fakeDao.findByIdAndBusinessId("cust-A", "biz-B"))
        assertNull(fakeDao.findByIdAndBusinessId("cust-B", "biz-A"))
    }

    @Test
    fun `optional home branch association supports filtering by branch and null branch`() {
        val fakeDao = object : CustomerDao {
            private val storage = mutableMapOf<String, CustomerEntity>()

            override fun upsert(customer: CustomerEntity) {
                storage[customer.id] = customer
            }

            override fun findById(id: String): CustomerEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): CustomerEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByBusinessId(businessId: String): List<CustomerEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedBy { it.name }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<CustomerEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedBy { it.name }
            }
        }

        val custBranch1 = Customer(
            id = "c1",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Branch 1 Customer"
        )
        val custBranch2 = Customer(
            id = "c2",
            businessId = "biz-01",
            branchId = "branch-02",
            name = "Branch 2 Customer"
        )
        val custNoBranch = Customer(
            id = "c3",
            businessId = "biz-01",
            branchId = null,
            name = "Global Customer"
        )

        fakeDao.upsert(custBranch1.toEntity())
        fakeDao.upsert(custBranch2.toEntity())
        fakeDao.upsert(custNoBranch.toEntity())

        // Business listing returns all 3
        val allCustomers = fakeDao.findByBusinessId("biz-01")
        assertEquals(3, allCustomers.size)

        // Branch 1 returns only c1
        val branch1Customers = fakeDao.findByBusinessIdAndBranchId("biz-01", "branch-01")
        assertEquals(1, branch1Customers.size)
        assertEquals("c1", branch1Customers[0].id)

        // Branch 2 returns only c2
        val branch2Customers = fakeDao.findByBusinessIdAndBranchId("biz-01", "branch-02")
        assertEquals(1, branch2Customers.size)
        assertEquals("c2", branch2Customers[0].id)
    }

    @Test
    fun `customer entity does NOT contain CustomerDebt fields`() {
        val fields = CustomerEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("CustomerEntity must NOT contain debtId", !fields.contains("debtId"))
        assertTrue("CustomerEntity must NOT contain originalAmount", !fields.contains("originalAmount"))
        assertTrue("CustomerEntity must NOT contain remainingAmount", !fields.contains("remainingAmount"))
        assertTrue("CustomerEntity must NOT contain dueDate", !fields.contains("dueDate"))
        assertTrue("CustomerEntity must NOT contain status", !fields.contains("status"))
        assertTrue("CustomerEntity must NOT contain referenceType", !fields.contains("referenceType"))
    }
}
