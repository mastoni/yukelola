package id.yukelola.core.domain.model.debt

import id.yukelola.core.domain.model.payment.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class DebtPaymentTest {

    @Test
    fun `debt payment instantiates with valid parameters`() {
        val payment = DebtPayment(
            id = "dp-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = "debt-01",
            amount = 50000L,
            paymentMethod = PaymentMethod.CASH,
            notes = "Kasbon cicilan 1",
            createdAt = 1000L
        )

        assertEquals("dp-01", payment.id)
        assertEquals(DebtType.CUSTOMER, payment.debtType)
        assertEquals(50000L, payment.amount)
        assertEquals(PaymentMethod.CASH, payment.paymentMethod)
    }

    @Test
    fun `debt payment rejects invalid amount or blank attributes`() {
        assertThrows(IllegalArgumentException::class.java) {
            DebtPayment(
                id = "dp-01",
                businessId = "biz-01",
                branchId = "branch-01",
                debtType = DebtType.CUSTOMER,
                debtId = "debt-01",
                amount = 0L,
                paymentMethod = PaymentMethod.CASH,
                createdAt = 1000L
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            DebtPayment(
                id = "",
                businessId = "biz-01",
                branchId = "branch-01",
                debtType = DebtType.CUSTOMER,
                debtId = "debt-01",
                amount = 10000L,
                paymentMethod = PaymentMethod.CASH,
                createdAt = 1000L
            )
        }
    }
}
