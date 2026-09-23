package id.yukelola.core.domain.model.serviceorder

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.sale.Sale
import id.yukelola.core.domain.model.sale.SaleItem
import id.yukelola.core.domain.model.sale.SaleStatus
import id.yukelola.core.domain.model.sale.TransactionMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhotocopyServiceOrderTest {

    private val attribution = TransactionAttribution(
        businessId = "biz-fc-01",
        branchId = "branch-fc-01",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    @Test
    fun `bulk volume photocopy and book binding routes through unified ServiceOrder`() {
        val bulkOrder = ServiceOrder(
            id = "so-fc-01",
            orderNumber = "FC-001",
            orderType = ServiceOrderType.PHOTOCOPY,
            attribution = attribution,
            customerId = "cust-04",
            contextMetadataJson = """{"masterPages":150,"copies":5,"binding":"Hardcover Skripsi"}""",
            notes = "Jilid Hardcover Skripsi 5 Ekslempar",
            createdAt = 1000L
        )

        assertEquals(TransactionMode.SERVICE_ORDER_TRANSACTION, bulkOrder.transactionMode)

        val copyItem = ServiceOrderItem(
            id = "item-fc-01",
            orderId = "so-fc-01",
            productId = "prod-copy-a4",
            productName = "Fotocopy A4 70gr",
            itemType = ServiceOrderItemType.SERVICE_LABOR,
            unit = "LEMBAR",
            unitPrice = 300L,
            costPrice = 120L,
            quantity = 750.0 // 150 pages * 5 copies
        )

        val bindingItem = ServiceOrderItem(
            id = "item-fc-02",
            orderId = "so-fc-01",
            productId = "prod-jilid-hardcover",
            productName = "Jilid Hardcover Skripsi Emboss Emas",
            itemType = ServiceOrderItemType.SERVICE_LABOR,
            unit = "BUKU",
            unitPrice = 35000L,
            costPrice = 10000L,
            quantity = 5.0
        )

        val withItemsOrder = bulkOrder
            .withItem(copyItem, 1020L)
            .withItem(bindingItem, 1020L)

        // 750 * 300 = 225.000 + 5 * 35.000 = 175.000 -> Total = 400.000
        assertEquals(400000L, withItemsOrder.totalAmount)

        val inProgressOrder = withItemsOrder.transitionTo(ServiceOrderStatus.IN_PROGRESS, 1100L)
        val readyOrder = inProgressOrder.transitionTo(ServiceOrderStatus.READY, 1500L)
        val completedOrder = readyOrder.complete(1600L)

        assertTrue(completedOrder.isCompleted)
    }

    @Test
    fun `walk-in per-page photocopy routes through instant retail Sale transaction`() {
        val walkInSale = Sale(
            id = "sale-fc-01",
            saleNumber = "STRUK-FC-001",
            transactionMode = TransactionMode.RETAIL_TRANSACTION,
            attribution = attribution,
            createdAt = 1000L
        )

        val item = SaleItem(
            id = "sale-item-01",
            saleId = "sale-fc-01",
            productId = "prod-copy-a4",
            productName = "Fotocopy A4 Walk-in",
            unit = "LEMBAR",
            unitPrice = 300L,
            costPrice = 120L,
            quantity = 10.0,
            discountAmount = 0L,
            subtotal = 3000L
        )

        val completedSale = walkInSale
            .withItem(item)
            .complete(paidAmount = 3000L, completedAt = 1010L)

        assertEquals(TransactionMode.RETAIL_TRANSACTION, completedSale.transactionMode)
        assertEquals(3000L, completedSale.totalAmount)
        assertEquals(SaleStatus.COMPLETED, completedSale.status)
    }
}
