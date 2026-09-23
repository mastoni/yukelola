package id.yukelola.core.domain.model.sale

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RetailSaleLifecycleTest {

    private val sampleAttribution = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-01",
        userId = "user-kasir-01",
        deviceId = "dev-pos-01",
        cashierSessionId = "sess-01",
        createdAt = 1711234000000L
    )

    private val sampleItem = SaleItem(
        id = "item-01",
        saleId = "sale-100",
        productId = "prod-01",
        productName = "Kopi Sachet",
        unitPrice = 3000L,
        quantity = 2.0
    )

    @Test
    fun `Sale initializes in DRAFT state and allows item additions`() {
        var draftSale = Sale(
            id = "sale-100",
            saleNumber = "TRX-CAB01-20260924-001",
            attribution = sampleAttribution
        )

        assertTrue(draftSale.isDraft)
        assertEquals(0L, draftSale.subtotal)

        draftSale = draftSale.withItem(sampleItem)
        assertEquals(1, draftSale.items.size)
        assertEquals(6000L, draftSale.subtotal)
        assertEquals(6000L, draftSale.totalAmount)
    }

    @Test
    fun `Sale transitions from DRAFT to COMPLETED upon payment settlement`() {
        val draftSale = Sale(
            id = "sale-100",
            saleNumber = "TRX-CAB01-20260924-001",
            attribution = sampleAttribution,
            items = listOf(sampleItem)
        )

        val completedSale = draftSale.complete(
            paidAmount = 6000L,
            completedAt = 1711234050000L
        )

        assertFalse(completedSale.isDraft)
        assertTrue(completedSale.isCompleted)
        assertEquals(PaymentStatus.PAID, completedSale.paymentStatus)
        assertEquals(6000L, completedSale.paidAmount)
        assertEquals(0L, completedSale.remainingBalance)
        assertEquals(1711234050000L, completedSale.completedAt)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Completing a sale with zero items throws exception`() {
        val emptyDraft = Sale(
            id = "sale-101",
            saleNumber = "TRX-CAB01-20260924-002",
            attribution = sampleAttribution,
            items = emptyList()
        )

        emptyDraft.complete(paidAmount = 0L, completedAt = 1711234050000L)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Credit sale with unpaid balance without customer throws exception`() {
        val draft = Sale(
            id = "sale-102",
            saleNumber = "TRX-CAB01-20260924-003",
            attribution = sampleAttribution,
            customerId = null, // Anonymous credit prohibited
            items = listOf(sampleItem)
        )

        draft.complete(paidAmount = 2000L, completedAt = 1711234050000L)
    }

    @Test
    fun `Credit sale with identified customer succeeds with PARTIALLY_PAID status`() {
        val draftWithCustomer = Sale(
            id = "sale-103",
            saleNumber = "TRX-CAB01-20260924-004",
            attribution = sampleAttribution,
            customerId = "cust-pak-rt",
            items = listOf(sampleItem)
        )

        val creditSale = draftWithCustomer.complete(
            paidAmount = 2000L,
            completedAt = 1711234050000L
        )

        assertTrue(creditSale.isCompleted)
        assertEquals(PaymentStatus.PARTIALLY_PAID, creditSale.paymentStatus)
        assertEquals(4000L, creditSale.remainingBalance)
    }

    @Test
    fun `Completed sale transitions to CANCELLED state`() {
        val draft = Sale(
            id = "sale-104",
            saleNumber = "TRX-CAB01-20260924-005",
            attribution = sampleAttribution,
            items = listOf(sampleItem)
        )
        val completed = draft.complete(paidAmount = 6000L, completedAt = 1711234050000L)

        val cancelled = completed.cancel(cancelledAt = 1711234100000L)

        assertTrue(cancelled.isCancelled)
        assertEquals(1711234100000L, cancelled.cancelledAt)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Cancelling an already cancelled sale throws exception`() {
        val draft = Sale(
            id = "sale-105",
            saleNumber = "TRX-CAB01-20260924-006",
            attribution = sampleAttribution,
            items = listOf(sampleItem)
        )
        val cancelled = draft.cancel(cancelledAt = 1711234100000L)
        cancelled.cancel(cancelledAt = 1711234200000L)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Cannot add items to completed sale`() {
        val draft = Sale(
            id = "sale-106",
            saleNumber = "TRX-CAB01-20260924-007",
            attribution = sampleAttribution,
            items = listOf(sampleItem)
        )
        val completed = draft.complete(paidAmount = 6000L, completedAt = 1711234050000L)

        val extraItem = SaleItem(
            id = "item-02",
            saleId = "sale-106",
            productId = "prod-02",
            productName = "Gula Pasir",
            unitPrice = 15000L
        )
        completed.withItem(extraItem)
    }
}
