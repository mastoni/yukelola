package id.yukelola.core.domain.model.sale

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BranchSaleIsolationTest {

    @Test
    fun `Sale is strictly bound to its operational branch attribution`() {
        val attributionBranchA = TransactionAttribution(
            businessId = "biz-01",
            branchId = "branch-a",
            userId = "user-kasir-a",
            deviceId = "dev-pos-a",
            createdAt = 1711234000000L
        )

        val saleBranchA = Sale(
            id = "sale-a-01",
            saleNumber = "TRX-A-001",
            attribution = attributionBranchA,
            items = listOf(
                SaleItem(
                    id = "item-a",
                    saleId = "sale-a-01",
                    productId = "prod-01",
                    productName = "Barang A",
                    unitPrice = 10000L,
                    quantity = 1.0
                )
            )
        )

        assertEquals("biz-01", saleBranchA.businessId)
        assertEquals("branch-a", saleBranchA.branchId)
        assertNotEquals("branch-b", saleBranchA.branchId)
    }
}
