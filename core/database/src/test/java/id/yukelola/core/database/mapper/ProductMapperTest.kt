package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.ProductEntity
import id.yukelola.core.domain.model.catalog.Product
import id.yukelola.core.domain.model.catalog.ProductType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductMapperTest {

    @Test
    fun `domain to entity preserves all canonical fields`() {
        val domain = Product(
            id = "prod-001",
            businessId = "biz-001",
            categoryId = "cat-001",
            sku = "SKU-INDOMIE-01",
            barcode = "8992345678901",
            name = "Indomie Goreng Spesial",
            productType = ProductType.PHYSICAL,
            baseUnit = "PCS",
            defaultCostPrice = 2500L,
            defaultSellingPrice = 3500L,
            trackStock = true,
            isActive = true
        )

        val entity = domain.toEntity()

        assertEquals("prod-001", entity.id)
        assertEquals("biz-001", entity.businessId)
        assertEquals("cat-001", entity.categoryId)
        assertEquals("SKU-INDOMIE-01", entity.sku)
        assertEquals("8992345678901", entity.barcode)
        assertEquals("Indomie Goreng Spesial", entity.name)
        assertEquals("PHYSICAL", entity.productType)
        assertEquals("PCS", entity.baseUnit)
        assertEquals(2500L, entity.defaultCostPrice)
        assertEquals(3500L, entity.defaultSellingPrice)
        assertTrue(entity.trackStock)
        assertTrue(entity.isActive)
    }

    @Test
    fun `entity to domain preserves all canonical fields with nullable properties`() {
        val entity = ProductEntity(
            id = "prod-002",
            businessId = "biz-002",
            categoryId = null,
            sku = null,
            barcode = null,
            name = "Jasa Servis Ringan",
            productType = "SERVICE",
            baseUnit = "JAM",
            defaultCostPrice = 0L,
            defaultSellingPrice = 50000L,
            trackStock = false,
            isActive = false
        )

        val domain = entity.toDomain()

        assertEquals("prod-002", domain.id)
        assertEquals("biz-002", domain.businessId)
        assertNull(domain.categoryId)
        assertNull(domain.sku)
        assertNull(domain.barcode)
        assertEquals("Jasa Servis Ringan", domain.name)
        assertEquals(ProductType.SERVICE, domain.productType)
        assertEquals("JAM", domain.baseUnit)
        assertEquals(0L, domain.defaultCostPrice)
        assertEquals(50000L, domain.defaultSellingPrice)
        assertFalse(domain.trackStock)
        assertFalse(domain.isActive)
    }

    @Test
    fun `roundtrip domain to entity to domain preserves all product types`() {
        val physical = Product(
            id = "p-phys",
            businessId = "biz-1",
            categoryId = "cat-1",
            sku = "SKU-1",
            barcode = "BAR-1",
            name = "Beras 5kg",
            productType = ProductType.PHYSICAL,
            baseUnit = "SAK",
            defaultCostPrice = 60000L,
            defaultSellingPrice = 70000L,
            trackStock = true,
            isActive = true
        )

        val service = Product(
            id = "p-srv",
            businessId = "biz-1",
            categoryId = "cat-2",
            sku = null,
            barcode = null,
            name = "Cuci Kilat",
            productType = ProductType.SERVICE,
            baseUnit = "KG",
            defaultCostPrice = 2000L,
            defaultSellingPrice = 10000L,
            trackStock = false,
            isActive = true
        )

        val digital = Product(
            id = "p-dig",
            businessId = "biz-1",
            categoryId = "cat-3",
            sku = "PLN-20K",
            barcode = null,
            name = "Token Listrik 20rb",
            productType = ProductType.DIGITAL,
            baseUnit = "TRX",
            defaultCostPrice = 20100L,
            defaultSellingPrice = 22500L,
            trackStock = false,
            isActive = true
        )

        assertEquals(physical, physical.toEntity().toDomain())
        assertEquals(service, service.toEntity().toDomain())
        assertEquals(digital, digital.toEntity().toDomain())
    }
}
