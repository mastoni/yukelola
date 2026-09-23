package id.yukelola.core.domain.model.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductIdentityVsBranchStateTest {

    @Test
    fun `Same global Product Master maintains independent operational states across multiple branches`() {
        val masterProduct = Product(
            id = "prod-minyak-1l",
            businessId = "biz-corp-01",
            categoryId = "cat-sembako",
            sku = "SKU-MINYAK-01",
            barcode = "8991234567890",
            name = "Minyak Goreng 1L",
            productType = ProductType.PHYSICAL,
            baseUnit = "PCS",
            defaultCostPrice = 14000L,
            defaultSellingPrice = 16000L
        )

        // Branch 1: Urban Center (higher price, higher stock)
        val branch1Override = BranchProductOverride(
            branchId = "branch-urban",
            productId = masterProduct.id,
            stock = 150.0,
            minStock = 20.0,
            localCostPrice = 14200L,
            localSellingPrice = 17500L,
            isAvailable = true
        )

        // Branch 2: Suburban Kiosk (lower price, lower stock, out of stock scenario)
        val branch2Override = BranchProductOverride(
            branchId = "branch-suburban",
            productId = masterProduct.id,
            stock = 5.0,
            minStock = 10.0,
            localCostPrice = null, // uses default cost
            localSellingPrice = 16500L,
            isAvailable = true
        )

        // Identity check
        assertEquals(masterProduct.id, branch1Override.productId)
        assertEquals(masterProduct.id, branch2Override.productId)
        assertNotEquals(branch1Override.branchId, branch2Override.branchId)

        // Pricing check
        assertEquals(17500L, branch1Override.getEffectiveSellingPrice(masterProduct.defaultSellingPrice))
        assertEquals(16500L, branch2Override.getEffectiveSellingPrice(masterProduct.defaultSellingPrice))
        assertEquals(14200L, branch1Override.getEffectiveCostPrice(masterProduct.defaultCostPrice))
        assertEquals(14000L, branch2Override.getEffectiveCostPrice(masterProduct.defaultCostPrice))

        // Stock and threshold check
        assertEquals(150.0, branch1Override.stock, 0.001)
        assertEquals(5.0, branch2Override.stock, 0.001)
        assertFalse(branch1Override.isLowStock)
        assertTrue(branch2Override.isLowStock)
    }

    @Test
    fun `BranchProductOverride supports branch-specific availability toggling`() {
        val masterProduct = Product(
            id = "prod-es-kopi",
            businessId = "biz-corp-01",
            name = "Es Kopi Susu",
            productType = ProductType.PHYSICAL,
            defaultSellingPrice = 18000L
        )

        val branch1Available = BranchProductOverride(
            branchId = "branch-cafe-01",
            productId = masterProduct.id,
            isAvailable = true
        )

        val branch2Unavailable = BranchProductOverride(
            branchId = "branch-warung-02",
            productId = masterProduct.id,
            isAvailable = false
        )

        assertTrue(branch1Available.isAvailable)
        assertFalse(branch2Unavailable.isAvailable)
    }
}
