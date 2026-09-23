package id.yukelola.core.domain.model.debt

import id.yukelola.core.domain.model.payment.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CustomerDebtTest {

    @Test
    fun `customer debt creation starts in UNPAID status with full remaining amount`() {
        val debt = CustomerDebt.create(
            id = "debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            customerId = "cust-01",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-101",
            originalAmount = 100000L,
            createdAt = 1000L
        )

        assertEquals(DebtStatus.UNPAID, debt.status)
        assertEquals(100000L, debt.originalAmount)
        assertEquals(100000L, debt.remainingAmount)
    }

    @Test
    fun `partial debt payment transitions status to PARTIALLY_PAID`() {
        val debt = CustomerDebt.create(
            id = "debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            customerId = "cust-01",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-101",
            originalAmount = 100000L,
            createdAt = 1000L
        )

        val payment = DebtPayment(
            id = "pay-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = "debt-01",
            amount = 40000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1100L
        )

        val updatedDebt = debt.applyPayment(payment)

        assertEquals(DebtStatus.PARTIALLY_PAID, updatedDebt.status)
        assertEquals(100000L, updatedDebt.originalAmount)
        assertEquals(60000L, updatedDebt.remainingAmount)
    }

    @Test
    fun `full debt payment transitions status to SETTLED`() {
        val debt = CustomerDebt.create(
            id = "debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            customerId = "cust-01",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-101",
            originalAmount = 100000L,
            createdAt = 1000L
        )

        val payment = DebtPayment(
            id = "pay-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = "debt-01",
            amount = 100000L,
            paymentMethod = PaymentMethod.TRANSFER,
            createdAt = 1100L
        )

        val updatedDebt = debt.applyPayment(payment)

        assertEquals(DebtStatus.SETTLED, updatedDebt.status)
        assertEquals(0L, updatedDebt.remainingAmount)
    }

    @Test
    fun `customer debt rejects overpayment`() {
        val debt = CustomerDebt.create(
            id = "debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            customerId = "cust-01",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-101",
            originalAmount = 100000L,
            createdAt = 1000L
        )

        val excessivePayment = DebtPayment(
            id = "pay-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = "debt-01",
            amount = 120000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1100L
        )

        assertThrows(IllegalArgumentException::class.java) {
            debt.applyPayment(excessivePayment)
        }
    }

    @Test
    fun `settled debt rejects further payments`() {
        val debt = CustomerDebt.create(
            id = "debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            customerId = "cust-01",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-101",
            originalAmount = 50000L,
            createdAt = 1000L
        )

        val fullPayment = DebtPayment(
            id = "pay-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = "debt-01",
            amount = 50000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1100L
        )

        val settledDebt = debt.applyPayment(fullPayment)

        val additionalPayment = DebtPayment(
            id = "pay-02",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = "debt-01",
            amount = 10000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1200L
        )

        assertThrows(IllegalArgumentException::class.java) {
            settledDebt.applyPayment(additionalPayment)
        }
    }
}
