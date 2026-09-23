package id.yukelola.core.domain.model.serviceorder

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.payment.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class DownPaymentRecordTest {

    private val attribution = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-01",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    private val validItem = ServiceOrderItem(
        id = "item-01",
        orderId = "so-01",
        productId = "prod-01",
        productName = "Service",
        unitPrice = 100000L,
        quantity = 1.0
    )

    @Test
    fun `down payment record validates positive amount and non-blank fields`() {
        val dp = DownPaymentRecord(
            id = "dp-01",
            businessId = "biz-01",
            branchId = "branch-01",
            orderId = "so-01",
            amount = 30000L,
            paymentMethod = PaymentMethod.CASH,
            notes = "Uang Muka 30%",
            createdAt = 1050L
        )

        assertEquals("dp-01", dp.id)
        assertEquals(30000L, dp.amount)
        assertEquals(PaymentMethod.CASH, dp.paymentMethod)
        assertEquals("Uang Muka 30%", dp.notes)

        assertThrows(IllegalArgumentException::class.java) {
            DownPaymentRecord(
                id = "dp-02",
                businessId = "biz-01",
                branchId = "branch-01",
                orderId = "so-01",
                amount = 0L,
                paymentMethod = PaymentMethod.CASH,
                createdAt = 1050L
            )
        }
    }

    @Test
    fun `multiple down payments aggregate deterministically without exceeding order total`() {
        val order = ServiceOrder(
            id = "so-01",
            orderNumber = "ORD-001",
            orderType = ServiceOrderType.PRINTING,
            attribution = attribution,
            items = listOf(validItem), // total = 100.000
            createdAt = 1000L
        )

        val dp1 = DownPaymentRecord(
            id = "dp-01",
            businessId = "biz-01",
            branchId = "branch-01",
            orderId = "so-01",
            amount = 30000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1010L
        )

        val dp2 = DownPaymentRecord(
            id = "dp-02",
            businessId = "biz-01",
            branchId = "branch-01",
            orderId = "so-01",
            amount = 40000L,
            paymentMethod = PaymentMethod.TRANSFER,
            createdAt = 1020L
        )

        val updatedOrder = order.withDownPayment(dp1).withDownPayment(dp2)

        assertEquals(70000L, updatedOrder.downPaymentAmount)
        assertEquals(30000L, updatedOrder.remainingBalance)
    }

    @Test
    fun `down payment exceeding total order amount is strictly rejected`() {
        val order = ServiceOrder(
            id = "so-01",
            orderNumber = "ORD-001",
            orderType = ServiceOrderType.PRINTING,
            attribution = attribution,
            items = listOf(validItem), // total = 100.000
            createdAt = 1000L
        )

        val excessiveDp = DownPaymentRecord(
            id = "dp-01",
            businessId = "biz-01",
            branchId = "branch-01",
            orderId = "so-01",
            amount = 120000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1010L
        )

        assertThrows(IllegalArgumentException::class.java) {
            order.withDownPayment(excessiveDp)
        }
    }
}
