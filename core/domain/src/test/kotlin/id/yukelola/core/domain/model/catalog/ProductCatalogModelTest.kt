package id.yukelola.core.domain.model.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductCatalogModelTest {

    @Test
    fun `ProductType correctly reports physical stock requirement`() {
        assertTrue(ProductType.PHYSICAL.requiresPhysicalStock)
        assertFalse(ProductType.SERVICE.requiresPhysicalStock)
        assertFalse(ProductType.DIGITAL.requiresPhysicalStock)
    }

    @Test
    fun `Product master entity initializes with global defaults`() {
        val product = Product(
            id = "prod-001",
            businessId = "biz-123",
            categoryId = "cat-sembako",
            sku = "SKU-MINYAK-1L",
            barcode = "8991234567890",
            name = "Minyak Goreng 1L",
            productType = ProductType.PHYSICAL,
            baseUnit = "PCS",
            defaultCostPrice = 14000L,
            defaultSellingPrice = 16000L
        )

        assertEquals("prod-001", product.id)
        assertEquals("biz-123", product.businessId)
        assertEquals(ProductType.PHYSICAL, product.productType)
        assertEquals(14000L, product.defaultCostPrice)
        assertEquals(16000L, product.defaultSellingPrice)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Product with negative price throws exception`() {
        Product(
            id = "prod-001",
            businessId = "biz-123",
            name = "Invalid Product",
            productType = ProductType.PHYSICAL,
            defaultSellingPrice = -500L
        )
    }

    @Test
    fun `BranchProductOverride correctly overrides pricing and evaluates low stock`() {
        val override = BranchProductOverride(
            branchId = "branch-01",
            productId = "prod-001",
            stock = 3.0,
            minStock = 5.0,
            localCostPrice = 14500L,
            localSellingPrice = 17000L,
            isAvailable = true
        )

        // Local override takes precedence
        assertEquals(17000L, override.getEffectiveSellingPrice(defaultPrice = 16000L))
        assertEquals(14500L, override.getEffectiveCostPrice(defaultCost = 14000L))

        // Low stock condition
        assertTrue(override.isLowStock)
    }

    @Test
    fun `BranchProductOverride falls back to default prices when overrides are null`() {
        val override = BranchProductOverride(
            branchId = "branch-02",
            productId = "prod-001",
            stock = 10.0,
            minStock = 5.0,
            localCostPrice = null,
            localSellingPrice = null
        )

        assertEquals(16000L, override.getEffectiveSellingPrice(defaultPrice = 16000L))
        assertEquals(14000L, override.getEffectiveCostPrice(defaultCost = 14000L))
        assertFalse(override.isLowStock)
    }
}
