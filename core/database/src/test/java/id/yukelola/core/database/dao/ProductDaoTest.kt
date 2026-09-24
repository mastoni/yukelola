package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.ProductEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.catalog.Product
import id.yukelola.core.domain.model.catalog.ProductType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductDaoTest {

    @Test
    fun `ProductDao interface defines required persistence operations`() {
        val upsertMethod = ProductDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("ProductDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(ProductEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = ProductDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("ProductDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(ProductEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            ProductDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("ProductDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(ProductEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByBusinessIdMethod = ProductDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("ProductDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates ProductDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.ProductDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement ProductDao",
            ProductDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : ProductDao {
            private val storage = mutableMapOf<String, ProductEntity>()

            override fun upsert(product: ProductEntity) {
                storage[product.id] = product
            }

            override fun findById(id: String): ProductEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): ProductEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByBusinessId(businessId: String): List<ProductEntity> {
                return storage.values.filter { it.businessId == businessId }
            }
        }

        val domainProduct = Product(
            id = "prod-101",
            businessId = "biz-101",
            categoryId = "cat-101",
            sku = "SKU-KOPI-01",
            barcode = "899123456701",
            name = "Kopi Kapal Api Spesial",
            productType = ProductType.PHYSICAL,
            baseUnit = "PCS",
            defaultCostPrice = 1500L,
            defaultSellingPrice = 2000L,
            trackStock = true,
            isActive = true
        )

        // 1. Insert/Upsert
        fakeDao.upsert(domainProduct.toEntity())

        // 2. Retrieve by ID
        val retrievedEntity = fakeDao.findById("prod-101")
        assertNotNull("Retrieved entity must not be null", retrievedEntity)
        assertEquals("prod-101", retrievedEntity!!.id)
        assertEquals("biz-101", retrievedEntity.businessId)
        assertEquals("SKU-KOPI-01", retrievedEntity.sku)
        assertEquals("Kopi Kapal Api Spesial", retrievedEntity.name)

        // 3. Map back to domain
        val retrievedDomain = retrievedEntity.toDomain()
        assertEquals(domainProduct, retrievedDomain)

        // 4. Non-existent returns null
        assertNull(fakeDao.findById("missing-product"))
    }

    @Test
    fun `business isolation test ensures products belonging to different businesses remain distinct`() {
        val fakeDao = object : ProductDao {
            private val storage = mutableMapOf<String, ProductEntity>()

            override fun upsert(product: ProductEntity) {
                storage[product.id] = product
            }

            override fun findById(id: String): ProductEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): ProductEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByBusinessId(businessId: String): List<ProductEntity> {
                return storage.values.filter { it.businessId == businessId }
            }
        }

        // Business A has Product A
        val productA = Product(
            id = "prod-A",
            businessId = "biz-A",
            name = "Product Business A",
            productType = ProductType.PHYSICAL,
            defaultCostPrice = 1000L,
            defaultSellingPrice = 1500L
        )

        // Business B has Product B
        val productB = Product(
            id = "prod-B",
            businessId = "biz-B",
            name = "Product Business B",
            productType = ProductType.DIGITAL,
            defaultCostPrice = 5000L,
            defaultSellingPrice = 6000L
        )

        fakeDao.upsert(productA.toEntity())
        fakeDao.upsert(productB.toEntity())

        // Verify independent retrieval and distinct businessId
        val retrievedA = fakeDao.findById("prod-A")
        val retrievedB = fakeDao.findById("prod-B")

        assertNotNull(retrievedA)
        assertNotNull(retrievedB)
        assertEquals("biz-A", retrievedA!!.businessId)
        assertEquals("biz-B", retrievedB!!.businessId)

        // Scoped lookup by businessId
        val businessAProducts = fakeDao.findByBusinessId("biz-A")
        assertEquals(1, businessAProducts.size)
        assertEquals("prod-A", businessAProducts[0].id)

        val businessBProducts = fakeDao.findByBusinessId("biz-B")
        assertEquals(1, businessBProducts.size)
        assertEquals("prod-B", businessBProducts[0].id)

        // Cross-business lookup returns null
        assertNull(fakeDao.findByIdAndBusinessId("prod-A", "biz-B"))
        assertNull(fakeDao.findByIdAndBusinessId("prod-B", "biz-A"))
    }

    @Test
    fun `product entity boundary test proves absence of branch operational override fields`() {
        // Verify ProductEntity fields do NOT contain branch operational state
        val declaredFieldNames = ProductEntity::class.java.declaredFields.map { it.name }.toSet()

        assertTrue("Must NOT contain branchId", !declaredFieldNames.contains("branchId"))
        assertTrue("Must NOT contain branch_id", !declaredFieldNames.contains("branch_id"))
        assertTrue("Must NOT contain stock", !declaredFieldNames.contains("stock"))
        assertTrue("Must NOT contain minStock", !declaredFieldNames.contains("minStock"))
        assertTrue("Must NOT contain min_stock", !declaredFieldNames.contains("min_stock"))
        assertTrue("Must NOT contain localSellingPrice", !declaredFieldNames.contains("localSellingPrice"))
        assertTrue("Must NOT contain local_selling_price", !declaredFieldNames.contains("local_selling_price"))
        assertTrue("Must NOT contain isAvailable", !declaredFieldNames.contains("isAvailable"))
        assertTrue("Must NOT contain is_available", !declaredFieldNames.contains("is_available"))
    }
}
