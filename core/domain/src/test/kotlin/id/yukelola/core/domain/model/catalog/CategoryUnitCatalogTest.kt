package id.yukelola.core.domain.model.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CategoryUnitCatalogTest {

    @Test
    fun `Category belongs to Business and supports sorting and visual styling`() {
        val category1 = Category(
            id = "cat-sembako",
            businessId = "biz-01",
            name = "Sembako",
            color = "#4CAF50",
            icon = "ic_grocery",
            sortOrder = 1
        )

        val category2 = Category(
            id = "cat-minuman",
            businessId = "biz-01",
            name = "Minuman Dingin",
            sortOrder = 2
        )

        assertEquals("cat-sembako", category1.id)
        assertEquals("biz-01", category1.businessId)
        assertEquals(1, category1.sortOrder)
        assertEquals(2, category2.sortOrder)
        assertNotEquals(category1.name, category2.name)
    }

    @Test
    fun `ProductUnit supports measurement units and fractional conversion factors`() {
        val unitPcs = ProductUnit(
            id = "unit-pcs",
            businessId = "biz-01",
            name = "Pieces",
            symbol = "PCS",
            conversionFactor = 1.0
        )

        val unitBox = ProductUnit(
            id = "unit-box",
            businessId = "biz-01",
            name = "Box Isi 10",
            symbol = "BOX",
            conversionFactor = 10.0
        )

        assertEquals("PCS", unitPcs.symbol)
        assertEquals("BOX", unitBox.symbol)
        assertEquals(10.0, unitBox.conversionFactor, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `ProductUnit with non-positive conversion factor throws exception`() {
        ProductUnit(
            id = "unit-invalid",
            businessId = "biz-01",
            name = "Invalid Unit",
            symbol = "INV",
            conversionFactor = 0.0
        )
    }
}
