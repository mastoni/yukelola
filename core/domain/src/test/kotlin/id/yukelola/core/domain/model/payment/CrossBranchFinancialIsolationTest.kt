package id.yukelola.core.domain.model.payment

import id.yukelola.core.domain.model.cash.CashMutation
import id.yukelola.core.domain.model.cash.CashMutationCategory
import id.yukelola.core.domain.model.cash.CashMutationType
import id.yukelola.core.domain.model.cash.CashRegister
import id.yukelola.core.domain.model.debt.CustomerDebt
import id.yukelola.core.domain.model.debt.DebtPayment
import id.yukelola.core.domain.model.debt.DebtReferenceType
import id.yukelola.core.domain.model.debt.DebtType
import id.yukelola.core.domain.model.debt.SupplierDebt
import org.junit.Assert.assertThrows
import org.junit.Test

class CrossBranchFinancialIsolationTest {

    @Test
    fun `cash register rejects cash mutation from different branch or business`() {
        val branchARegister = CashRegister(
            id = "reg-branch-a",
            businessId = "biz-01",
            branchId = "branch-a",
            name = "Branch A Drawer",
            currentBalance = 1000000L,
            updatedAt = 1000L
        )

        val branchBMutation = CashMutation(
            id = "mut-01",
            businessId = "biz-01",
            branchId = "branch-b",
            registerId = "reg-branch-a",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.SALE,
            amount = 50000L,
            source = "SALE-B-01",
            createdAt = 1100L
        )

        assertThrows(IllegalArgumentException::class.java) {
            branchARegister.applyMutation(branchBMutation)
        }

        val otherBusinessMutation = CashMutation(
            id = "mut-02",
            businessId = "biz-other",
            branchId = "branch-a",
            registerId = "reg-branch-a",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.SALE,
            amount = 50000L,
            source = "SALE-OTHER-01",
            createdAt = 1100L
        )

        assertThrows(IllegalArgumentException::class.java) {
            branchARegister.applyMutation(otherBusinessMutation)
        }
    }

    @Test
    fun `customer debt rejects payment from different branch or business`() {
        val branchADebt = CustomerDebt.create(
            id = "debt-a-01",
            businessId = "biz-01",
            branchId = "branch-a",
            customerId = "cust-01",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-a-01",
            originalAmount = 200000L,
            createdAt = 1000L
        )

        val branchBPayment = DebtPayment(
            id = "pay-b-01",
            businessId = "biz-01",
            branchId = "branch-b",
            debtType = DebtType.CUSTOMER,
            debtId = "debt-a-01",
            amount = 50000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1100L
        )

        assertThrows(IllegalArgumentException::class.java) {
            branchADebt.applyPayment(branchBPayment)
        }
    }

    @Test
    fun `supplier debt rejects payment from different branch or business`() {
        val branchADebt = SupplierDebt.create(
            id = "sup-debt-a-01",
            businessId = "biz-01",
            branchId = "branch-a",
            supplierId = "sup-01",
            purchaseId = "purch-a-01",
            originalAmount = 500000L,
            createdAt = 1000L
        )

        val crossBusinessPayment = DebtPayment(
            id = "pay-other-01",
            businessId = "biz-other",
            branchId = "branch-a",
            debtType = DebtType.SUPPLIER,
            debtId = "sup-debt-a-01",
            amount = 100000L,
            paymentMethod = PaymentMethod.TRANSFER,
            createdAt = 1100L
        )

        assertThrows(IllegalArgumentException::class.java) {
            branchADebt.applyPayment(crossBusinessPayment)
        }
    }
}
