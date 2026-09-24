package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.ProductEntity
import id.yukelola.core.database.entity.ProductUnitEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.catalog.Product
import id.yukelola.core.domain.model.catalog.ProductType
import id.yukelola.core.domain.model.catalog.ProductUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductUnitDaoTest {

    @Test
    fun `ProductUnitDao interface defines required persistence operations`() {
        val upsertMethod = ProductUnitDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("ProductUnitDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(ProductUnitEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = ProductUnitDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("ProductUnitDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(ProductUnitEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            ProductUnitDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("ProductUnitDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(ProductUnitEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByBusinessIdMethod =
            ProductUnitDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("ProductUnitDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates ProductUnitDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.ProductUnitDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement ProductUnitDao",
            ProductUnitDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : ProductUnitDao {
            private val storage = mutableMapOf<String, ProductUnitEntity>()

            override fun upsert(productUnit: ProductUnitEntity) {
                storage[productUnit.id] = productUnit
            }

            override fun findById(id: String): ProductUnitEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): ProductUnitEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByBusinessId(businessId: String): List<ProductUnitEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedBy { it.name }
            }
        }

        val domainUnit = ProductUnit(
            id = "unit-box-12",
            businessId = "biz-101",
            name = "Box Isi 12",
            symbol = "BOX",
            conversionFactor = 12.0
        )

        // 1. Insert/Upsert
        fakeDao.upsert(domainUnit.toEntity())

        // 2. Retrieve by ID
        val retrievedEntity = fakeDao.findById("unit-box-12")
        assertNotNull("Retrieved entity must not be null", retrievedEntity)
        assertEquals("unit-box-12", retrievedEntity!!.id)
        assertEquals("biz-101", retrievedEntity.businessId)
        assertEquals("Box Isi 12", retrievedEntity.name)
        assertEquals("BOX", retrievedEntity.symbol)
        assertEquals(12.0, retrievedEntity.conversionFactor, 0.001)

        // 3. Map back to domain
        val retrievedDomain = retrievedEntity.toDomain()
        assertEquals(domainUnit, retrievedDomain)

        // 4. Non-existent returns null
        assertNull(fakeDao.findById("missing-unit"))
    }

    @Test
    fun `business isolation ensures product units belonging to different businesses remain distinct`() {
        val fakeDao = object : ProductUnitDao {
            private val storage = mutableMapOf<String, ProductUnitEntity>()

            override fun upsert(productUnit: ProductUnitEntity) {
                storage[productUnit.id] = productUnit
            }

            override fun findById(id: String): ProductUnitEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): ProductUnitEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByBusinessId(businessId: String): List<ProductUnitEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedBy { it.name }
            }
        }

        val unitA = ProductUnit(
            id = "unit-A",
            businessId = "biz-A",
            name = "Unit A",
            symbol = "UA",
            conversionFactor = 1.0
        )

        val unitB = ProductUnit(
            id = "unit-B",
            businessId = "biz-B",
            name = "Unit B",
            symbol = "UB",
            conversionFactor = 2.0
        )

        fakeDao.upsert(unitA.toEntity())
        fakeDao.upsert(unitB.toEntity())

        val retrievedA = fakeDao.findById("unit-A")
        val retrievedB = fakeDao.findById("unit-B")

        assertNotNull(retrievedA)
        assertNotNull(retrievedB)
        assertEquals("biz-A", retrievedA!!.businessId)
        assertEquals("biz-B", retrievedB!!.businessId)

        val businessAUnits = fakeDao.findByBusinessId("biz-A")
        assertEquals(1, businessAUnits.size)
        assertEquals("unit-A", businessAUnits[0].id)

        val businessBUnits = fakeDao.findByBusinessId("biz-B")
        assertEquals(1, businessBUnits.size)
        assertEquals("unit-B", businessBUnits[0].id)

        assertNull(fakeDao.findByIdAndBusinessId("unit-A", "biz-B"))
        assertNull(fakeDao.findByIdAndBusinessId("unit-B", "biz-A"))
    }

    @Test
    fun `product baseUnit and product unit authority separation has no duplicate authority`() {
        val product = Product(
            id = "prod-drink",
            businessId = "biz-01",
            name = "Mineral Water",
            productType = ProductType.PHYSICAL,
            baseUnit = "PCS",
            defaultSellingPrice = 3000L
        )

        val productEntity = product.toEntity()
        assertEquals("PCS", productEntity.baseUnit)

        // Ensure ProductEntity only stores baseUnit and does NOT embed ProductUnitEntity or duplicate unit tables
        val productEntityFields = ProductEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("ProductEntity must NOT duplicate unit_name", !productEntityFields.contains("unit_name"))
        assertTrue("ProductEntity must NOT duplicate unitName", !productEntityFields.contains("unitName"))
        assertTrue("ProductEntity must NOT duplicate conversionFactor", !productEntityFields.contains("conversionFactor"))
        assertTrue("ProductEntity must NOT duplicate conversion_factor", !productEntityFields.contains("conversion_factor"))
    }
}
