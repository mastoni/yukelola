package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.CashMutationEntity
import id.yukelola.core.domain.model.cash.CashMutation
import id.yukelola.core.domain.model.cash.CashMutationCategory
import id.yukelola.core.domain.model.cash.CashMutationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CashMutationMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly with optional fields populated`() {
        val domain = CashMutation(
            id = "mut-01",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            cashierSessionId = "sess-01",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.CUSTOMER_DEBT_PAYMENT,
            amount = 125000L,
            source = "DEBT_PAYMENT-01",
            referenceId = "debt-01",
            notes = "Pelunasan cicilan",
            createdAt = 1700000000000L
        )

        val entity = domain.toEntity()

        assertEquals("mut-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("reg-01", entity.registerId)
        assertEquals("sess-01", entity.cashierSessionId)
        assertEquals("INFLOW", entity.mutationType)
        assertEquals("CUSTOMER_DEBT_PAYMENT", entity.category)
        assertEquals(125000L, entity.amount)
        assertEquals("DEBT_PAYMENT-01", entity.source)
        assertEquals("debt-01", entity.referenceId)
        assertEquals("Pelunasan cicilan", entity.notes)
        assertEquals(1700000000000L, entity.createdAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly with optional fields null`() {
        val entity = CashMutationEntity(
            id = "mut-02",
            businessId = "biz-02",
            branchId = "branch-02",
            registerId = "reg-02",
            cashierSessionId = null,
            mutationType = "OUTFLOW",
            category = "EXPENSE",
            amount = 50000L,
            source = "EXPENSE-01",
            referenceId = null,
            notes = null,
            createdAt = 1710000000000L
        )

        val domain = entity.toDomain()

        assertEquals("mut-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals("reg-02", domain.registerId)
        assertNull(domain.cashierSessionId)
        assertEquals(CashMutationType.OUTFLOW, domain.mutationType)
        assertEquals(CashMutationCategory.EXPENSE, domain.category)
        assertEquals(50000L, domain.amount)
        assertEquals("EXPENSE-01", domain.source)
        assertNull(domain.referenceId)
        assertNull(domain.notes)
        assertEquals(1710000000000L, domain.createdAt)
    }

    @Test
    fun `full round trip preserves exact domain identity, enums, and Long precision`() {
        val original = CashMutation(
            id = "mut-03",
            businessId = "biz-03",
            branchId = "branch-03",
            registerId = "reg-03",
            cashierSessionId = "sess-03",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.OPENING_BALANCE,
            amount = 1000000L,
            source = "MODAL_AWAL",
            referenceId = "ref-03",
            notes = "Kas awal",
            createdAt = 1720000000000L
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }
}
