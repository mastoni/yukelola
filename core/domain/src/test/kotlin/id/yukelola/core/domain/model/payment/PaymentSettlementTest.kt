package id.yukelola.core.domain.model.payment

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PaymentSettlementTest {

    @Test
    fun `payment represents valid settlement record across payment methods`() {
        val cashPayment = Payment(
            id = "pay-01",
            businessId = "biz-01",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.SALE,
            referenceId = "sale-01",
            paymentMethod = PaymentMethod.CASH,
            amount = 100000L,
            createdAt = 1000L
        )

        val qrisPayment = Payment(
            id = "pay-02",
            businessId = "biz-01",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.SALE,
            referenceId = "sale-02",
            paymentMethod = PaymentMethod.QRIS,
            amount = 150000L,
            createdAt = 1000L
        )

        val transferPayment = Payment(
            id = "pay-03",
            businessId = "biz-01",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.SALE,
            referenceId = "sale-03",
            paymentMethod = PaymentMethod.TRANSFER,
            amount = 200000L,
            createdAt = 1000L
        )

        val debtPayment = Payment(
            id = "pay-04",
            businessId = "biz-01",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.CUSTOMER_DEBT,
            referenceId = "debt-01",
            paymentMethod = PaymentMethod.DEBT,
            amount = 50000L,
            createdAt = 1000L
        )

        assertEquals(PaymentMethod.CASH, cashPayment.paymentMethod)
        assertEquals(PaymentMethod.QRIS, qrisPayment.paymentMethod)
        assertEquals(PaymentMethod.TRANSFER, transferPayment.paymentMethod)
        assertEquals(PaymentMethod.DEBT, debtPayment.paymentMethod)
    }

    @Test
    fun `payment rejects non-positive amount and blank IDs`() {
        assertThrows(IllegalArgumentException::class.java) {
            Payment(
                id = "pay-01",
                businessId = "biz-01",
                branchId = "branch-01",
                transactionType = PaymentTransactionType.SALE,
                referenceId = "sale-01",
                paymentMethod = PaymentMethod.CASH,
                amount = 0L,
                createdAt = 1000L
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            Payment(
                id = "pay-01",
                businessId = "biz-01",
                branchId = "branch-01",
                transactionType = PaymentTransactionType.SALE,
                referenceId = "sale-01",
                paymentMethod = PaymentMethod.CASH,
                amount = -1000L,
                createdAt = 1000L
            )
        }
    }
}
