package id.yukelola.core.domain.model.payment

import org.junit.Assert.assertEquals
import org.junit.Test

class PaymentDiscountModelTest {

    @Test
    fun `Payment initializes with positive amount and valid references`() {
        val payment = Payment(
            id = "pay-001",
            businessId = "biz-123",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.SALE,
            referenceId = "sale-1001",
            paymentMethod = PaymentMethod.CASH,
            amount = 50000L,
            createdAt = 1711234567000L
        )

        assertEquals(50000L, payment.amount)
        assertEquals(PaymentMethod.CASH, payment.paymentMethod)
        assertEquals(PaymentTransactionType.SALE, payment.transactionType)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Payment with zero or negative amount throws exception`() {
        Payment(
            id = "pay-001",
            businessId = "biz-123",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.SALE,
            referenceId = "sale-1001",
            paymentMethod = PaymentMethod.CASH,
            amount = 0L,
            createdAt = 1711234567000L
        )
    }

    @Test
    fun `Discount calculates fixed amount deduction correctly`() {
        val discount = Discount(type = DiscountType.FIXED_AMOUNT, value = 5000L)
        val gross = 20000L
        assertEquals(5000L, discount.calculateDeduction(gross))
    }

    @Test
    fun `Discount calculates fixed amount deduction capped at gross`() {
        val discount = Discount(type = DiscountType.FIXED_AMOUNT, value = 25000L)
        val gross = 20000L
        assertEquals(20000L, discount.calculateDeduction(gross))
    }

    @Test
    fun `Discount calculates percentage deduction correctly`() {
        val discount = Discount(type = DiscountType.PERCENTAGE, value = 10L)
        val gross = 150000L
        assertEquals(15000L, discount.calculateDeduction(gross))
    }
}
