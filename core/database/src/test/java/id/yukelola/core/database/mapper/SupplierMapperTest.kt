package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.SupplierEntity
import id.yukelola.core.domain.model.actor.Supplier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SupplierMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly with phone populated`() {
        val domain = Supplier(
            id = "sup-01",
            businessId = "biz-01",
            name = "PT Sumber Makmur",
            phone = "081234567890",
            debtBalance = 2500000L,
            isActive = true
        )

        val entity = domain.toEntity()

        assertEquals("sup-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("PT Sumber Makmur", entity.name)
        assertEquals("081234567890", entity.phone)
        assertEquals(2500000L, entity.debtBalance)
        assertTrue(entity.isActive)
    }

    @Test
    fun `entity to domain maps all fields losslessly with phone null`() {
        val entity = SupplierEntity(
            id = "sup-02",
            businessId = "biz-02",
            name = "CV Maju Jaya",
            phone = null,
            debtBalance = 0L,
            isActive = true
        )

        val domain = entity.toDomain()

        assertEquals("sup-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("CV Maju Jaya", domain.name)
        assertNull(domain.phone)
        assertEquals(0L, domain.debtBalance)
        assertTrue(domain.isActive)
    }

    @Test
    fun `full round trip preserves exact domain identity and state`() {
        val original = Supplier(
            id = "sup-03",
            businessId = "biz-03",
            name = "Distributor Utama",
            phone = "085678901234",
            debtBalance = 10000000L,
            isActive = false
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }
}
