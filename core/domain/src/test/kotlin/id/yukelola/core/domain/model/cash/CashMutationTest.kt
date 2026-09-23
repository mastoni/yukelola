package id.yukelola.core.domain.model.cash

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Test

class CashMutationTest {

    @Test
    fun `cash mutation instantiates with valid parameters`() {
        val mutation = CashMutation(
            id = "mut-01",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            cashierSessionId = "session-01",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.CUSTOMER_DEBT_PAYMENT,
            amount = 75000L,
            source = "DEBT_PAYMENT-01",
            referenceId = "debt-01",
            notes = "Cashbon paid in full",
            createdAt = 1500L
        )

        assertEquals("mut-01", mutation.id)
        assertEquals("biz-01", mutation.businessId)
        assertEquals("branch-01", mutation.branchId)
        assertEquals("reg-01", mutation.registerId)
        assertEquals("session-01", mutation.cashierSessionId)
        assertEquals(CashMutationType.INFLOW, mutation.mutationType)
        assertEquals(CashMutationCategory.CUSTOMER_DEBT_PAYMENT, mutation.category)
        assertEquals(75000L, mutation.amount)
        assertEquals("DEBT_PAYMENT-01", mutation.source)
        assertEquals("debt-01", mutation.referenceId)
        assertEquals("Cashbon paid in full", mutation.notes)
        assertEquals(1500L, mutation.createdAt)
    }

    @Test
    fun `cash mutation rejects non-positive amount and blank fields`() {
        assertThrows(IllegalArgumentException::class.java) {
            CashMutation(
                id = "mut-01",
                businessId = "biz-01",
                branchId = "branch-01",
                registerId = "reg-01",
                mutationType = CashMutationType.INFLOW,
                category = CashMutationCategory.SALE,
                amount = 0L,
                source = "SALE-01",
                createdAt = 1000L
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            CashMutation(
                id = "mut-01",
                businessId = "biz-01",
                branchId = "branch-01",
                registerId = "reg-01",
                mutationType = CashMutationType.INFLOW,
                category = CashMutationCategory.SALE,
                amount = -5000L,
                source = "SALE-01",
                createdAt = 1000L
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            CashMutation(
                id = "mut-01",
                businessId = "biz-01",
                branchId = "branch-01",
                registerId = "reg-01",
                mutationType = CashMutationType.INFLOW,
                category = CashMutationCategory.SALE,
                amount = 5000L,
                source = "   ",
                createdAt = 1000L
            )
        }
    }
}
