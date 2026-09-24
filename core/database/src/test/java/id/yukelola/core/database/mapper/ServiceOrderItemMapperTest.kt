package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.ServiceOrderItemEntity
import id.yukelola.core.domain.model.serviceorder.ServiceOrderItem
import id.yukelola.core.domain.model.serviceorder.ServiceOrderItemType
import org.junit.Assert.assertEquals
import org.junit.Test

class ServiceOrderItemMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly for service labor`() {
        val domain = ServiceOrderItem(
            id = "soi-01",
            orderId = "so-01",
            productId = "prod-01",
            productName = "Cuci Setrika Express",
            itemType = ServiceOrderItemType.SERVICE_LABOR,
            unit = "KG",
            unitPrice = 12000L,
            costPrice = 3000L,
            quantity = 3.5,
            discountAmount = 2000L,
            subtotal = 40000L
        )

        val entity = domain.toEntity()

        assertEquals("soi-01", entity.id)
        assertEquals("so-01", entity.orderId)
        assertEquals("prod-01", entity.productId)
        assertEquals("Cuci Setrika Express", entity.productName)
        assertEquals("SERVICE_LABOR", entity.itemType)
        assertEquals("KG", entity.unit)
        assertEquals(12000L, entity.unitPrice)
        assertEquals(3000L, entity.costPrice)
        assertEquals(3.5, entity.quantity, 0.0001)
        assertEquals(2000L, entity.discountAmount)
        assertEquals(40000L, entity.subtotal)
    }

    @Test
    fun `domain to entity maps all fields losslessly for physical part`() {
        val domain = ServiceOrderItem(
            id = "soi-02",
            orderId = "so-02",
            productId = "prod-02",
            productName = "Kampas Rem Depan",
            itemType = ServiceOrderItemType.PHYSICAL_PART,
            unit = "SET",
            unitPrice = 85000L,
            costPrice = 65000L,
            quantity = 1.0,
            discountAmount = 0L,
            subtotal = 85000L
        )

        val entity = domain.toEntity()

        assertEquals("soi-02", entity.id)
        assertEquals("so-02", entity.orderId)
        assertEquals("prod-02", entity.productId)
        assertEquals("Kampas Rem Depan", entity.productName)
        assertEquals("PHYSICAL_PART", entity.itemType)
        assertEquals("SET", entity.unit)
        assertEquals(85000L, entity.unitPrice)
        assertEquals(65000L, entity.costPrice)
        assertEquals(1.0, entity.quantity, 0.0001)
        assertEquals(0L, entity.discountAmount)
        assertEquals(85000L, entity.subtotal)
    }

    @Test
    fun `entity to domain maps all fields losslessly`() {
        val entity = ServiceOrderItemEntity(
            id = "soi-03",
            orderId = "so-03",
            productId = "prod-03",
            productName = "Banner Vinyl 280gr",
            itemType = "SERVICE_LABOR",
            unit = "M2",
            unitPrice = 25000L,
            costPrice = 15000L,
            quantity = 6.0,
            discountAmount = 10000L,
            subtotal = 140000L
        )

        val domain = entity.toDomain()

        assertEquals("soi-03", domain.id)
        assertEquals("so-03", domain.orderId)
        assertEquals("prod-03", domain.productId)
        assertEquals("Banner Vinyl 280gr", domain.productName)
        assertEquals(ServiceOrderItemType.SERVICE_LABOR, domain.itemType)
        assertEquals("M2", domain.unit)
        assertEquals(25000L, domain.unitPrice)
        assertEquals(15000L, domain.costPrice)
        assertEquals(6.0, domain.quantity, 0.0001)
        assertEquals(10000L, domain.discountAmount)
        assertEquals(140000L, domain.subtotal)
    }

    @Test
    fun `full round trip preserves exact domain identity and precision`() {
        val original = ServiceOrderItem(
            id = "soi-04",
            orderId = "so-04",
            productId = "prod-04",
            productName = "Jilid Spiral Kawat",
            itemType = ServiceOrderItemType.SERVICE_LABOR,
            unit = "BUKU",
            unitPrice = 15000L,
            costPrice = 5000L,
            quantity = 10.0,
            discountAmount = 5000L,
            subtotal = 145000L
        )

        val entity = original.toEntity()
        val restored = entity.toDomain()

        assertEquals(original, restored)
    }
}
