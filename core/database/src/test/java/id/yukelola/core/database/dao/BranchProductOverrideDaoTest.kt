package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.BranchProductOverrideEntity
import id.yukelola.core.database.entity.ProductEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.catalog.BranchProductOverride
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BranchProductOverrideDaoTest {

    @Test
    fun `BranchProductOverrideDao interface defines required persistence operations`() {
        val upsertMethod = BranchProductOverrideDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("BranchProductOverrideDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(BranchProductOverrideEntity::class.java, upsertMethod.parameterTypes[0])

        val findByBranchAndProductMethod =
            BranchProductOverrideDao::class.java.methods.firstOrNull { it.name == "findByBranchAndProduct" }
        assertNotNull("BranchProductOverrideDao must declare findByBranchAndProduct method", findByBranchAndProductMethod)
        assertEquals(2, findByBranchAndProductMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBranchAndProductMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBranchAndProductMethod.parameterTypes[1])
        assertEquals(BranchProductOverrideEntity::class.java, findByBranchAndProductMethod.returnType)

        val findByBranchIdMethod =
            BranchProductOverrideDao::class.java.methods.firstOrNull { it.name == "findByBranchId" }
        assertNotNull("BranchProductOverrideDao must declare findByBranchId method", findByBranchIdMethod)
        assertEquals(1, findByBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBranchIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBranchIdMethod.returnType)

        val findByProductIdMethod =
            BranchProductOverrideDao::class.java.methods.firstOrNull { it.name == "findByProductId" }
        assertNotNull("BranchProductOverrideDao must declare findByProductId method", findByProductIdMethod)
        assertEquals(1, findByProductIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByProductIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByProductIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates BranchProductOverrideDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.BranchProductOverrideDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement BranchProductOverrideDao",
            BranchProductOverrideDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : BranchProductOverrideDao {
            private val storage = mutableMapOf<Pair<String, String>, BranchProductOverrideEntity>()

            override fun upsert(override: BranchProductOverrideEntity) {
                storage[Pair(override.branchId, override.productId)] = override
            }

            override fun findByBranchAndProduct(
                branchId: String,
                productId: String
            ): BranchProductOverrideEntity? {
                return storage[Pair(branchId, productId)]
            }

            override fun findByBranchId(branchId: String): List<BranchProductOverrideEntity> {
                return storage.values.filter { it.branchId == branchId }
            }

            override fun findByProductId(productId: String): List<BranchProductOverrideEntity> {
                return storage.values.filter { it.productId == productId }
            }
        }

        val domainOverride = BranchProductOverride(
            branchId = "branch-101",
            productId = "prod-101",
            stock = 50.0,
            minStock = 10.0,
            localCostPrice = 2000L,
            localSellingPrice = 3000L,
            isAvailable = true
        )

        // 1. Insert/Upsert
        fakeDao.upsert(domainOverride.toEntity())

        // 2. Retrieve by Branch + Product
        val retrievedEntity = fakeDao.findByBranchAndProduct("branch-101", "prod-101")
        assertNotNull("Retrieved entity must not be null", retrievedEntity)
        assertEquals("branch-101", retrievedEntity!!.branchId)
        assertEquals("prod-101", retrievedEntity.productId)
        assertEquals(50.0, retrievedEntity.stock, 0.0001)
        assertEquals(2000L, retrievedEntity.localCostPrice)
        assertEquals(3000L, retrievedEntity.localSellingPrice)

        // 3. Map back to domain
        val retrievedDomain = retrievedEntity.toDomain()
        assertEquals(domainOverride, retrievedDomain)

        // 4. Non-existent returns null
        assertNull(fakeDao.findByBranchAndProduct("branch-101", "missing-prod"))
    }

    @Test
    fun `same product in different branches maintains independent override state`() {
        val fakeDao = object : BranchProductOverrideDao {
            private val storage = mutableMapOf<Pair<String, String>, BranchProductOverrideEntity>()

            override fun upsert(override: BranchProductOverrideEntity) {
                storage[Pair(override.branchId, override.productId)] = override
            }

            override fun findByBranchAndProduct(
                branchId: String,
                productId: String
            ): BranchProductOverrideEntity? {
                return storage[Pair(branchId, productId)]
            }

            override fun findByBranchId(branchId: String): List<BranchProductOverrideEntity> {
                return storage.values.filter { it.branchId == branchId }
            }

            override fun findByProductId(productId: String): List<BranchProductOverrideEntity> {
                return storage.values.filter { it.productId == productId }
            }
        }

        val productId = "prod-shared-01"

        val overrideBranchA = BranchProductOverride(
            branchId = "branch-A",
            productId = productId,
            stock = 100.0,
            minStock = 20.0,
            localSellingPrice = 5000L,
            isAvailable = true
        )

        val overrideBranchB = BranchProductOverride(
            branchId = "branch-B",
            productId = productId,
            stock = 15.0,
            minStock = 5.0,
            localSellingPrice = 5500L,
            isAvailable = false
        )

        fakeDao.upsert(overrideBranchA.toEntity())
        fakeDao.upsert(overrideBranchB.toEntity())

        val retrievedA = fakeDao.findByBranchAndProduct("branch-A", productId)
        val retrievedB = fakeDao.findByBranchAndProduct("branch-B", productId)

        assertNotNull(retrievedA)
        assertNotNull(retrievedB)

        // Verify independent values
        assertEquals(100.0, retrievedA!!.stock, 0.0001)
        assertEquals(5000L, retrievedA.localSellingPrice)
        assertTrue(retrievedA.isAvailable)

        assertEquals(15.0, retrievedB!!.stock, 0.0001)
        assertEquals(5500L, retrievedB.localSellingPrice)
        assertEquals(false, retrievedB.isAvailable)

        // Branch-scoped listing
        val branchAList = fakeDao.findByBranchId("branch-A")
        assertEquals(1, branchAList.size)
        assertEquals(100.0, branchAList[0].stock, 0.0001)

        val branchBList = fakeDao.findByBranchId("branch-B")
        assertEquals(1, branchBList.size)
        assertEquals(15.0, branchBList[0].stock, 0.0001)

        // Product-scoped listing across all branches
        val productOverrides = fakeDao.findByProductId(productId)
        assertEquals(2, productOverrides.size)
    }

    @Test
    fun `duplicate branch and product upsert updates existing row`() {
        val fakeDao = object : BranchProductOverrideDao {
            private val storage = mutableMapOf<Pair<String, String>, BranchProductOverrideEntity>()

            override fun upsert(override: BranchProductOverrideEntity) {
                storage[Pair(override.branchId, override.productId)] = override
            }

            override fun findByBranchAndProduct(
                branchId: String,
                productId: String
            ): BranchProductOverrideEntity? {
                return storage[Pair(branchId, productId)]
            }

            override fun findByBranchId(branchId: String): List<BranchProductOverrideEntity> {
                return storage.values.filter { it.branchId == branchId }
            }

            override fun findByProductId(productId: String): List<BranchProductOverrideEntity> {
                return storage.values.filter { it.productId == productId }
            }
        }

        val initial = BranchProductOverride(
            branchId = "branch-01",
            productId = "prod-01",
            stock = 10.0,
            localSellingPrice = 10000L
        )
        fakeDao.upsert(initial.toEntity())

        val updated = BranchProductOverride(
            branchId = "branch-01",
            productId = "prod-01",
            stock = 25.0,
            localSellingPrice = 11000L
        )
        fakeDao.upsert(updated.toEntity())

        val retrieved = fakeDao.findByBranchAndProduct("branch-01", "prod-01")
        assertNotNull(retrieved)
        assertEquals(25.0, retrieved!!.stock, 0.0001)
        assertEquals(11000L, retrieved.localSellingPrice)
        assertEquals(1, fakeDao.findByBranchId("branch-01").size)
    }

    @Test
    fun `no duplicate authority test validates ProductEntity and BranchProductOverride separation`() {
        val productFields = ProductEntity::class.java.declaredFields.map { it.name }.toSet()
        val overrideFields = BranchProductOverrideEntity::class.java.declaredFields.map { it.name }.toSet()

        // ProductEntity must not contain branch operational override fields
        assertTrue(!productFields.contains("branchId"))
        assertTrue(!productFields.contains("stock"))
        assertTrue(!productFields.contains("minStock"))
        assertTrue(!productFields.contains("localSellingPrice"))
        assertTrue(!productFields.contains("localCostPrice"))
        assertTrue(!productFields.contains("isAvailable"))

        // BranchProductOverrideEntity must not contain Product master definition fields
        assertTrue(!overrideFields.contains("name"))
        assertTrue(!overrideFields.contains("sku"))
        assertTrue(!overrideFields.contains("barcode"))
        assertTrue(!overrideFields.contains("categoryId"))
        assertTrue(!overrideFields.contains("productType"))
        assertTrue(!overrideFields.contains("baseUnit"))
        assertTrue(!overrideFields.contains("defaultSellingPrice"))
        assertTrue(!overrideFields.contains("defaultCostPrice"))
    }
}
