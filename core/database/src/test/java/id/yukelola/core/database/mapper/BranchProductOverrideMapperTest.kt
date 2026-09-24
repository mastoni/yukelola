package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.BranchProductOverrideEntity
import id.yukelola.core.domain.model.catalog.BranchProductOverride
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BranchProductOverrideMapperTest {

    @Test
    fun `domain to entity preserves all canonical fields`() {
        val domain = BranchProductOverride(
            branchId = "branch-001",
            productId = "prod-001",
            stock = 25.5,
            minStock = 5.0,
            localCostPrice = 3000L,
            localSellingPrice = 4500L,
            isAvailable = true
        )

        val entity = domain.toEntity()

        assertEquals("branch-001", entity.branchId)
        assertEquals("prod-001", entity.productId)
        assertEquals(25.5, entity.stock, 0.0001)
        assertEquals(5.0, entity.minStock, 0.0001)
        assertEquals(3000L, entity.localCostPrice)
        assertEquals(4500L, entity.localSellingPrice)
        assertTrue(entity.isAvailable)
    }

    @Test
    fun `entity to domain preserves all canonical fields with nullable prices`() {
        val entity = BranchProductOverrideEntity(
            branchId = "branch-002",
            productId = "prod-002",
            stock = 0.0,
            minStock = 0.0,
            localCostPrice = null,
            localSellingPrice = null,
            isAvailable = false
        )

        val domain = entity.toDomain()

        assertEquals("branch-002", domain.branchId)
        assertEquals("prod-002", domain.productId)
        assertEquals(0.0, domain.stock, 0.0001)
        assertEquals(0.0, domain.minStock, 0.0001)
        assertNull(domain.localCostPrice)
        assertNull(domain.localSellingPrice)
        assertFalse(domain.isAvailable)
    }

    @Test
    fun `roundtrip domain to entity to domain produces identical aggregate`() {
        val original = BranchProductOverride(
            branchId = "branch-003",
            productId = "prod-003",
            stock = 100.0,
            minStock = 10.0,
            localCostPrice = 12000L,
            localSellingPrice = 15000L,
            isAvailable = true
        )

        val roundtrip = original.toEntity().toDomain()

        assertEquals(original, roundtrip)
    }
}
