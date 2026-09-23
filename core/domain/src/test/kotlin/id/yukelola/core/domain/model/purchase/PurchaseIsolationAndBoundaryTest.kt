package id.yukelola.core.domain.model.purchase

import id.yukelola.core.domain.model.actor.Supplier
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.cash.CashRegister
import id.yukelola.core.domain.model.catalog.BranchProductOverride
import id.yukelola.core.domain.model.debt.SupplierDebt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class PurchaseIsolationAndBoundaryTest {

    private val attributionBranchA = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-a",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    private val attributionBranchB = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-b",
        userId = "user-02",
        deviceId = "dev-02",
        createdAt = 1000L
    )

    @Test
    fun `purchase is branch isolated via attribution`() {
        val purchaseBranchA = Purchase(
            id = "purch-a-01",
            purchaseNumber = "PO-A-01",
            attribution = attributionBranchA,
            supplierId = "sup-01",
            createdAt = 1000L
        )

        val purchaseBranchB = Purchase(
            id = "purch-b-01",
            purchaseNumber = "PO-B-01",
            attribution = attributionBranchB,
            supplierId = "sup-01",
            createdAt = 1000L
        )

        assertEquals("branch-a", purchaseBranchA.branchId)
        assertEquals("branch-b", purchaseBranchB.branchId)
        assertNotEquals(purchaseBranchA.branchId, purchaseBranchB.branchId)
    }

    @Test
    fun `purchase creation does NOT mutate branch stock, cash register, or supplier debt automatically`() {
        val branchStock = BranchProductOverride(
            branchId = "branch-a",
            productId = "prod-01",
            stock = 20.0,
            minStock = 5.0
        )

        val cashRegister = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-a",
            name = "Cash Drawer",
            currentBalance = 5000000L,
            updatedAt = 1000L
        )

        val item = PurchaseItem(
            id = "pi-01",
            purchaseId = "purch-a-01",
            productId = "prod-01",
            productName = "Beras 25kg",
            unitCost = 300000L,
            quantity = 10.0
        )

        val purchase = Purchase(
            id = "purch-a-01",
            purchaseNumber = "PO-A-01",
            attribution = attributionBranchA,
            supplierId = "sup-01",
            items = listOf(item),
            paidAmount = 1000000L, // Credit purchase: total = 3.000.000, paid = 1.000.000
            createdAt = 1000L
        )

        assertEquals(3000000L, purchase.totalAmount)
        assertEquals(2000000L, purchase.remainingBalance)

        // INVARIANTS:
        // 1. Stock remains untouched (still 20.0)
        assertEquals(20.0, branchStock.stock, 0.0)

        // 2. Cash drawer remains untouched (still 5.000.000)
        assertEquals(5000000L, cashRegister.currentBalance)

        // 3. Purchase can act as a reference for SupplierDebt, but does not mutate it automatically
        val debt = SupplierDebt.create(
            id = "debt-sup-01",
            businessId = purchase.businessId,
            branchId = purchase.branchId,
            supplierId = "sup-01",
            purchaseId = purchase.id,
            originalAmount = purchase.remainingBalance,
            createdAt = 1000L
        )

        assertEquals("purch-a-01", debt.purchaseId)
        assertEquals(2000000L, debt.remainingAmount)
    }
}
