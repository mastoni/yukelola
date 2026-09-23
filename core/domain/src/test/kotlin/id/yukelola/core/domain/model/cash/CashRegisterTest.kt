package id.yukelola.core.domain.model.cash

import id.yukelola.core.domain.model.catalog.BranchProductOverride
import id.yukelola.core.domain.model.debt.CustomerDebt
import id.yukelola.core.domain.model.debt.DebtReferenceType
import id.yukelola.core.domain.model.debt.DebtStatus
import id.yukelola.core.domain.model.debt.SupplierDebt
import id.yukelola.core.domain.model.digital.DigitalDepositAccount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class CashRegisterTest {

    @Test
    fun `valid CashRegister creation with standard parameters`() {
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
        assertEquals("Main Cash Drawer", register.name)
        assertEquals(500000L, register.currentBalance)
        assertEquals(1000L, register.updatedAt)
    }

    @Test
    fun `blank register id, businessId, branchId, or name rejected`() {
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

        assertThrows(IllegalArgumentException::class.java) {
            CashRegister(
                id = "reg-01",
                businessId = "   ",
                branchId = "branch-01",
                name = "Main",
                currentBalance = 0L,
                updatedAt = 1000L
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            CashRegister(
                id = "reg-01",
                businessId = "biz-01",
                branchId = "",
                name = "Main",
                currentBalance = 0L,
                updatedAt = 1000L
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            CashRegister(
                id = "reg-01",
                businessId = "biz-01",
                branchId = "branch-01",
                name = "   ",
                currentBalance = 0L,
                updatedAt = 1000L
            )
        }
    }

    @Test
    fun `invalid negative current balance rejected`() {
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
    }

    @Test
    fun `INFLOW mutation increases balance deterministically`() {
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
    fun `OUTFLOW mutation decreases balance and rejects overdraft`() {
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
            notes = "Listrik dan air",
            createdAt = 1200L
        )

        val updatedRegister = register.applyMutation(outflowMutation)

        assertEquals(400000L, updatedRegister.currentBalance)
        assertEquals(1200L, updatedRegister.updatedAt)

        val excessiveOutflow = CashMutation(
            id = "mut-03",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.OUTFLOW,
            category = CashMutationCategory.EXPENSE,
            amount = 450000L,
            source = "EXPENSE-02",
            createdAt = 1300L
        )

        assertThrows(IllegalArgumentException::class.java) {
            updatedRegister.applyMutation(excessiveOutflow)
        }
    }

    @Test
    fun `business, branch, and register isolation is enforced on mutation application`() {
        val register = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Main Cash Drawer",
            currentBalance = 100000L,
            updatedAt = 1000L
        )

        val wrongBusinessMutation = CashMutation(
            id = "mut-wrong-biz",
            businessId = "biz-OTHER",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.MANUAL_ADJUSTMENT,
            amount = 50000L,
            source = "SETORAN",
            createdAt = 1100L
        )

        assertThrows(IllegalArgumentException::class.java) {
            register.applyMutation(wrongBusinessMutation)
        }

        val wrongBranchMutation = CashMutation(
            id = "mut-wrong-branch",
            businessId = "biz-01",
            branchId = "branch-OTHER",
            registerId = "reg-01",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.MANUAL_ADJUSTMENT,
            amount = 50000L,
            source = "SETORAN",
            createdAt = 1100L
        )

        assertThrows(IllegalArgumentException::class.java) {
            register.applyMutation(wrongBranchMutation)
        }

        val wrongRegisterMutation = CashMutation(
            id = "mut-wrong-reg",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-OTHER",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.MANUAL_ADJUSTMENT,
            amount = 50000L,
            source = "SETORAN",
            createdAt = 1100L
        )

        assertThrows(IllegalArgumentException::class.java) {
            register.applyMutation(wrongRegisterMutation)
        }
    }

    @Test
    fun `balance auditability invariant matches sum of mutations from opening balance`() {
        val openingBalance = 200000L
        val register0 = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Kasir 1",
            currentBalance = openingBalance,
            updatedAt = 1000L
        )

        val mut1 = CashMutation(
            id = "m-01",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.SALE,
            amount = 150000L,
            source = "SALE-01",
            createdAt = 1100L
        )

        val mut2 = CashMutation(
            id = "m-02",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.CUSTOMER_DEBT_PAYMENT,
            amount = 50000L,
            source = "DEBT-01",
            createdAt = 1200L
        )

        val mut3 = CashMutation(
            id = "m-03",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.OUTFLOW,
            category = CashMutationCategory.PURCHASE,
            amount = 80000L,
            source = "PURCHASE-01",
            createdAt = 1300L
        )

        val mut4 = CashMutation(
            id = "m-04",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.OUTFLOW,
            category = CashMutationCategory.EXPENSE,
            amount = 20000L,
            source = "EXPENSE-01",
            createdAt = 1400L
        )

        val mutations = listOf(mut1, mut2, mut3, mut4)

        var current = register0
        for (m in mutations) {
            current = current.applyMutation(m)
        }

        val totalInflows = mutations.filter { it.mutationType == CashMutationType.INFLOW }.sumOf { it.amount }
        val totalOutflows = mutations.filter { it.mutationType == CashMutationType.OUTFLOW }.sumOf { it.amount }

        val expectedBalance = openingBalance + totalInflows - totalOutflows
        assertEquals(expectedBalance, current.currentBalance)
        assertEquals(300000L, current.currentBalance)
    }

    @Test
    fun `CashRegister represents physical cash only and is distinct from DigitalDeposit, Debt, and Stock`() {
        val register = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Kasir 1",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        val deposit = DigitalDepositAccount(
            id = "dep-01",
            businessId = "biz-01",
            branchId = "branch-01",
            currentBalance = 1000000L,
            updatedAt = 1000L
        )

        val customerDebt = CustomerDebt.create(
            id = "debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            customerId = "cust-01",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-01",
            originalAmount = 150000L,
            createdAt = 1000L
        )

        val supplierDebt = SupplierDebt.create(
            id = "sup-debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            supplierId = "sup-01",
            purchaseId = "purch-01",
            originalAmount = 300000L,
            createdAt = 1000L
        )

        val stock = BranchProductOverride(
            branchId = "branch-01",
            productId = "prod-01",
            stock = 50.0
        )

        // Mutating cash drawer does not mutate digital deposit, customer debt, supplier debt, or physical stock
        val mutation = CashMutation(
            id = "mut-01",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.MANUAL_ADJUSTMENT,
            amount = 100000L,
            source = "MODAL_AWAL",
            createdAt = 1100L
        )

        val updatedRegister = register.applyMutation(mutation)

        assertEquals(600000L, updatedRegister.currentBalance)
        assertEquals(1000000L, deposit.currentBalance)
        assertEquals(150000L, customerDebt.remainingAmount)
        assertEquals(300000L, supplierDebt.remainingAmount)
        assertEquals(50.0, stock.stock, 0.001)
    }

    @Test
    fun `CashMutationType enum contains strictly INFLOW and OUTFLOW`() {
        val expected = setOf(CashMutationType.INFLOW, CashMutationType.OUTFLOW)
        assertEquals(2, CashMutationType.values().size)
        assertEquals(expected, CashMutationType.values().toSet())
    }
}
