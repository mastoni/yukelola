package id.yukelola.core.domain.model.serviceorder

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.payment.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CrossBranchServiceOrderIsolationTest {

    private val branchAAttribution = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-a",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    private val validItem = ServiceOrderItem(
        id = "item-01",
        orderId = "so-branch-a",
        productId = "prod-01",
        productName = "Service",
        unitPrice = 100000L,
        quantity = 1.0
    )

    @Test
    fun `service order branch and business are bound to attribution`() {
        val order = ServiceOrder(
            id = "so-branch-a",
            orderNumber = "ORD-A-01",
            orderType = ServiceOrderType.LAUNDRY,
            attribution = branchAAttribution,
            items = listOf(validItem),
            createdAt = 1000L
        )

        assertEquals("biz-01", order.businessId)
        assertEquals("branch-a", order.branchId)
    }

    @Test
    fun `service order rejects down payment from a different branch or business`() {
        val order = ServiceOrder(
            id = "so-branch-a",
            orderNumber = "ORD-A-01",
            orderType = ServiceOrderType.LAUNDRY,
            attribution = branchAAttribution,
            items = listOf(validItem),
            createdAt = 1000L
        )

        val branchBDp = DownPaymentRecord(
            id = "dp-b-01",
            businessId = "biz-01",
            branchId = "branch-b",
            orderId = "so-branch-a",
            amount = 30000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1050L
        )

        assertThrows(IllegalArgumentException::class.java) {
            order.withDownPayment(branchBDp)
        }

        val otherBizDp = DownPaymentRecord(
            id = "dp-other-01",
            businessId = "biz-other",
            branchId = "branch-a",
            orderId = "so-branch-a",
            amount = 30000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1050L
        )

        assertThrows(IllegalArgumentException::class.java) {
            order.withDownPayment(otherBizDp)
        }
    }
}
