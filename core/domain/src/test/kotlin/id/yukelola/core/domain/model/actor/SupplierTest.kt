package id.yukelola.core.domain.model.actor

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.cash.CashRegister
import id.yukelola.core.domain.model.debt.DebtStatus
import id.yukelola.core.domain.model.debt.SupplierDebt
import id.yukelola.core.domain.model.purchase.Purchase
import id.yukelola.core.domain.model.purchase.PurchaseItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class SupplierTest {

    @Test
    fun `valid supplier creation with standard and default fields`() {
        val supplier = Supplier(
            id = "sup-001",
            businessId = "biz-001",
            name = "PT Sumber Makmur",
            phone = "081234567890",
            debtBalance = 500000L,
            isActive = true
        )

        assertEquals("sup-001", supplier.id)
        assertEquals("biz-001", supplier.businessId)
        assertEquals("PT Sumber Makmur", supplier.name)
        assertEquals("081234567890", supplier.phone)
        assertEquals(500000L, supplier.debtBalance)
        assertTrue(supplier.isActive)

        val defaultSupplier = Supplier(
            id = "sup-002",
            businessId = "biz-001",
            name = "CV Maju Jaya"
        )
        assertNull(defaultSupplier.phone)
        assertEquals(0L, defaultSupplier.debtBalance)
        assertTrue(defaultSupplier.isActive)
    }

    @Test
    fun `supplier rejects blank id`() {
        assertThrows(IllegalArgumentException::class.java) {
            Supplier(
                id = "",
                businessId = "biz-001",
                name = "Vendor A"
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            Supplier(
                id = "   ",
                businessId = "biz-001",
                name = "Vendor A"
            )
        }
    }

    @Test
    fun `supplier rejects blank businessId`() {
        assertThrows(IllegalArgumentException::class.java) {
            Supplier(
                id = "sup-001",
                businessId = "",
                name = "Vendor A"
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            Supplier(
                id = "sup-001",
                businessId = "   ",
                name = "Vendor A"
            )
        }
    }

    @Test
    fun `supplier rejects blank name`() {
        assertThrows(IllegalArgumentException::class.java) {
            Supplier(
                id = "sup-001",
                businessId = "biz-001",
                name = ""
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            Supplier(
                id = "sup-001",
                businessId = "biz-001",
                name = "   "
            )
        }
    }

    @Test
    fun `supplier rejects negative debt balance`() {
        assertThrows(IllegalArgumentException::class.java) {
            Supplier(
                id = "sup-001",
                businessId = "biz-001",
                name = "Vendor A",
                debtBalance = -1L
            )
        }
    }

    @Test
    fun `supplier identity remains stable across property modifications`() {
        val original = Supplier(
            id = "sup-001",
            businessId = "biz-001",
            name = "PT Sumber Pangan",
            phone = "081111111",
            debtBalance = 0L,
            isActive = true
        )

        val updated = original.copy(
            name = "PT Sumber Pangan Abadi",
            phone = "082222222",
            isActive = false
        )

        assertEquals(original.id, updated.id)
        assertEquals(original.businessId, updated.businessId)
        assertEquals("PT Sumber Pangan Abadi", updated.name)
        assertEquals("082222222", updated.phone)
        assertFalse(updated.isActive)
    }

    @Test
    fun `supplier creation has zero automatic side effects on SupplierDebt aggregate`() {
        val supplier = Supplier(
            id = "sup-001",
            businessId = "biz-001",
            name = "Distributor Utama",
            debtBalance = 0L
        )

        val debt = SupplierDebt.create(
            id = "sup-debt-001",
            businessId = "biz-001",
            branchId = "branch-001",
            supplierId = supplier.id,
            purchaseId = "purch-001",
            originalAmount = 1500000L,
            createdAt = 1000L
        )

        // Supplier master creation does NOT mutate existing SupplierDebt aggregate
        assertEquals(1500000L, debt.originalAmount)
        assertEquals(1500000L, debt.remainingAmount)
        assertEquals(DebtStatus.UNPAID, debt.status)
        assertEquals(0L, supplier.debtBalance)
    }

    @Test
    fun `supplier creation has zero automatic side effects on CashRegister`() {
        val supplier = Supplier(
            id = "sup-001",
            businessId = "biz-001",
            name = "Distributor Beras",
            debtBalance = 1000000L
        )

        val register = CashRegister(
            id = "reg-001",
            businessId = "biz-001",
            branchId = "branch-001",
            name = "Laci Kasir 1",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        // Supplier master does not mutate CashRegister balance
        assertEquals(500000L, register.currentBalance)
    }

    @Test
    fun `purchase can reference canonical business-scoped supplier`() {
        val supplier = Supplier(
            id = "sup-001",
            businessId = "biz-001",
            name = "PT Grosir Retail",
            phone = "085555555"
        )

        val attribution = TransactionAttribution(
            businessId = "biz-001",
            branchId = "branch-001",
            userId = "user-001",
            deviceId = "dev-001",
            createdAt = 1000L
        )

        val item = PurchaseItem(
            id = "p-item-01",
            purchaseId = "purch-001",
            productId = "prod-001",
            productName = "Minyak Goreng 2L",
            unitCost = 28000L,
            quantity = 10.0
        )

        val purchase = Purchase(
            id = "purch-001",
            purchaseNumber = "PO-2026-001",
            attribution = attribution,
            supplierId = supplier.id,
            items = listOf(item),
            paidAmount = 280000L
        )

        assertEquals(supplier.id, purchase.supplierId)
        Purchase.validateSupplierOwnership(supplier, purchase)
    }

    @Test
    fun `cross-business supplier referencing is rejected by purchase validation`() {
        val supplier = Supplier(
            id = "sup-001",
            businessId = "biz-OTHER",
            name = "PT Supplier Asing"
        )

        val attribution = TransactionAttribution(
            businessId = "biz-001",
            branchId = "branch-001",
            userId = "user-001",
            deviceId = "dev-001",
            createdAt = 1000L
        )

        val purchase = Purchase(
            id = "purch-001",
            purchaseNumber = "PO-2026-001",
            attribution = attribution,
            supplierId = supplier.id
        )

        assertThrows(IllegalArgumentException::class.java) {
            Purchase.validateSupplierOwnership(supplier, purchase)
        }
    }
}
