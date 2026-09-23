package id.yukelola.core.domain.model.cash

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CashRegisterTest {

    @Test
    fun `cash register initializes with valid parameters`() {
        val register = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Main Cash Drawer",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        assertEquals("reg-01", register.id)
        assertEquals("biz-01", register.businessId)
        assertEquals("branch-01", register.branchId)
        assertEquals(500000L, register.currentBalance)
    }

    @Test
    fun `cash register rejects negative balance or blank fields`() {
        assertThrows(IllegalArgumentException::class.java) {
            CashRegister(
                id = "reg-01",
                businessId = "biz-01",
                branchId = "branch-01",
                name = "Main",
                currentBalance = -100L,
                updatedAt = 1000L
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            CashRegister(
                id = "",
                businessId = "biz-01",
                branchId = "branch-01",
                name = "Main",
                currentBalance = 0L,
                updatedAt = 1000L
            )
        }
    }

    @Test
    fun `cash register increases balance on inflow mutation deterministically`() {
        val register = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Main Cash Drawer",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        val inflowMutation = CashMutation(
            id = "mut-01",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.SALE,
            amount = 150000L,
            source = "SALE-101",
            createdAt = 1100L
        )

        val updatedRegister = register.applyMutation(inflowMutation)

        assertEquals(650000L, updatedRegister.currentBalance)
        assertEquals(1100L, updatedRegister.updatedAt)
    }

    @Test
    fun `cash register decreases balance on outflow mutation`() {
        val register = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Main Cash Drawer",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        val outflowMutation = CashMutation(
            id = "mut-02",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.OUTFLOW,
            category = CashMutationCategory.EXPENSE,
            amount = 100000L,
            source = "EXPENSE-01",
            notes = "Electricity token",
            createdAt = 1200L
        )

        val updatedRegister = register.applyMutation(outflowMutation)

        assertEquals(400000L, updatedRegister.currentBalance)
        assertEquals(1200L, updatedRegister.updatedAt)
    }

    @Test
    fun `cash register rejects outflow exceeding current balance`() {
        val register = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Main Cash Drawer",
            currentBalance = 50000L,
            updatedAt = 1000L
        )

        val excessiveOutflow = CashMutation(
            id = "mut-03",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.OUTFLOW,
            category = CashMutationCategory.EXPENSE,
            amount = 60000L,
            source = "EXPENSE-02",
            createdAt = 1200L
        )

        assertThrows(IllegalArgumentException::class.java) {
            register.applyMutation(excessiveOutflow)
        }
    }
}
