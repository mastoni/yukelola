package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.BranchEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.business.Branch
import id.yukelola.core.domain.model.business.BusinessModel
import id.yukelola.core.domain.model.business.BusinessProfile
import id.yukelola.core.domain.model.business.Capability
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BranchDaoTest {

    @Test
    fun `BranchDao interface defines required persistence operations`() {
        val upsertMethod = BranchDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("BranchDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(BranchEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = BranchDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("BranchDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(BranchEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            BranchDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("BranchDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(BranchEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByBusinessIdMethod = BranchDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("BranchDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates BranchDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.BranchDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement BranchDao",
            BranchDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : BranchDao {
            private val storage = mutableMapOf<String, BranchEntity>()

            override fun upsert(branch: BranchEntity) {
                storage[branch.id] = branch
            }

            override fun findById(id: String): BranchEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): BranchEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByBusinessId(businessId: String): List<BranchEntity> {
                return storage.values.filter { it.businessId == businessId }
            }
        }

        val domainBranch = Branch(
            id = "branch-101",
            businessId = "biz-101",
            code = "PST",
            name = "Pusat Toko",
            businessProfile = BusinessProfile(
                name = "Toko Pusat",
                phone = "0811111111",
                address = "Jakarta",
                currency = "IDR",
                timezone = "Asia/Jakarta"
            ),
            businessModel = BusinessModel.RETAIL_WARUNG,
            enabledCapabilities = setOf(Capability.RETAIL, Capability.INVENTORY),
            isActive = true
        )

        // 1. Insert/Upsert
        fakeDao.upsert(domainBranch.toEntity())

        // 2. Retrieve by ID
        val retrievedEntity = fakeDao.findById("branch-101")
        assertNotNull("Retrieved entity must not be null", retrievedEntity)
        assertEquals("branch-101", retrievedEntity!!.id)
        assertEquals("biz-101", retrievedEntity.businessId)
        assertEquals("PST", retrievedEntity.code)
        assertEquals("Pusat Toko", retrievedEntity.name)

        // 3. Map back to domain
        val retrievedDomain = retrievedEntity.toDomain()
        assertEquals(domainBranch, retrievedDomain)

        // 4. Non-existent returns null
        assertNull(fakeDao.findById("missing-branch"))
    }

    @Test
    fun `business isolation test ensures branches belonging to different businesses remain distinct`() {
        val fakeDao = object : BranchDao {
            private val storage = mutableMapOf<String, BranchEntity>()

            override fun upsert(branch: BranchEntity) {
                storage[branch.id] = branch
            }

            override fun findById(id: String): BranchEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): BranchEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByBusinessId(businessId: String): List<BranchEntity> {
                return storage.values.filter { it.businessId == businessId }
            }
        }

        // Business A has Branch A
        val branchA = Branch(
            id = "branch-A",
            businessId = "biz-A",
            code = "MAIN",
            name = "Outlet Business A",
            businessProfile = BusinessProfile(name = "Business A Profile"),
            businessModel = BusinessModel.RETAIL_WARUNG,
            enabledCapabilities = setOf(Capability.RETAIL),
            isActive = true
        )

        // Business B has Branch B
        val branchB = Branch(
            id = "branch-B",
            businessId = "biz-B",
            code = "MAIN",
            name = "Outlet Business B",
            businessProfile = BusinessProfile(name = "Business B Profile"),
            businessModel = BusinessModel.LAUNDRY,
            enabledCapabilities = setOf(Capability.SERVICE),
            isActive = true
        )

        fakeDao.upsert(branchA.toEntity())
        fakeDao.upsert(branchB.toEntity())

        // Verify independent retrieval and distinct businessId
        val retrievedA = fakeDao.findById("branch-A")
        val retrievedB = fakeDao.findById("branch-B")

        assertNotNull(retrievedA)
        assertNotNull(retrievedB)
        assertEquals("biz-A", retrievedA!!.businessId)
        assertEquals("biz-B", retrievedB!!.businessId)

        // Scoped lookup by businessId
        val businessABranches = fakeDao.findByBusinessId("biz-A")
        assertEquals(1, businessABranches.size)
        assertEquals("branch-A", businessABranches[0].id)

        val businessBBranches = fakeDao.findByBusinessId("biz-B")
        assertEquals(1, businessBBranches.size)
        assertEquals("branch-B", businessBBranches[0].id)

        // Cross-business lookup returns null
        assertNull(fakeDao.findByIdAndBusinessId("branch-A", "biz-B"))
        assertNull(fakeDao.findByIdAndBusinessId("branch-B", "biz-A"))
    }
}
