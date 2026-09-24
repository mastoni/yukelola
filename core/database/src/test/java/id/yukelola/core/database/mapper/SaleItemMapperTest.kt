package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.SaleItemEntity
import id.yukelola.core.domain.model.sale.SaleItem
import org.junit.Assert.assertEquals
import org.junit.Test

class SaleItemMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly`() {
        val domain = SaleItem(
            id = "item-01",
            saleId = "sale-01",
            productId = "prod-01",
            productName = "Product 1",
            unit = "PCS",
            unitPrice = 12000L,
            costPrice = 9000L,
            quantity = 3.0,
            discountAmount = 1000L,
            subtotal = 35000L
        )

        val entity = domain.toEntity()

        assertEquals("item-01", entity.id)
        assertEquals("sale-01", entity.saleId)
        assertEquals("prod-01", entity.productId)
        assertEquals("Product 1", entity.productName)
        assertEquals("PCS", entity.unit)
        assertEquals(12000L, entity.unitPrice)
        assertEquals(9000L, entity.costPrice)
        assertEquals(3.0, entity.quantity, 0.0001)
        assertEquals(1000L, entity.discountAmount)
        assertEquals(35000L, entity.subtotal)
    }

    @Test
    fun `entity to domain maps all fields losslessly`() {
        val entity = SaleItemEntity(
            id = "item-02",
            saleId = "sale-02",
            productId = "prod-02",
            productName = "Product 2",
            unit = "BOX",
            unitPrice = 50000L,
            costPrice = 40000L,
            quantity = 2.5,
            discountAmount = 5000L,
            subtotal = 120000L
        )

        val domain = entity.toDomain()

        assertEquals("item-02", domain.id)
        assertEquals("sale-02", domain.saleId)
        assertEquals("prod-02", domain.productId)
        assertEquals("Product 2", domain.productName)
        assertEquals("BOX", domain.unit)
        assertEquals(50000L, domain.unitPrice)
        assertEquals(40000L, domain.costPrice)
        assertEquals(2.5, domain.quantity, 0.0001)
        assertEquals(5000L, domain.discountAmount)
        assertEquals(120000L, domain.subtotal)
    }

    @Test
    fun `full round trip preserves exact domain identity and historical snapshot`() {
        val domain = SaleItem(
            id = "item-03",
            saleId = "sale-03",
            productId = "prod-03",
            productName = "Historical Product Snapshot",
            unit = "KG",
            unitPrice = 30000L,
            costPrice = 25000L,
            quantity = 1.75,
            discountAmount = 2500L,
            subtotal = 50000L
        )

        val entity = domain.toEntity()
        val restored = entity.toDomain()

        assertEquals(domain, restored)
    }
}
