package id.yukelola.core.domain.model.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CrossBranchStockIsolationTest {

    @Test
    fun `Mutating stock in Branch A does not mutate stock in Branch B`() {
        val masterProduct = Product(
            id = "prod-beras-5kg",
            businessId = "biz-01",
            name = "Beras Rojolele 5kg",
            productType = ProductType.PHYSICAL,
            defaultSellingPrice = 75000L
        )

        var branchAStock = BranchProductOverride(
            branchId = "branch-a",
            productId = masterProduct.id,
            stock = 50.0,
            minStock = 5.0
        )

        val branchBStock = BranchProductOverride(
            branchId = "branch-b",
            productId = masterProduct.id,
            stock = 20.0,
            minStock = 5.0
        )

        // Branch A performs a sale decrement (immutable transition via withStock)
        branchAStock = branchAStock.withStock(branchAStock.stock - 5.0)

        // Verify Branch A mutated locally, Branch B remains untouched
        assertEquals(45.0, branchAStock.stock, 0.001)
        assertEquals(20.0, branchBStock.stock, 0.001)
        assertNotEquals(branchAStock.stock, branchBStock.stock)
    }

    @Test
    fun `Branch pricing update does not leak across branches`() {
        val masterProduct = Product(
            id = "prod-telur-kg",
            businessId = "biz-01",
            name = "Telur Ayam 1kg",
            productType = ProductType.PHYSICAL,
            defaultSellingPrice = 28000L
        )

        var branchA = BranchProductOverride(
            branchId = "branch-a",
            productId = masterProduct.id,
            localSellingPrice = 29000L
        )

        val branchB = BranchProductOverride(
            branchId = "branch-b",
            productId = masterProduct.id,
            localSellingPrice = 30000L
        )

        // Branch A adjusts price due to local supplier cost
        branchA = branchA.withPricing(localSellingPrice = 28500L, localCostPrice = 26000L)

        assertEquals(28500L, branchA.getEffectiveSellingPrice(masterProduct.defaultSellingPrice))
        assertEquals(30000L, branchB.getEffectiveSellingPrice(masterProduct.defaultSellingPrice))
    }
}
