package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.DigitalDepositAccountEntity
import id.yukelola.core.domain.model.digital.DigitalDepositAccount
import org.junit.Assert.assertEquals
import org.junit.Test

class DigitalDepositAccountMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly`() {
        val domain = DigitalDepositAccount(
            id = "dep-01",
            businessId = "biz-01",
            branchId = "branch-01",
            currentBalance = 1500000L,
            updatedAt = 1700000000000L
        )

        val entity = domain.toEntity()

        assertEquals("dep-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals(1500000L, entity.currentBalance)
        assertEquals(1700000000000L, entity.updatedAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly`() {
        val entity = DigitalDepositAccountEntity(
            id = "dep-02",
            businessId = "biz-02",
            branchId = "branch-02",
            currentBalance = 0L,
            updatedAt = 1710000000000L
        )

        val domain = entity.toDomain()

        assertEquals("dep-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals(0L, domain.currentBalance)
        assertEquals(1710000000000L, domain.updatedAt)
    }

    @Test
    fun `full round trip preserves exact domain identity and Long monetary precision`() {
        val original = DigitalDepositAccount(
            id = "dep-03",
            businessId = "biz-03",
            branchId = "branch-03",
            currentBalance = 25000000L,
            updatedAt = 1720000000000L
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }
}
