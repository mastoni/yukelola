package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.PurchaseItemEntity
import id.yukelola.core.domain.model.purchase.PurchaseItem
import org.junit.Assert.assertEquals
import org.junit.Test

class PurchaseItemMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly`() {
        val domain = PurchaseItem(
            id = "pi-01",
            purchaseId = "purch-01",
            productId = "prod-01",
            productName = "Product 1",
            unit = "PCS",
            unitCost = 20000L,
            quantity = 5.0,
            subtotal = 100000L
        )

        val entity = domain.toEntity()

        assertEquals("pi-01", entity.id)
        assertEquals("purch-01", entity.purchaseId)
        assertEquals("prod-01", entity.productId)
        assertEquals("Product 1", entity.productName)
        assertEquals("PCS", entity.unit)
        assertEquals(20000L, entity.unitCost)
        assertEquals(5.0, entity.quantity, 0.0001)
        assertEquals(100000L, entity.subtotal)
    }

    @Test
    fun `entity to domain maps all fields losslessly`() {
        val entity = PurchaseItemEntity(
            id = "pi-02",
            purchaseId = "purch-02",
            productId = "prod-02",
            productName = "Product 2",
            unit = "KARUNG",
            unitCost = 300000L,
            quantity = 2.5,
            subtotal = 750000L
        )

        val domain = entity.toDomain()

        assertEquals("pi-02", domain.id)
        assertEquals("purch-02", domain.purchaseId)
        assertEquals("prod-02", domain.productId)
        assertEquals("Product 2", domain.productName)
        assertEquals("KARUNG", domain.unit)
        assertEquals(300000L, domain.unitCost)
        assertEquals(2.5, domain.quantity, 0.0001)
        assertEquals(750000L, domain.subtotal)
    }

    @Test
    fun `full round trip preserves exact domain identity and historical snapshot`() {
        val domain = PurchaseItem(
            id = "pi-03",
            purchaseId = "purch-03",
            productId = "prod-03",
            productName = "Historical Procured Product",
            unit = "DUS",
            unitCost = 125000L,
            quantity = 12.0,
            subtotal = 1500000L
        )

        val entity = domain.toEntity()
        val restored = entity.toDomain()

        assertEquals(domain, restored)
    }
}
