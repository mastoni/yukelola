package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.CategoryEntity
import id.yukelola.core.database.entity.ProductEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.catalog.Category
import id.yukelola.core.domain.model.catalog.Product
import id.yukelola.core.domain.model.catalog.ProductType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryDaoTest {

    @Test
    fun `CategoryDao interface defines required persistence operations`() {
        val upsertMethod = CategoryDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("CategoryDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(CategoryEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = CategoryDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("CategoryDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(CategoryEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            CategoryDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("CategoryDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(CategoryEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByBusinessIdMethod =
            CategoryDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("CategoryDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates CategoryDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.CategoryDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement CategoryDao",
            CategoryDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : CategoryDao {
            private val storage = mutableMapOf<String, CategoryEntity>()

            override fun upsert(category: CategoryEntity) {
                storage[category.id] = category
            }

            override fun findById(id: String): CategoryEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): CategoryEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByBusinessId(businessId: String): List<CategoryEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedWith(compareBy({ it.sortOrder }, { it.name }))
            }
        }

        val domainCategory = Category(
            id = "cat-101",
            businessId = "biz-101",
            name = "Kategori Minuman",
            color = "#00BCD4",
            icon = "ic_drink",
            sortOrder = 2
        )

        // 1. Insert/Upsert
        fakeDao.upsert(domainCategory.toEntity())

        // 2. Retrieve by ID
        val retrievedEntity = fakeDao.findById("cat-101")
        assertNotNull("Retrieved entity must not be null", retrievedEntity)
        assertEquals("cat-101", retrievedEntity!!.id)
        assertEquals("biz-101", retrievedEntity.businessId)
        assertEquals("Kategori Minuman", retrievedEntity.name)
        assertEquals("#00BCD4", retrievedEntity.color)
        assertEquals(2, retrievedEntity.sortOrder)

        // 3. Map back to domain
        val retrievedDomain = retrievedEntity.toDomain()
        assertEquals(domainCategory, retrievedDomain)

        // 4. Non-existent returns null
        assertNull(fakeDao.findById("missing-cat"))
    }

    @Test
    fun `business isolation ensures categories belonging to different businesses remain distinct`() {
        val fakeDao = object : CategoryDao {
            private val storage = mutableMapOf<String, CategoryEntity>()

            override fun upsert(category: CategoryEntity) {
                storage[category.id] = category
            }

            override fun findById(id: String): CategoryEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): CategoryEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByBusinessId(businessId: String): List<CategoryEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedWith(compareBy({ it.sortOrder }, { it.name }))
            }
        }

        val categoryA = Category(
            id = "cat-A",
            businessId = "biz-A",
            name = "Category A",
            sortOrder = 1
        )

        val categoryB = Category(
            id = "cat-B",
            businessId = "biz-B",
            name = "Category B",
            sortOrder = 1
        )

        fakeDao.upsert(categoryA.toEntity())
        fakeDao.upsert(categoryB.toEntity())

        val retrievedA = fakeDao.findById("cat-A")
        val retrievedB = fakeDao.findById("cat-B")

        assertNotNull(retrievedA)
        assertNotNull(retrievedB)
        assertEquals("biz-A", retrievedA!!.businessId)
        assertEquals("biz-B", retrievedB!!.businessId)

        val businessACats = fakeDao.findByBusinessId("biz-A")
        assertEquals(1, businessACats.size)
        assertEquals("cat-A", businessACats[0].id)

        val businessBCats = fakeDao.findByBusinessId("biz-B")
        assertEquals(1, businessBCats.size)
        assertEquals("cat-B", businessBCats[0].id)

        assertNull(fakeDao.findByIdAndBusinessId("cat-A", "biz-B"))
        assertNull(fakeDao.findByIdAndBusinessId("cat-B", "biz-A"))
    }

    @Test
    fun `product category relationship retains loose reference without duplicate metadata`() {
        val category = Category(
            id = "cat-snack",
            businessId = "biz-01",
            name = "Snack & Makanan Ringan",
            color = "#FF9800",
            icon = "ic_snack",
            sortOrder = 1
        )

        val product = Product(
            id = "prod-chips",
            businessId = "biz-01",
            categoryId = category.id,
            name = "Keripik Singkong",
            productType = ProductType.PHYSICAL,
            defaultSellingPrice = 5000L
        )

        // ProductEntity only stores categoryId as a reference, not embedded category object
        val productEntity = product.toEntity()
        assertEquals(category.id, productEntity.categoryId)

        val productEntityFields = ProductEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("ProductEntity must NOT duplicate categoryName", !productEntityFields.contains("categoryName"))
        assertTrue("ProductEntity must NOT duplicate category_name", !productEntityFields.contains("category_name"))
        assertTrue("ProductEntity must NOT duplicate categoryColor", !productEntityFields.contains("categoryColor"))
        assertTrue("ProductEntity must NOT duplicate category_color", !productEntityFields.contains("category_color"))
    }
}
