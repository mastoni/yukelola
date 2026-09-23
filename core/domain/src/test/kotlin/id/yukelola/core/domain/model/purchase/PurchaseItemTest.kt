package id.yukelola.core.domain.model.purchase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PurchaseItemTest {

    @Test
    fun `purchase item computes subtotal deterministically using integer money`() {
        val item = PurchaseItem(
            id = "pi-01",
            purchaseId = "purch-01",
            productId = "prod-01",
            productName = "Minyak Goreng 2L",
            unit = "KARTON",
            unitCost = 165000L,
            quantity = 10.0
        )

        assertEquals("pi-01", item.id)
        assertEquals("purch-01", item.purchaseId)
        assertEquals(165000L, item.unitCost)
        assertEquals(10.0, item.quantity, 0.0)
        assertEquals(1650000L, item.subtotal)
    }

    @Test
    fun `purchase item validates positive quantity and non-negative unit cost`() {
        assertThrows(IllegalArgumentException::class.java) {
            PurchaseItem(
                id = "pi-01",
                purchaseId = "purch-01",
                productId = "prod-01",
                productName = "Item",
                unitCost = 10000L,
                quantity = 0.0
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            PurchaseItem(
                id = "pi-01",
                purchaseId = "purch-01",
                productId = "prod-01",
                productName = "Item",
                unitCost = -500L,
                quantity = 1.0
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            PurchaseItem(
                id = "",
                purchaseId = "purch-01",
                productId = "prod-01",
                productName = "Item",
                unitCost = 10000L,
                quantity = 1.0
            )
        }
    }
}
