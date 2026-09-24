package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.ProductUnitEntity
import id.yukelola.core.domain.model.catalog.ProductUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductUnitMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly`() {
        val domain = ProductUnit(
            id = "unit-box-10",
            businessId = "biz-01",
            name = "Box Isi 10",
            symbol = "BOX",
            conversionFactor = 10.0
        )

        val entity = domain.toEntity()

        assertEquals("unit-box-10", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("Box Isi 10", entity.name)
        assertEquals("BOX", entity.symbol)
        assertEquals(10.0, entity.conversionFactor, 0.001)
    }

    @Test
    fun `entity to domain maps all fields losslessly`() {
        val entity = ProductUnitEntity(
            id = "unit-kg",
            businessId = "biz-02",
            name = "Kilogram",
            symbol = "KG",
            conversionFactor = 1.0
        )

        val domain = entity.toDomain()

        assertEquals("unit-kg", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("Kilogram", domain.name)
        assertEquals("KG", domain.symbol)
        assertEquals(1.0, domain.conversionFactor, 0.001)
    }

    @Test
    fun `full round trip preserves exact domain identity and precision`() {
        val original = ProductUnit(
            id = "unit-strip-6",
            businessId = "biz-pharma",
            name = "Strip 6 Tablet",
            symbol = "STRIP",
            conversionFactor = 6.0
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }
}
