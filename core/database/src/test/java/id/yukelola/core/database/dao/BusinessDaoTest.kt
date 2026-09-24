package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.BusinessEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.business.Business
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BusinessDaoTest {

    @Test
    fun `BusinessDao interface defines required persistence operations`() {
        val upsertMethod = BusinessDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("BusinessDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(BusinessEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = BusinessDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("BusinessDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(BusinessEntity::class.java, findByIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates BusinessDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.BusinessDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement BusinessDao",
            BusinessDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        // In-memory backing store validating the DAO persistence slice contract
        val fakeDao = object : BusinessDao {
            private val storage = mutableMapOf<String, BusinessEntity>()

            override fun upsert(business: BusinessEntity) {
                storage[business.id] = business
            }

            override fun findById(id: String): BusinessEntity? {
                return storage[id]
            }
        }

        val domainBusiness = Business(
            id = "biz-201",
            legalName = "Yukelola Retail Store",
            ownerUserId = "user-999",
            createdAt = 1705000000000L,
            isActive = true
        )

        // 1. Domain -> Entity mapping & Insert/Upsert
        val entity = domainBusiness.toEntity()
        fakeDao.upsert(entity)

        // 2. Retrieve by ID
        val retrievedEntity = fakeDao.findById("biz-201")
        assertNotNull("Retrieved entity must not be null", retrievedEntity)
        assertEquals("biz-201", retrievedEntity!!.id)
        assertEquals("Yukelola Retail Store", retrievedEntity.legalName)
        assertEquals("user-999", retrievedEntity.ownerUserId)
        assertEquals(1705000000000L, retrievedEntity.createdAt)
        assertTrue(retrievedEntity.isActive)

        // 3. Entity -> Domain mapping
        val retrievedDomain = retrievedEntity.toDomain()
        assertEquals(domainBusiness, retrievedDomain)

        // 4. Non-existent ID returns null
        assertNull(fakeDao.findById("non-existent-id"))
    }

    @Test
    fun `persistence slice supports upsert update behavior`() {
        val fakeDao = object : BusinessDao {
            private val storage = mutableMapOf<String, BusinessEntity>()

            override fun upsert(business: BusinessEntity) {
                storage[business.id] = business
            }

            override fun findById(id: String): BusinessEntity? {
                return storage[id]
            }
        }

        val initialDomain = Business(
            id = "biz-202",
            legalName = "Initial Store Name",
            ownerUserId = "user-100",
            createdAt = 1706000000000L,
            isActive = true
        )
        fakeDao.upsert(initialDomain.toEntity())

        val updatedDomain = Business(
            id = "biz-202",
            legalName = "Updated Store Name",
            ownerUserId = "user-100",
            createdAt = 1706000000000L,
            isActive = false
        )
        fakeDao.upsert(updatedDomain.toEntity())

        val retrievedDomain = fakeDao.findById("biz-202")?.toDomain()
        assertEquals(updatedDomain, retrievedDomain)
        assertEquals("Updated Store Name", retrievedDomain?.legalName)
        assertEquals(false, retrievedDomain?.isActive)
    }
}
