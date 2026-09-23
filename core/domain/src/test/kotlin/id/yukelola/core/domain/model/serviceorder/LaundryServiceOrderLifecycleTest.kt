package id.yukelola.core.domain.model.serviceorder

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.payment.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LaundryServiceOrderLifecycleTest {

    private val attribution = TransactionAttribution(
        businessId = "biz-laundry-01",
        branchId = "branch-laundry-01",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    @Test
    fun `laundry service order executes full lifecycle from RECEIVED to COMPLETED`() {
        val order = ServiceOrder(
            id = "so-laundry-01",
            orderNumber = "LND-001",
            orderType = ServiceOrderType.LAUNDRY,
            attribution = attribution,
            customerId = "cust-01",
            notes = "Cuci Komplit Express, Lavender scent",
            createdAt = 1000L
        )

        assertEquals(ServiceOrderStatus.RECEIVED, order.status)
        assertTrue(order.isActive)
        assertFalse(order.isCompleted)

        // Add laundry line item: Cuci Komplit 5.0 Kg @ 8000/Kg
        val item = ServiceOrderItem(
            id = "so-item-01",
            orderId = "so-laundry-01",
            productId = "prod-cuci-komplit",
            productName = "Cuci Komplit Reguler",
            itemType = ServiceOrderItemType.SERVICE_LABOR,
            unit = "KG",
            unitPrice = 8000L,
            costPrice = 2000L,
            quantity = 5.0,
            discountAmount = 0L
        )

        val withItemOrder = order.withItem(item, 1050L)
        assertEquals(40000L, withItemOrder.totalAmount)

        // Customer pays partial down payment: 15.000
        val downPayment = DownPaymentRecord(
            id = "dp-01",
            businessId = "biz-laundry-01",
            branchId = "branch-laundry-01",
            orderId = "so-laundry-01",
            amount = 15000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1100L
        )

        val withDpOrder = withItemOrder.withDownPayment(downPayment)
        assertEquals(15000L, withDpOrder.downPaymentAmount)
        assertEquals(25000L, withDpOrder.remainingBalance)

        // Step 1: Work started -> IN_PROGRESS
        val inProgressOrder = withDpOrder.transitionTo(ServiceOrderStatus.IN_PROGRESS, 1200L)
        assertEquals(ServiceOrderStatus.IN_PROGRESS, inProgressOrder.status)

        // Step 2: Washing finished, packed -> READY
        val readyOrder = inProgressOrder.transitionTo(ServiceOrderStatus.READY, 1500L)
        assertEquals(ServiceOrderStatus.READY, readyOrder.status)

        // Step 3: Customer picks up clothes -> COMPLETED
        val completedOrder = readyOrder.complete(1800L)
        assertEquals(ServiceOrderStatus.COMPLETED, completedOrder.status)
        assertTrue(completedOrder.isCompleted)
        assertFalse(completedOrder.isActive)
        assertEquals(1800L, completedOrder.completedAt)
    }
}
