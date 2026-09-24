package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.BusinessEntity
import id.yukelola.core.domain.model.business.Business
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BusinessMapperTest {

    @Test
    fun `domain to entity preserves all canonical fields`() {
        val domain = Business(
            id = "biz-101",
            legalName = "Yukelola Store",
            ownerUserId = "user-001",
            createdAt = 1700000000000L,
            isActive = true
        )

        val entity = domain.toEntity()

        assertEquals("biz-101", entity.id)
        assertEquals("Yukelola Store", entity.legalName)
        assertEquals("user-001", entity.ownerUserId)
        assertEquals(1700000000000L, entity.createdAt)
        assertTrue(entity.isActive)
    }

    @Test
    fun `entity to domain preserves all canonical fields`() {
        val entity = BusinessEntity(
            id = "biz-102",
            legalName = "Yukelola Mart",
            ownerUserId = "user-002",
            createdAt = 1710000000000L,
            isActive = false
        )

        val domain = entity.toDomain()

        assertEquals("biz-102", domain.id)
        assertEquals("Yukelola Mart", domain.legalName)
        assertEquals("user-002", domain.ownerUserId)
        assertEquals(1710000000000L, domain.createdAt)
        assertFalse(domain.isActive)
    }

    @Test
    fun `roundtrip domain to entity to domain produces identical aggregate`() {
        val original = Business(
            id = "biz-103",
            legalName = "Yukelola Kiosk",
            ownerUserId = "user-003",
            createdAt = 1720000000000L,
            isActive = true
        )

        val roundtrip = original.toEntity().toDomain()

        assertEquals(original, roundtrip)
    }
}
