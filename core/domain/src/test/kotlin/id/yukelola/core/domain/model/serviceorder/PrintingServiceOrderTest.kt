package id.yukelola.core.domain.model.serviceorder

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.payment.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PrintingServiceOrderTest {

    private val attribution = TransactionAttribution(
        businessId = "biz-print-01",
        branchId = "branch-print-01",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    @Test
    fun `printing service order manages job specifications down payments and fulfillment`() {
        val order = ServiceOrder(
            id = "so-print-01",
            orderNumber = "PRT-001",
            orderType = ServiceOrderType.PRINTING,
            attribution = attribution,
            customerId = "cust-03",
            contextMetadataJson = """{"fileName":"banner_hut_ri.pdf","dimensions":"3x1m","finishing":"Mata Ayam 4 Sudut"}""",
            notes = "Bahan Flexi Korea 340gr",
            createdAt = 1000L
        )

        val printItem = ServiceOrderItem(
            id = "item-print-01",
            orderId = "so-print-01",
            productId = "prod-flexi-korea",
            productName = "Cetak Flexi Korea 340gr (m2)",
            itemType = ServiceOrderItemType.SERVICE_LABOR,
            unit = "M2",
            unitPrice = 30000L,
            costPrice = 12000L,
            quantity = 3.0 // 3 meter persegi
        )

        val withItemOrder = order.withItem(printItem, 1020L)
        assertEquals(90000L, withItemOrder.totalAmount)

        // Customer pays 50% down payment: 45.000 via QRIS
        val downPayment = DownPaymentRecord(
            id = "dp-print-01",
            businessId = "biz-print-01",
            branchId = "branch-print-01",
            orderId = "so-print-01",
            amount = 45000L,
            paymentMethod = PaymentMethod.QRIS,
            createdAt = 1050L
        )

        val withDpOrder = withItemOrder.withDownPayment(downPayment)
        assertEquals(45000L, withDpOrder.downPaymentAmount)
        assertEquals(45000L, withDpOrder.remainingBalance)

        // Printing underway -> IN_PROGRESS
        val inProgressOrder = withDpOrder.transitionTo(ServiceOrderStatus.IN_PROGRESS, 1100L)
        assertEquals(ServiceOrderStatus.IN_PROGRESS, inProgressOrder.status)

        // Banner printed, cut, and grommeted -> READY
        val readyOrder = inProgressOrder.transitionTo(ServiceOrderStatus.READY, 1300L)
        assertEquals(ServiceOrderStatus.READY, readyOrder.status)

        // Collected by customer -> COMPLETED
        val completedOrder = readyOrder.complete(1500L)
        assertEquals(ServiceOrderStatus.COMPLETED, completedOrder.status)
        assertTrue(completedOrder.isCompleted)
    }
}
