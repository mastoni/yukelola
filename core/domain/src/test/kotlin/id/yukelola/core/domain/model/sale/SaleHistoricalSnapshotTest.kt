package id.yukelola.core.domain.model.sale

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.catalog.BranchProductOverride
import id.yukelola.core.domain.model.catalog.Product
import id.yukelola.core.domain.model.catalog.ProductType
import org.junit.Assert.assertEquals
import org.junit.Test

class SaleHistoricalSnapshotTest {

    private val sampleAttribution = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-01",
        userId = "user-kasir-01",
        deviceId = "dev-pos-01",
        createdAt = 1711234000000L
    )

    @Test
    fun `Completed Sale preserves historical unit price and product name even after master catalog modifications`() {
        var masterProduct = Product(
            id = "prod-telur-1kg",
            businessId = "biz-01",
            name = "Telur Ayam 1kg",
            productType = ProductType.PHYSICAL,
            defaultCostPrice = 24000L,
            defaultSellingPrice = 28000L
        )

        var branchOverride = BranchProductOverride(
            branchId = "branch-01",
            productId = masterProduct.id,
            stock = 50.0,
            localSellingPrice = 28000L
        )

        // Snapshot line item at transaction execution time
        val saleItem = SaleItem(
            id = "item-01",
            saleId = "sale-100",
            productId = masterProduct.id,
            productName = masterProduct.name,
            unit = masterProduct.baseUnit,
            unitPrice = branchOverride.getEffectiveSellingPrice(masterProduct.defaultSellingPrice),
            costPrice = masterProduct.defaultCostPrice,
            quantity = 2.0
        )

        val completedSale = Sale(
            id = "sale-100",
            saleNumber = "TRX-001",
            attribution = sampleAttribution,
            items = listOf(saleItem),
            paidAmount = 56000L,
            paymentStatus = PaymentStatus.PAID,
            status = SaleStatus.COMPLETED,
            completedAt = 1711234050000L
        )

        // Next Day: Supplier price inflates; Master product and branch prices are updated
        masterProduct = masterProduct.copy(name = "Telur Ayam Negeri Premium 1kg", defaultSellingPrice = 32000L)
        branchOverride = branchOverride.withPricing(localSellingPrice = 33000L, localCostPrice = 29000L)

        // Historical Sale remains 100% untouched
        assertEquals("Telur Ayam 1kg", completedSale.items[0].productName)
        assertEquals(28000L, completedSale.items[0].unitPrice)
        assertEquals(56000L, completedSale.items[0].subtotal)
        assertEquals(56000L, completedSale.totalAmount)
    }
}
