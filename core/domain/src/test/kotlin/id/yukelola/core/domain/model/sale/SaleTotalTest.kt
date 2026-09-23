package id.yukelola.core.domain.model.sale

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import org.junit.Assert.assertEquals
import org.junit.Test

class SaleTotalTest {

    private val sampleAttribution = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-01",
        userId = "user-kasir-01",
        deviceId = "dev-pos-01",
        createdAt = 1711234000000L
    )

    @Test
    fun `Sale correctly sums multiple line items and applies sale discount and tax`() {
        val item1 = SaleItem(
            id = "item-1",
            saleId = "sale-1",
            productId = "prod-1",
            productName = "Beras 5kg",
            unitPrice = 75000L,
            quantity = 2.0
        ) // 150000

        val item2 = SaleItem(
            id = "item-2",
            saleId = "sale-1",
            productId = "prod-2",
            productName = "Minyak 2L",
            unitPrice = 35000L,
            quantity = 1.0,
            discountAmount = 5000L
        ) // 30000

        val sale = Sale(
            id = "sale-1",
            saleNumber = "TRX-001",
            attribution = sampleAttribution,
            items = listOf(item1, item2),
            discountAmount = 10000L, // Sale level discount
            taxAmount = 2000L
        )

        // Subtotal = 150000 + 30000 = 180000
        assertEquals(180000L, sale.subtotal)
        // Total = 180000 - 10000 + 2000 = 172000
        assertEquals(172000L, sale.totalAmount)
        assertEquals(172000L, sale.remainingBalance)
    }

    @Test
    fun `Sale totalAmount does not drop below zero when discount exceeds subtotal`() {
        val item = SaleItem(
            id = "item-1",
            saleId = "sale-2",
            productId = "prod-1",
            productName = "Voucher Diskon",
            unitPrice = 10000L,
            quantity = 1.0
        )

        val sale = Sale(
            id = "sale-2",
            saleNumber = "TRX-002",
            attribution = sampleAttribution,
            items = listOf(item),
            discountAmount = 50000L // Exceeds subtotal
        )

        assertEquals(0L, sale.totalAmount)
    }
}
