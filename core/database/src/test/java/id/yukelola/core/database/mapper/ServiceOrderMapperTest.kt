package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.ServiceOrderEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.payment.PaymentMethod
import id.yukelola.core.domain.model.sale.TransactionMode
import id.yukelola.core.domain.model.serviceorder.DownPaymentRecord
import id.yukelola.core.domain.model.serviceorder.ServiceOrder
import id.yukelola.core.domain.model.serviceorder.ServiceOrderItem
import id.yukelola.core.domain.model.serviceorder.ServiceOrderItemType
import id.yukelola.core.domain.model.serviceorder.ServiceOrderStatus
import id.yukelola.core.domain.model.serviceorder.ServiceOrderType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ServiceOrderMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly with active order`() {
        val attribution = TransactionAttribution(
            businessId = "biz-01",
            branchId = "branch-01",
            userId = "user-01",
            deviceId = "dev-01",
            cashierSessionId = "sess-01",
            createdAt = 1700000000000L
        )

        val item = ServiceOrderItem(
            id = "soi-01",
            orderId = "so-01",
            productId = "prod-01",
            productName = "Cuci Kiloan",
            itemType = ServiceOrderItemType.SERVICE_LABOR,
            unit = "KG",
            unitPrice = 10000L,
            costPrice = 2000L,
            quantity = 5.0,
            discountAmount = 5000L,
            subtotal = 45000L
        )

        val dp = DownPaymentRecord(
            id = "dp-01",
            businessId = "biz-01",
            branchId = "branch-01",
            orderId = "so-01",
            amount = 20000L,
            paymentMethod = PaymentMethod.CASH,
            notes = "Intake DP",
            createdAt = 1700000000000L
        )

        val domain = ServiceOrder(
            id = "so-01",
            orderNumber = "SO-2026-001",
            orderType = ServiceOrderType.LAUNDRY,
            transactionMode = TransactionMode.SERVICE_ORDER_TRANSACTION,
            attribution = attribution,
            customerId = "cust-01",
            items = listOf(item),
            downPayments = listOf(dp),
            discountAmount = 0L,
            taxAmount = 0L,
            status = ServiceOrderStatus.RECEIVED,
            contextMetadataJson = "{\"detergent\":\"lavender\"}",
            estimatedCompletionDate = 1700086400000L,
            notes = "Fragile garments",
            createdAt = 1700000000000L,
            updatedAt = 1700000000000L,
            completedAt = null,
            cancelledAt = null
        )

        val entity = domain.toEntity()

        assertEquals("so-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("user-01", entity.userId)
        assertEquals("dev-01", entity.deviceId)
        assertEquals("sess-01", entity.cashierSessionId)
        assertEquals("SO-2026-001", entity.orderNumber)
        assertEquals("LAUNDRY", entity.orderType)
        assertEquals("SERVICE_ORDER_TRANSACTION", entity.transactionMode)
        assertEquals("cust-01", entity.customerId)
        assertEquals(0L, entity.discountAmount)
        assertEquals(0L, entity.taxAmount)
        assertEquals("RECEIVED", entity.status)
        assertEquals("{\"detergent\":\"lavender\"}", entity.contextMetadataJson)
        assertEquals(1700086400000L, entity.estimatedCompletionDate)
        assertEquals("Fragile garments", entity.notes)
        assertEquals(1700000000000L, entity.createdAt)
        assertEquals(1700000000000L, entity.updatedAt)
        assertNull(entity.completedAt)
        assertNull(entity.cancelledAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly with nullable fields null`() {
        val entity = ServiceOrderEntity(
            id = "so-02",
            businessId = "biz-02",
            branchId = "branch-02",
            userId = "user-02",
            deviceId = "dev-02",
            cashierSessionId = null,
            orderNumber = "SO-002",
            orderType = "WORKSHOP",
            transactionMode = "SERVICE_ORDER_TRANSACTION",
            customerId = null,
            discountAmount = 0L,
            taxAmount = 0L,
            status = "RECEIVED",
            contextMetadataJson = null,
            estimatedCompletionDate = null,
            notes = null,
            createdAt = 1700000000000L,
            updatedAt = 1700000000000L,
            completedAt = null,
            cancelledAt = null
        )

        val domain = entity.toDomain(emptyList(), emptyList())

        assertEquals("so-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals("user-02", domain.attribution.userId)
        assertEquals("dev-02", domain.attribution.deviceId)
        assertNull(domain.attribution.cashierSessionId)
        assertEquals("SO-002", domain.orderNumber)
        assertEquals(ServiceOrderType.WORKSHOP, domain.orderType)
        assertEquals(TransactionMode.SERVICE_ORDER_TRANSACTION, domain.transactionMode)
        assertNull(domain.customerId)
        assertEquals(0L, domain.discountAmount)
        assertEquals(0L, domain.taxAmount)
        assertEquals(ServiceOrderStatus.RECEIVED, domain.status)
        assertNull(domain.contextMetadataJson)
        assertNull(domain.estimatedCompletionDate)
        assertNull(domain.notes)
        assertEquals(1700000000000L, domain.createdAt)
        assertEquals(1700000000000L, domain.updatedAt)
        assertNull(domain.completedAt)
        assertNull(domain.cancelledAt)
    }

    @Test
    fun `full round trip preserves exact domain identity and precision`() {
        val attribution = TransactionAttribution(
            businessId = "biz-03",
            branchId = "branch-03",
            userId = "user-03",
            deviceId = "dev-03",
            cashierSessionId = "sess-03",
            createdAt = 1700000000000L
        )

        val item1 = ServiceOrderItem(
            id = "soi-03",
            orderId = "so-03",
            productId = "prod-03",
            productName = "Ganti Oli",
            itemType = ServiceOrderItemType.SERVICE_LABOR,
            unit = "PAKET",
            unitPrice = 50000L,
            costPrice = 10000L,
            quantity = 1.0,
            discountAmount = 0L,
            subtotal = 50000L
        )

        val item2 = ServiceOrderItem(
            id = "soi-04",
            orderId = "so-03",
            productId = "prod-04",
            productName = "Oli Mesin 1L",
            itemType = ServiceOrderItemType.PHYSICAL_PART,
            unit = "BOTOL",
            unitPrice = 75000L,
            costPrice = 60000L,
            quantity = 2.0,
            discountAmount = 5000L,
            subtotal = 145000L
        )

        val dp1 = DownPaymentRecord(
            id = "dp-03",
            businessId = "biz-03",
            branchId = "branch-03",
            orderId = "so-03",
            amount = 50000L,
            paymentMethod = PaymentMethod.TRANSFER,
            notes = "Transfer DP",
            createdAt = 1700000000000L
        )

        val domain = ServiceOrder(
            id = "so-03",
            orderNumber = "SO-003",
            orderType = ServiceOrderType.WORKSHOP,
            transactionMode = TransactionMode.SERVICE_ORDER_TRANSACTION,
            attribution = attribution,
            customerId = "cust-03",
            items = listOf(item1, item2),
            downPayments = listOf(dp1),
            discountAmount = 10000L,
            taxAmount = 5000L,
            status = ServiceOrderStatus.COMPLETED,
            contextMetadataJson = "{\"vehicle\":\"B1234XYZ\"}",
            estimatedCompletionDate = 1700050000000L,
            notes = "Periodic service",
            createdAt = 1700000000000L,
            updatedAt = 1700060000000L,
            completedAt = 1700060000000L,
            cancelledAt = null
        )

        val entity = domain.toEntity()
        val restored = entity.toDomain(listOf(item1, item2), listOf(dp1))

        assertEquals(domain, restored)
        assertEquals(195000L, restored.subtotal)
        assertEquals(190000L, restored.totalAmount)
        assertEquals(50000L, restored.downPaymentAmount)
        assertEquals(140000L, restored.remainingBalance)
        assertEquals(ServiceOrderStatus.COMPLETED, restored.status)
    }
}
