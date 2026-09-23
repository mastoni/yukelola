package id.yukelola.core.domain.model.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductTypeSemanticsTest {

    @Test
    fun `PHYSICAL product requires physical stock when trackStock is true`() {
        val physicalTracked = Product(
            id = "prod-phys-01",
            businessId = "biz-01",
            name = "Sabun Mandi",
            productType = ProductType.PHYSICAL,
            trackStock = true
        )

        assertTrue(physicalTracked.productType.requiresPhysicalStock)
        assertTrue(physicalTracked.isInventoryTracked)
    }

    @Test
    fun `PHYSICAL product bypasses stock tracking when trackStock is false`() {
        val physicalUntracked = Product(
            id = "prod-phys-02",
            businessId = "biz-01",
            name = "Gorengan Tempe",
            productType = ProductType.PHYSICAL,
            trackStock = false
        )

        assertTrue(physicalUntracked.productType.requiresPhysicalStock)
        assertFalse(physicalUntracked.isInventoryTracked)
    }

    @Test
    fun `SERVICE product never requires physical inventory tracking`() {
        val serviceProduct = Product(
            id = "prod-srv-01",
            businessId = "biz-01",
            name = "Jasa Servis Motor Ringan",
            productType = ProductType.SERVICE,
            trackStock = true // trackStock ignored for SERVICE
        )

        assertFalse(serviceProduct.productType.requiresPhysicalStock)
        assertFalse(serviceProduct.isInventoryTracked)
    }

    @Test
    fun `DIGITAL product never requires physical inventory tracking`() {
        val digitalProduct = Product(
            id = "prod-digi-01",
            businessId = "biz-01",
            sku = "PULSA_TELKOMSEL_25K",
            name = "Pulsa Telkomsel 25.000",
            productType = ProductType.DIGITAL,
            defaultCostPrice = 25100L,
            defaultSellingPrice = 27000L,
            trackStock = true
        )

        assertFalse(digitalProduct.productType.requiresPhysicalStock)
        assertFalse(digitalProduct.isInventoryTracked)
        assertEquals(ProductType.DIGITAL, digitalProduct.productType)
    }
}
