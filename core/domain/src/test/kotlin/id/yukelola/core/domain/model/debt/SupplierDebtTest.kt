package id.yukelola.core.domain.model.debt

import id.yukelola.core.domain.model.payment.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SupplierDebtTest {

    @Test
    fun `supplier debt creation starts in UNPAID status with full remaining amount`() {
        val debt = SupplierDebt.create(
            id = "sup-debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            supplierId = "sup-01",
            purchaseId = "purch-01",
            originalAmount = 2500000L,
            createdAt = 1000L
        )

        assertEquals(DebtStatus.UNPAID, debt.status)
        assertEquals(2500000L, debt.originalAmount)
        assertEquals(2500000L, debt.remainingAmount)
    }

    @Test
    fun `supplier debt supports partial and settled lifecycle`() {
        val debt = SupplierDebt.create(
            id = "sup-debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            supplierId = "sup-01",
            purchaseId = "purch-01",
            originalAmount = 2500000L,
            createdAt = 1000L
        )

        val partialPayment = DebtPayment(
            id = "pay-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.SUPPLIER,
            debtId = "sup-debt-01",
            amount = 1000000L,
            paymentMethod = PaymentMethod.TRANSFER,
            createdAt = 1100L
        )

        val partiallyPaidDebt = debt.applyPayment(partialPayment)
        assertEquals(DebtStatus.PARTIALLY_PAID, partiallyPaidDebt.status)
        assertEquals(1500000L, partiallyPaidDebt.remainingAmount)

        val finalPayment = DebtPayment(
            id = "pay-02",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.SUPPLIER,
            debtId = "sup-debt-01",
            amount = 1500000L,
            paymentMethod = PaymentMethod.TRANSFER,
            createdAt = 1200L
        )

        val settledDebt = partiallyPaidDebt.applyPayment(finalPayment)
        assertEquals(DebtStatus.SETTLED, settledDebt.status)
        assertEquals(0L, settledDebt.remainingAmount)
    }

    @Test
    fun `supplier debt rejects overpayment`() {
        val debt = SupplierDebt.create(
            id = "sup-debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            supplierId = "sup-01",
            purchaseId = "purch-01",
            originalAmount = 500000L,
            createdAt = 1000L
        )

        val overpayment = DebtPayment(
            id = "pay-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.SUPPLIER,
            debtId = "sup-debt-01",
            amount = 600000L,
            paymentMethod = PaymentMethod.TRANSFER,
            createdAt = 1100L
        )

        assertThrows(IllegalArgumentException::class.java) {
            debt.applyPayment(overpayment)
        }
    }
}
