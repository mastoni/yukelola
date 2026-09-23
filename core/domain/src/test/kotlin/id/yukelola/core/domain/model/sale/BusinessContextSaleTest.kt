package id.yukelola.core.domain.model.sale

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BusinessContextSaleTest {

    private fun createAttribution(branchId: String) = TransactionAttribution(
        businessId = "biz-01",
        branchId = branchId,
        userId = "user-kasir",
        deviceId = "dev-pos",
        createdAt = 1711234000000L
    )

    @Test
    fun `RETAIL_HEALTH executes transactions using standard Sale and SaleItem`() {
        val healthSaleItem = SaleItem(
            id = "item-rx-01",
            saleId = "sale-apt-01",
            productId = "prod-amoxicillin-500",
            productName = "Amoxicillin 500mg (Strip)",
            unit = "STRIP",
            unitPrice = 12000L,
            quantity = 2.0
        )

        val healthSale = Sale(
            id = "sale-apt-01",
            saleNumber = "APT-20260924-001",
            attribution = createAttribution("branch-apotek-01"),
            items = listOf(healthSaleItem),
            notes = "Resep dr. Anton / 2x sehari sesudah makan"
        ).complete(paidAmount = 24000L, completedAt = 1711234050000L)

        assertTrue(healthSale.isCompleted)
        assertEquals(24000L, healthSale.totalAmount)
        assertEquals("Resep dr. Anton / 2x sehari sesudah makan", healthSale.notes)
    }

    @Test
    fun `WARUNG with auxiliary medicine executes transactions using standard Sale and SaleItem`() {
        val groceryItem = SaleItem(
            id = "item-groc-01",
            saleId = "sale-wrg-01",
            productId = "prod-beras-1kg",
            productName = "Beras 1kg",
            unit = "KG",
            unitPrice = 15000L,
            quantity = 1.0
        )

        val medicineItem = SaleItem(
            id = "item-med-01",
            saleId = "sale-wrg-01",
            productId = "prod-paracetamol-strip",
            productName = "Paracetamol 500mg Strip",
            unit = "STRIP",
            unitPrice = 5000L,
            quantity = 1.0
        )

        val warungSale = Sale(
            id = "sale-wrg-01",
            saleNumber = "WRG-20260924-001",
            attribution = createAttribution("branch-warung-01"),
            items = listOf(groceryItem, medicineItem)
        ).complete(paidAmount = 20000L, completedAt = 1711234050000L)

        assertTrue(warungSale.isCompleted)
        assertEquals(20000L, warungSale.totalAmount)
        assertEquals(2, warungSale.items.size)
    }

    @Test
    fun `F&B and walk-in Photocopy execute transactions using standard Sale and SaleItem`() {
        val foodItem = SaleItem(
            id = "item-fb-01",
            saleId = "sale-cafe-01",
            productId = "prod-nasi-goreng",
            productName = "Nasi Goreng Spesial",
            unit = "PORSI",
            unitPrice = 25000L,
            quantity = 2.0
        )

        val cafeSale = Sale(
            id = "sale-cafe-01",
            saleNumber = "CAFE-20260924-001",
            attribution = createAttribution("branch-cafe-01"),
            items = listOf(foodItem),
            notes = "Meja 4 / Tidak pedas"
        ).complete(paidAmount = 50000L, completedAt = 1711234050000L)

        assertTrue(cafeSale.isCompleted)
        assertEquals(50000L, cafeSale.totalAmount)

        val photocopyItem = SaleItem(
            id = "item-fc-01",
            saleId = "sale-fc-01",
            productId = "prod-fc-a4",
            productName = "Fotocopy A4 (Per Halaman)",
            unit = "LEMBAR",
            unitPrice = 300L,
            quantity = 50.0
        )

        val fcSale = Sale(
            id = "sale-fc-01",
            saleNumber = "FC-20260924-001",
            attribution = createAttribution("branch-fc-01"),
            items = listOf(photocopyItem)
        ).complete(paidAmount = 15000L, completedAt = 1711234050000L)

        assertTrue(fcSale.isCompleted)
        assertEquals(15000L, fcSale.totalAmount)
    }
}
