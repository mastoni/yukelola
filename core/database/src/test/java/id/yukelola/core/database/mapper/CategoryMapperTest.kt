package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.CategoryEntity
import id.yukelola.core.domain.model.catalog.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CategoryMapperTest {

    @Test
    fun `domain to entity preserves all canonical fields`() {
        val domain = Category(
            id = "cat-001",
            businessId = "biz-001",
            name = "Makanan & Minuman",
            color = "#FF5722",
            icon = "ic_food",
            sortOrder = 1
        )

        val entity = domain.toEntity()

        assertEquals("cat-001", entity.id)
        assertEquals("biz-001", entity.businessId)
        assertEquals("Makanan & Minuman", entity.name)
        assertEquals("#FF5722", entity.color)
        assertEquals("ic_food", entity.icon)
        assertEquals(1, entity.sortOrder)
    }

    @Test
    fun `entity to domain preserves all canonical fields with nullable properties`() {
        val entity = CategoryEntity(
            id = "cat-002",
            businessId = "biz-002",
            name = "Sembako",
            color = null,
            icon = null,
            sortOrder = 0
        )

        val domain = entity.toDomain()

        assertEquals("cat-002", domain.id)
        assertEquals("biz-002", domain.businessId)
        assertEquals("Sembako", domain.name)
        assertNull(domain.color)
        assertNull(domain.icon)
        assertEquals(0, domain.sortOrder)
    }

    @Test
    fun `roundtrip domain to entity to domain produces identical aggregate`() {
        val original = Category(
            id = "cat-003",
            businessId = "biz-003",
            name = "Peralatan Rumah Tangga",
            color = "#2196F3",
            icon = "ic_home",
            sortOrder = 5
        )

        val roundtrip = original.toEntity().toDomain()

        assertEquals(original, roundtrip)
    }
}
