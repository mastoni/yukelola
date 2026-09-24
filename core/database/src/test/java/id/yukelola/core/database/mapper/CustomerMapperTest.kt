package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.CustomerEntity
import id.yukelola.core.domain.model.actor.Customer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomerMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly with all values populated`() {
        val domain = Customer(
            id = "cust-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Ahmad Dani",
            phone = "081234567890",
            debtBalance = 150000L,
            isActive = true
        )

        val entity = domain.toEntity()

        assertEquals("cust-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("Ahmad Dani", entity.name)
        assertEquals("081234567890", entity.phone)
        assertEquals(150000L, entity.debtBalance)
        assertTrue(entity.isActive)
    }

    @Test
    fun `entity to domain maps all fields losslessly with optional fields null`() {
        val entity = CustomerEntity(
            id = "cust-02",
            businessId = "biz-02",
            branchId = null,
            name = "Siti Rahma",
            phone = null,
            debtBalance = 0L,
            isActive = true
        )

        val domain = entity.toDomain()

        assertEquals("cust-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertNull(domain.branchId)
        assertEquals("Siti Rahma", domain.name)
        assertNull(domain.phone)
        assertEquals(0L, domain.debtBalance)
        assertTrue(domain.isActive)
    }

    @Test
    fun `full round trip preserves exact domain identity and state`() {
        val original = Customer(
            id = "cust-03",
            businessId = "biz-03",
            branchId = "branch-02",
            name = "Budi Santoso",
            phone = "085678901234",
            debtBalance = 500000L,
            isActive = false
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }
}
