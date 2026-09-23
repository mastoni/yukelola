package id.yukelola.core.domain.model.serviceorder

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ServiceOrderItemTotalTest {

    private val attribution = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-01",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    @Test
    fun `service order item computes subtotal with integer money deterministically`() {
        val item = ServiceOrderItem(
            id = "item-01",
            orderId = "so-01",
            productId = "prod-01",
            productName = "Cuci Kering",
            itemType = ServiceOrderItemType.SERVICE_LABOR,
            unit = "KG",
            unitPrice = 7000L,
            costPrice = 1500L,
            quantity = 3.5,
            discountAmount = 2000L
        )

        // 3.5 * 7000 = 24500 - 2000 = 22500
        assertEquals(22500L, item.subtotal)
    }

    @Test
    fun `service order calculates total with order-level discount and tax`() {
        val item1 = ServiceOrderItem(
            id = "item-01",
            orderId = "so-01",
            productId = "prod-01",
            productName = "Item 1",
            unitPrice = 50000L,
            quantity = 2.0
        ) // 100.000

        val item2 = ServiceOrderItem(
            id = "item-02",
            orderId = "so-01",
            productId = "prod-02",
            productName = "Item 2",
            unitPrice = 30000L,
            quantity = 1.0
        ) // 30.000

        val order = ServiceOrder(
            id = "so-01",
            orderNumber = "ORD-001",
            orderType = ServiceOrderType.WORKSHOP,
            attribution = attribution,
            items = listOf(item1, item2),
            discountAmount = 10000L,
            taxAmount = 5000L,
            createdAt = 1000L
        )

        // subtotal = 130.000, discount = 10.000, tax = 5.000 -> total = 125.000
        assertEquals(130000L, order.subtotal)
        assertEquals(125000L, order.totalAmount)
    }

    @Test
    fun `item subtotal floors at zero if discount exceeds gross price`() {
        val item = ServiceOrderItem(
            id = "item-01",
            orderId = "so-01",
            productId = "prod-01",
            productName = "Promo Service",
            unitPrice = 15000L,
            quantity = 1.0,
            discountAmount = 20000L
        )

        assertEquals(0L, item.subtotal)
    }

    @Test
    fun `historical price snapshot remains invariant if catalog price changes later`() {
        var catalogServicePrice = 50000L

        val historicalItem = ServiceOrderItem(
            id = "item-01",
            orderId = "so-01",
            productId = "prod-01",
            productName = "Jasa Servis Motor",
            unitPrice = catalogServicePrice,
            quantity = 1.0
        )

        val completedOrder = ServiceOrder(
            id = "so-01",
            orderNumber = "ORD-001",
            orderType = ServiceOrderType.WORKSHOP,
            attribution = attribution,
            items = listOf(historicalItem),
            createdAt = 1000L
        ).transitionTo(ServiceOrderStatus.IN_PROGRESS, 1100L)
            .transitionTo(ServiceOrderStatus.READY, 1200L)
            .complete(1300L)

        // Tomorrow, the shop raises prices in the catalog
        catalogServicePrice = 75000L

        // Historical order still retains original price of 50.000
        assertEquals(50000L, completedOrder.items.first().unitPrice)
        assertEquals(50000L, completedOrder.totalAmount)
    }
}
