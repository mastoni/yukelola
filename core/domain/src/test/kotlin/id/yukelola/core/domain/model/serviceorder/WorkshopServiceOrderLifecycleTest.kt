package id.yukelola.core.domain.model.serviceorder

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.payment.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkshopServiceOrderLifecycleTest {

    private val attribution = TransactionAttribution(
        businessId = "biz-workshop-01",
        branchId = "branch-workshop-01",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    @Test
    fun `workshop service order executes lifecycle with inspection labor and spare parts`() {
        val order = ServiceOrder(
            id = "so-bengkel-01",
            orderNumber = "BKL-001",
            orderType = ServiceOrderType.WORKSHOP,
            attribution = attribution,
            customerId = "cust-02",
            contextMetadataJson = """{"plateNumber":"B 1234 XYZ","vehicle":"Honda Vario 150"}""",
            notes = "Keluhan: Rem bunyi & ganti oli mesin",
            createdAt = 1000L
        )

        assertEquals(ServiceOrderStatus.RECEIVED, order.status)

        // Mechanic begins vehicle diagnosis -> INSPECTION
        val inspectionOrder = order.transitionTo(ServiceOrderStatus.INSPECTION, 1050L)
        assertEquals(ServiceOrderStatus.INSPECTION, inspectionOrder.status)

        // Add labor service item
        val laborItem = ServiceOrderItem(
            id = "item-labor-01",
            orderId = "so-bengkel-01",
            productId = "prod-service-ringan",
            productName = "Jasa Servis Ringan + Tune Up",
            itemType = ServiceOrderItemType.SERVICE_LABOR,
            unit = "PAKET",
            unitPrice = 50000L,
            costPrice = 0L,
            quantity = 1.0
        )

        // Add physical spare part item
        val sparePartItem = ServiceOrderItem(
            id = "item-part-01",
            orderId = "so-bengkel-01",
            productId = "prod-oli-mpx2",
            productName = "Oli AHM MPX2 0.8L",
            itemType = ServiceOrderItemType.PHYSICAL_PART,
            unit = "BOTOL",
            unitPrice = 65000L,
            costPrice = 48000L,
            quantity = 1.0
        )

        val withItemsOrder = inspectionOrder
            .withItem(laborItem, 1100L)
            .withItem(sparePartItem, 1100L)

        // Total = 50.000 + 65.000 = 115.000
        assertEquals(115000L, withItemsOrder.totalAmount)

        // Work actively underway -> IN_PROGRESS
        val inProgressOrder = withItemsOrder.transitionTo(ServiceOrderStatus.IN_PROGRESS, 1150L)
        assertEquals(ServiceOrderStatus.IN_PROGRESS, inProgressOrder.status)

        // Repair finished, vehicle ready for test drive & pickup -> READY
        val readyOrder = inProgressOrder.transitionTo(ServiceOrderStatus.READY, 1300L)
        assertEquals(ServiceOrderStatus.READY, readyOrder.status)

        // Vehicle handed over & invoice completed -> COMPLETED
        val completedOrder = readyOrder.complete(1400L)
        assertEquals(ServiceOrderStatus.COMPLETED, completedOrder.status)
        assertTrue(completedOrder.isCompleted)
    }
}
