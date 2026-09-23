package id.yukelola.core.domain.model.serviceorder

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import org.junit.Assert.assertThrows
import org.junit.Test

class ServiceOrderInvalidTransitionTest {

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
        unitPrice = 10000L,
        quantity = 1.0
    )

    @Test
    fun `completed service order cannot transition or be modified`() {
        val completedOrder = ServiceOrder(
            id = "so-01",
            orderNumber = "ORD-001",
            orderType = ServiceOrderType.LAUNDRY,
            attribution = attribution,
            items = listOf(validItem),
            createdAt = 1000L
        ).transitionTo(ServiceOrderStatus.IN_PROGRESS, 1100L)
            .transitionTo(ServiceOrderStatus.READY, 1200L)
            .complete(1300L)

        // Attempt to transition back to IN_PROGRESS
        assertThrows(IllegalArgumentException::class.java) {
            completedOrder.transitionTo(ServiceOrderStatus.IN_PROGRESS, 1400L)
        }

        // Attempt to cancel a completed order
        assertThrows(IllegalArgumentException::class.java) {
            completedOrder.cancel(1400L)
        }

        // Attempt to add new item to completed order
        assertThrows(IllegalArgumentException::class.java) {
            completedOrder.withItem(validItem.copy(id = "item-02"))
        }
    }

    @Test
    fun `cancelled service order cannot transition to any active state`() {
        val cancelledOrder = ServiceOrder(
            id = "so-01",
            orderNumber = "ORD-001",
            orderType = ServiceOrderType.LAUNDRY,
            attribution = attribution,
            items = listOf(validItem),
            createdAt = 1000L
        ).cancel(1100L)

        assertThrows(IllegalArgumentException::class.java) {
            cancelledOrder.transitionTo(ServiceOrderStatus.RECEIVED, 1200L)
        }

        assertThrows(IllegalArgumentException::class.java) {
            cancelledOrder.transitionTo(ServiceOrderStatus.IN_PROGRESS, 1200L)
        }

        assertThrows(IllegalArgumentException::class.java) {
            cancelledOrder.complete(1200L)
        }
    }

    @Test
    fun `illegal skipping of lifecycle states is rejected`() {
        val receivedOrder = ServiceOrder(
            id = "so-01",
            orderNumber = "ORD-001",
            orderType = ServiceOrderType.LAUNDRY,
            attribution = attribution,
            items = listOf(validItem),
            createdAt = 1000L
        )

        // Cannot skip directly from RECEIVED to READY
        assertThrows(IllegalArgumentException::class.java) {
            receivedOrder.transitionTo(ServiceOrderStatus.READY, 1100L)
        }

        // Cannot complete with zero items
        val emptyOrder = ServiceOrder(
            id = "so-02",
            orderNumber = "ORD-002",
            orderType = ServiceOrderType.LAUNDRY,
            attribution = attribution,
            items = emptyList(),
            createdAt = 1000L
        ).transitionTo(ServiceOrderStatus.IN_PROGRESS, 1100L)
            .transitionTo(ServiceOrderStatus.READY, 1200L)

        assertThrows(IllegalArgumentException::class.java) {
            emptyOrder.complete(1300L)
        }
    }
}
