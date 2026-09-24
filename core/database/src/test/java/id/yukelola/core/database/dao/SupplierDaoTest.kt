package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.SupplierEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.actor.Supplier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SupplierDaoTest {

    @Test
    fun `SupplierDao interface defines required persistence operations`() {
        val upsertMethod = SupplierDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("SupplierDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(SupplierEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = SupplierDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("SupplierDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(SupplierEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            SupplierDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("SupplierDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(SupplierEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByBusinessIdMethod =
            SupplierDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("SupplierDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates SupplierDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.SupplierDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement SupplierDao",
            SupplierDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : SupplierDao {
            private val storage = mutableMapOf<String, SupplierEntity>()

            override fun upsert(supplier: SupplierEntity) {
                storage[supplier.id] = supplier
            }

            override fun findById(id: String): SupplierEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): SupplierEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByBusinessId(businessId: String): List<SupplierEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedBy { it.name }
            }
        }

        val domainSupplier = Supplier(
            id = "sup-101",
            businessId = "biz-101",
            name = "Grosir Sembako Jaya",
            phone = "081122334455",
            debtBalance = 3500000L,
            isActive = true
        )

        // 1. Insert/Upsert
        fakeDao.upsert(domainSupplier.toEntity())

        // 2. Retrieve by ID
        val retrievedEntity = fakeDao.findById("sup-101")
        assertNotNull("Retrieved entity must not be null", retrievedEntity)
        assertEquals("sup-101", retrievedEntity!!.id)
        assertEquals("biz-101", retrievedEntity.businessId)
        assertEquals("Grosir Sembako Jaya", retrievedEntity.name)
        assertEquals("081122334455", retrievedEntity.phone)
        assertEquals(3500000L, retrievedEntity.debtBalance)
        assertTrue(retrievedEntity.isActive)

        // 3. Map back to domain
        val retrievedDomain = retrievedEntity.toDomain()
        assertEquals(domainSupplier, retrievedDomain)

        // 4. Non-existent returns null
        assertNull(fakeDao.findById("missing-sup"))
    }

    @Test
    fun `business isolation ensures suppliers belonging to different businesses remain distinct`() {
        val fakeDao = object : SupplierDao {
            private val storage = mutableMapOf<String, SupplierEntity>()

            override fun upsert(supplier: SupplierEntity) {
                storage[supplier.id] = supplier
            }

            override fun findById(id: String): SupplierEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): SupplierEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByBusinessId(businessId: String): List<SupplierEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedBy { it.name }
            }
        }

        val supplierA = Supplier(
            id = "sup-A",
            businessId = "biz-A",
            name = "Supplier Alpha"
        )

        val supplierB = Supplier(
            id = "sup-B",
            businessId = "biz-B",
            name = "Supplier Beta"
        )

        fakeDao.upsert(supplierA.toEntity())
        fakeDao.upsert(supplierB.toEntity())

        val retrievedA = fakeDao.findById("sup-A")
        val retrievedB = fakeDao.findById("sup-B")

        assertNotNull(retrievedA)
        assertNotNull(retrievedB)
        assertEquals("biz-A", retrievedA!!.businessId)
        assertEquals("biz-B", retrievedB!!.businessId)

        val businessASuppliers = fakeDao.findByBusinessId("biz-A")
        assertEquals(1, businessASuppliers.size)
        assertEquals("sup-A", businessASuppliers[0].id)

        val businessBSuppliers = fakeDao.findByBusinessId("biz-B")
        assertEquals(1, businessBSuppliers.size)
        assertEquals("sup-B", businessBSuppliers[0].id)

        assertNull(fakeDao.findByIdAndBusinessId("sup-A", "biz-B"))
        assertNull(fakeDao.findByIdAndBusinessId("sup-B", "biz-A"))
    }

    @Test
    fun `supplier entity does NOT contain SupplierDebt or Purchase fields`() {
        val fields = SupplierEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("SupplierEntity must NOT contain debtId", !fields.contains("debtId"))
        assertTrue("SupplierEntity must NOT contain purchaseId", !fields.contains("purchaseId"))
        assertTrue("SupplierEntity must NOT contain originalAmount", !fields.contains("originalAmount"))
        assertTrue("SupplierEntity must NOT contain remainingAmount", !fields.contains("remainingAmount"))
        assertTrue("SupplierEntity must NOT contain dueDate", !fields.contains("dueDate"))
        assertTrue("SupplierEntity must NOT contain status", !fields.contains("status"))
        assertTrue("SupplierEntity must NOT contain totalPurchase", !fields.contains("totalPurchase"))
        assertTrue("SupplierEntity must NOT contain purchaseCount", !fields.contains("purchaseCount"))
    }
}
