package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.SaleEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.sale.PaymentStatus
import id.yukelola.core.domain.model.sale.Sale
import id.yukelola.core.domain.model.sale.SaleItem
import id.yukelola.core.domain.model.sale.SaleStatus
import id.yukelola.core.domain.model.sale.TransactionMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SaleMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly with completed state and customer`() {
        val attribution = TransactionAttribution(
            businessId = "biz-01",
            branchId = "branch-01",
            userId = "user-01",
            deviceId = "dev-01",
            cashierSessionId = "sess-01",
            createdAt = 1700000000000L
        )

        val item = SaleItem(
            id = "item-01",
            saleId = "sale-01",
            productId = "prod-01",
            productName = "Product A",
            unit = "PCS",
            unitPrice = 50000L,
            costPrice = 35000L,
            quantity = 2.0,
            discountAmount = 5000L,
            subtotal = 95000L
        )

        val domain = Sale(
            id = "sale-01",
            saleNumber = "TRX-001",
            transactionMode = TransactionMode.RETAIL_TRANSACTION,
            attribution = attribution,
            customerId = "cust-01",
            items = listOf(item),
            discountAmount = 10000L,
            taxAmount = 5000L,
            paidAmount = 90000L,
            paymentStatus = PaymentStatus.PAID,
            status = SaleStatus.COMPLETED,
            notes = "Cash sale notes",
            createdAt = 1700000000000L,
            completedAt = 1700000050000L,
            cancelledAt = null
        )

        val entity = domain.toEntity()

        assertEquals("sale-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("user-01", entity.userId)
        assertEquals("dev-01", entity.deviceId)
        assertEquals("sess-01", entity.cashierSessionId)
        assertEquals("TRX-001", entity.saleNumber)
        assertEquals("RETAIL_TRANSACTION", entity.transactionMode)
        assertEquals("cust-01", entity.customerId)
        assertEquals(10000L, entity.discountAmount)
        assertEquals(5000L, entity.taxAmount)
        assertEquals(90000L, entity.paidAmount)
        assertEquals("PAID", entity.paymentStatus)
        assertEquals("COMPLETED", entity.status)
        assertEquals("Cash sale notes", entity.notes)
        assertEquals(1700000000000L, entity.createdAt)
        assertEquals(1700000050000L, entity.completedAt)
        assertNull(entity.cancelledAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly with nullable fields null`() {
        val entity = SaleEntity(
            id = "sale-draft-02",
            businessId = "biz-02",
            branchId = "branch-02",
            userId = "user-02",
            deviceId = "dev-02",
            cashierSessionId = null,
            saleNumber = "TRX-002",
            transactionMode = "RETAIL_TRANSACTION",
            customerId = null,
            discountAmount = 0L,
            taxAmount = 0L,
            paidAmount = 0L,
            paymentStatus = "UNPAID",
            status = "DRAFT",
            notes = null,
            createdAt = 1700000000000L,
            completedAt = null,
            cancelledAt = null
        )

        val domain = entity.toDomain(emptyList())

        assertEquals("sale-draft-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals("user-02", domain.attribution.userId)
        assertEquals("dev-02", domain.attribution.deviceId)
        assertNull(domain.attribution.cashierSessionId)
        assertEquals("TRX-002", domain.saleNumber)
        assertEquals(TransactionMode.RETAIL_TRANSACTION, domain.transactionMode)
        assertNull(domain.customerId)
        assertEquals(0L, domain.discountAmount)
        assertEquals(0L, domain.taxAmount)
        assertEquals(0L, domain.paidAmount)
        assertEquals(PaymentStatus.UNPAID, domain.paymentStatus)
        assertEquals(SaleStatus.DRAFT, domain.status)
        assertNull(domain.notes)
        assertEquals(1700000000000L, domain.createdAt)
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

        val item = SaleItem(
            id = "item-03",
            saleId = "sale-03",
            productId = "prod-03",
            productName = "Product C",
            unit = "KG",
            unitPrice = 25000L,
            costPrice = 20000L,
            quantity = 3.5,
            discountAmount = 2500L,
            subtotal = 85000L
        )

        val sale = Sale(
            id = "sale-03",
            saleNumber = "TRX-003",
            transactionMode = TransactionMode.RETAIL_TRANSACTION,
            attribution = attribution,
            customerId = "cust-03",
            items = listOf(item),
            discountAmount = 5000L,
            taxAmount = 0L,
            paidAmount = 80000L,
            paymentStatus = PaymentStatus.PAID,
            status = SaleStatus.COMPLETED,
            notes = "Roundtrip sale",
            createdAt = 1700000000000L,
            completedAt = 1700000060000L,
            cancelledAt = null
        )

        val entity = sale.toEntity()
        val restored = entity.toDomain(listOf(item))

        assertEquals(sale, restored)
    }
}
