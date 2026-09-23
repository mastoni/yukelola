package id.yukelola.core.domain.model.sale

import org.junit.Assert.assertEquals
import org.junit.Test

class SaleItemTest {

    @Test
    fun `SaleItem calculates line subtotal correctly without discount`() {
        val item = SaleItem(
            id = "item-01",
            saleId = "sale-01",
            productId = "prod-01",
            productName = "Indomie Goreng",
            unit = "BUNGKUS",
            unitPrice = 3500L,
            costPrice = 3000L,
            quantity = 5.0
        )

        assertEquals(17500L, item.subtotal)
    }

    @Test
    fun `SaleItem calculates line subtotal correctly with item discount`() {
        val item = SaleItem(
            id = "item-02",
            saleId = "sale-01",
            productId = "prod-02",
            productName = "Susu UHT 1L",
            unitPrice = 20000L,
            quantity = 2.0,
            discountAmount = 3000L
        )

        // (2 * 20000) - 3000 = 37000
        assertEquals(37000L, item.subtotal)
    }

    @Test
    fun `SaleItem discount is capped to prevent negative subtotal`() {
        val item = SaleItem(
            id = "item-03",
            saleId = "sale-01",
            productId = "prod-03",
            productName = "Promo Item",
            unitPrice = 5000L,
            quantity = 1.0,
            discountAmount = 10000L // Exceeds gross amount
        )

        // Capped at zero
        assertEquals(0L, item.subtotal)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `SaleItem with zero or negative quantity throws exception`() {
        SaleItem(
            id = "item-04",
            saleId = "sale-01",
            productId = "prod-04",
            productName = "Invalid Item",
            unitPrice = 5000L,
            quantity = 0.0
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `SaleItem with negative unitPrice throws exception`() {
        SaleItem(
            id = "item-05",
            saleId = "sale-01",
            productId = "prod-05",
            productName = "Negative Price Item",
            unitPrice = -5000L,
            quantity = 1.0
        )
    }
}
