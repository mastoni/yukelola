package id.yukelola.core.domain.model.purchase

import id.yukelola.core.domain.model.actor.Supplier
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.sale.PaymentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PurchaseAggregateTest {

    private val attribution = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-01",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    private val supplier = Supplier(
        id = "sup-01",
        businessId = "biz-01",
        name = "PT Sumber Sembako Utama",
        phone = "081122334455"
    )

    @Test
    fun `purchase creates and calculates total deterministically from items`() {
        val item1 = PurchaseItem(
            id = "pi-01",
            purchaseId = "purch-01",
            productId = "prod-01",
            productName = "Beras Premium 25kg",
            unit = "KARUNG",
            unitCost = 320000L,
            quantity = 5.0 // 1.600.000
        )

        val item2 = PurchaseItem(
            id = "pi-02",
            purchaseId = "purch-01",
            productId = "prod-02",
            productName = "Gula Pasir 50kg",
            unit = "KARUNG",
            unitCost = 650000L,
            quantity = 2.0 // 1.300.000
        )

        val purchase = Purchase(
            id = "purch-01",
            purchaseNumber = "PO-2026-001",
            attribution = attribution,
            supplierId = supplier.id,
            items = listOf(item1, item2),
            paidAmount = 2900000L,
            createdAt = 1000L
        )

        assertEquals("purch-01", purchase.id)
        assertEquals("biz-01", purchase.businessId)
        assertEquals("branch-01", purchase.branchId)
        assertEquals("sup-01", purchase.supplierId)
        assertEquals(2900000L, purchase.totalAmount)
        assertEquals(2900000L, purchase.paidAmount)
        assertEquals(0L, purchase.remainingBalance)
        assertEquals(PaymentStatus.PAID, purchase.paymentStatus)
    }

    @Test
    fun `purchase supports dynamic item addition and removal with payment status recalculation`() {
        val purchase = Purchase(
            id = "purch-01",
            purchaseNumber = "PO-001",
            attribution = attribution,
            supplierId = supplier.id,
            paidAmount = 100000L,
            createdAt = 1000L
        )

        val item = PurchaseItem(
            id = "pi-01",
            purchaseId = "purch-01",
            productId = "prod-01",
            productName = "Kopi Sachet 1 Dus",
            unit = "DUS",
            unitCost = 150000L,
            quantity = 2.0 // 300.000
        )

        val withItem = purchase.withItem(item)
        assertEquals(300000L, withItem.totalAmount)
        assertEquals(200000L, withItem.remainingBalance)
        assertEquals(PaymentStatus.PARTIALLY_PAID, withItem.paymentStatus)

        val withoutItem = withItem.withoutItem("pi-01")
        assertEquals(0L, withoutItem.totalAmount)
        assertEquals(PaymentStatus.PAID, withoutItem.paymentStatus)
    }

    @Test
    fun `credit purchase with unpaid balance requires identified supplier`() {
        val item = PurchaseItem(
            id = "pi-01",
            purchaseId = "purch-01",
            productId = "prod-01",
            productName = "Tepung Terigu",
            unit = "SAK",
            unitCost = 200000L,
            quantity = 1.0
        )

        // Credit purchase (paidAmount = 50.000 < totalAmount = 200.000) without supplierId should throw
        assertThrows(IllegalArgumentException::class.java) {
            Purchase(
                id = "purch-01",
                purchaseNumber = "PO-001",
                attribution = attribution,
                supplierId = null,
                items = listOf(item),
                paidAmount = 50000L,
                createdAt = 1000L
            )
        }
    }

    @Test
    fun `historical product snapshot remains invariant when catalog cost changes later`() {
        var catalogProductCost = 100000L

        val item = PurchaseItem(
            id = "pi-01",
            purchaseId = "purch-01",
            productId = "prod-01",
            productName = "Minyak Kita 12L",
            unitCost = catalogProductCost,
            quantity = 10.0
        )

        val purchase = Purchase(
            id = "purch-01",
            purchaseNumber = "PO-001",
            attribution = attribution,
            supplierId = supplier.id,
            items = listOf(item),
            paidAmount = 1000000L,
            createdAt = 1000L
        )

        // Next week, supplier raises catalog cost price
        catalogProductCost = 120000L

        // Historical purchase record retains original unitCost and total
        assertEquals(100000L, purchase.items.first().unitCost)
        assertEquals(1000000L, purchase.totalAmount)
    }

    @Test
    fun `supplier ownership validation rejects supplier from different business`() {
        val crossBusinessSupplier = Supplier(
            id = "sup-other",
            businessId = "biz-other",
            name = "Vendor Luar"
        )

        val purchase = Purchase(
            id = "purch-01",
            purchaseNumber = "PO-001",
            attribution = attribution, // biz-01
            supplierId = "sup-01",
            createdAt = 1000L
        )

        assertThrows(IllegalArgumentException::class.java) {
            Purchase.validateSupplierOwnership(crossBusinessSupplier, purchase)
        }
    }
}
