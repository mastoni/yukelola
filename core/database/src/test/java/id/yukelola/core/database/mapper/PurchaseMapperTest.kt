package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.PurchaseEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.purchase.Purchase
import id.yukelola.core.domain.model.purchase.PurchaseItem
import id.yukelola.core.domain.model.sale.PaymentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PurchaseMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly with completed paid purchase`() {
        val attribution = TransactionAttribution(
            businessId = "biz-01",
            branchId = "branch-01",
            userId = "user-01",
            deviceId = "dev-01",
            cashierSessionId = "sess-01",
            createdAt = 1700000000000L
        )

        val item = PurchaseItem(
            id = "pi-01",
            purchaseId = "purch-01",
            productId = "prod-01",
            productName = "Product A",
            unit = "BOX",
            unitCost = 150000L,
            quantity = 10.0,
            subtotal = 1500000L
        )

        val domain = Purchase(
            id = "purch-01",
            purchaseNumber = "PO-2026-001",
            attribution = attribution,
            supplierId = "sup-01",
            items = listOf(item),
            paidAmount = 1500000L,
            paymentStatus = PaymentStatus.PAID,
            notes = "Cash procurement notes",
            createdAt = 1700000000000L
        )

        val entity = domain.toEntity()

        assertEquals("purch-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("user-01", entity.userId)
        assertEquals("dev-01", entity.deviceId)
        assertEquals("sess-01", entity.cashierSessionId)
        assertEquals("PO-2026-001", entity.purchaseNumber)
        assertEquals("sup-01", entity.supplierId)
        assertEquals(1500000L, entity.paidAmount)
        assertEquals("PAID", entity.paymentStatus)
        assertEquals("Cash procurement notes", entity.notes)
        assertEquals(1700000000000L, entity.createdAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly with nullable fields null`() {
        val entity = PurchaseEntity(
            id = "purch-02",
            businessId = "biz-02",
            branchId = "branch-02",
            userId = "user-02",
            deviceId = "dev-02",
            cashierSessionId = null,
            purchaseNumber = "PO-002",
            supplierId = null,
            paidAmount = 0L,
            paymentStatus = "PAID", // Empty purchase resolves to PAID
            notes = null,
            createdAt = 1700000000000L
        )

        val domain = entity.toDomain(emptyList())

        assertEquals("purch-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals("user-02", domain.attribution.userId)
        assertEquals("dev-02", domain.attribution.deviceId)
        assertNull(domain.attribution.cashierSessionId)
        assertEquals("PO-002", domain.purchaseNumber)
        assertNull(domain.supplierId)
        assertEquals(0L, domain.paidAmount)
        assertEquals(PaymentStatus.PAID, domain.paymentStatus)
        assertNull(domain.notes)
        assertEquals(1700000000000L, domain.createdAt)
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

        val item1 = PurchaseItem(
            id = "pi-03",
            purchaseId = "purch-03",
            productId = "prod-03",
            productName = "Product 3",
            unit = "KG",
            unitCost = 25000L,
            quantity = 40.0,
            subtotal = 1000000L
        )

        val item2 = PurchaseItem(
            id = "pi-04",
            purchaseId = "purch-03",
            productId = "prod-04",
            productName = "Product 4",
            unit = "LITER",
            unitCost = 14000L,
            quantity = 50.0,
            subtotal = 700000L
        )

        val domain = Purchase(
            id = "purch-03",
            purchaseNumber = "PO-003",
            attribution = attribution,
            supplierId = "sup-03",
            items = listOf(item1, item2),
            paidAmount = 1000000L, // Credit purchase: total = 1.700.000, paid = 1.000.000
            paymentStatus = PaymentStatus.PARTIALLY_PAID,
            notes = "Credit procurement",
            createdAt = 1700000000000L
        )

        val entity = domain.toEntity()
        val restored = entity.toDomain(listOf(item1, item2))

        assertEquals(domain, restored)
        assertEquals(1700000L, restored.totalAmount)
        assertEquals(700000L, restored.remainingBalance)
        assertEquals(PaymentStatus.PARTIALLY_PAID, restored.paymentStatus)
    }
}
