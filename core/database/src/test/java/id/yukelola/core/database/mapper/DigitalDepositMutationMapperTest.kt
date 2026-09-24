package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.DigitalDepositMutationEntity
import id.yukelola.core.domain.model.digital.DigitalDepositMutation
import id.yukelola.core.domain.model.digital.DigitalDepositMutationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DigitalDepositMutationMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly with optional fields populated`() {
        val domain = DigitalDepositMutation(
            id = "mut-01",
            businessId = "biz-01",
            branchId = "branch-01",
            accountId = "dep-01",
            mutationType = DigitalDepositMutationType.TOP_UP,
            amount = 500000L,
            balanceBefore = 1000000L,
            balanceAfter = 1500000L,
            referenceId = "TOPUP-123",
            notes = "Topup deposit kasir",
            createdAt = 1700000000000L
        )

        val entity = domain.toEntity()

        assertEquals("mut-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("dep-01", entity.accountId)
        assertEquals("TOP_UP", entity.mutationType)
        assertEquals(500000L, entity.amount)
        assertEquals(1000000L, entity.balanceBefore)
        assertEquals(1500000L, entity.balanceAfter)
        assertEquals("TOPUP-123", entity.referenceId)
        assertEquals("Topup deposit kasir", entity.notes)
        assertEquals(1700000000000L, entity.createdAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly with optional fields null`() {
        val entity = DigitalDepositMutationEntity(
            id = "mut-02",
            businessId = "biz-02",
            branchId = "branch-02",
            accountId = "dep-02",
            mutationType = "DIGITAL_SALE",
            amount = 49000L,
            balanceBefore = 1500000L,
            balanceAfter = 1451000L,
            referenceId = null,
            notes = null,
            createdAt = 1710000000000L
        )

        val domain = entity.toDomain()

        assertEquals("mut-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals("dep-02", domain.accountId)
        assertEquals(DigitalDepositMutationType.DIGITAL_SALE, domain.mutationType)
        assertEquals(49000L, domain.amount)
        assertEquals(1500000L, domain.balanceBefore)
        assertEquals(1451000L, domain.balanceAfter)
        assertNull(domain.referenceId)
        assertNull(domain.notes)
        assertEquals(1710000000000L, domain.createdAt)
    }

    @Test
    fun `full round trip preserves exact domain identity, enums, and Long precision`() {
        val original = DigitalDepositMutation(
            id = "mut-03",
            businessId = "biz-03",
            branchId = "branch-03",
            accountId = "dep-03",
            mutationType = DigitalDepositMutationType.REVERSAL,
            amount = 49000L,
            balanceBefore = 1451000L,
            balanceAfter = 1500000L,
            referenceId = "DT-001",
            notes = "Reversal for failed dispatch",
            createdAt = 1720000000000L
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }
}
