package id.yukelola.core.domain.model.stock

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.business.BusinessProfile
import id.yukelola.core.domain.model.cash.CashRegister
import id.yukelola.core.domain.model.catalog.BranchProductOverride
import id.yukelola.core.domain.model.catalog.Product
import id.yukelola.core.domain.model.catalog.ProductType
import id.yukelola.core.domain.model.debt.CustomerDebt
import id.yukelola.core.domain.model.debt.DebtReferenceType
import id.yukelola.core.domain.model.debt.SupplierDebt
import id.yukelola.core.domain.model.digital.DigitalDepositAccount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class StockDomainTest {

    @Test
    fun `PHYSICAL product maintains inventory tracking whereas SERVICE and DIGITAL do not`() {
        val physicalProduct = Product(
            id = "prod-phys-01",
            businessId = "biz-01",
            name = "Beras Rojolele 5kg",
            productType = ProductType.PHYSICAL,
            trackStock = true
        )
        assertTrue(physicalProduct.productType.requiresPhysicalStock)
        assertTrue(physicalProduct.isInventoryTracked)

        val serviceProduct = Product(
            id = "prod-srv-01",
            businessId = "biz-01",
            name = "Jasa Servis Motor",
            productType = ProductType.SERVICE,
            trackStock = true
        )
        assertFalse(serviceProduct.productType.requiresPhysicalStock)
        assertFalse(serviceProduct.isInventoryTracked)

        val digitalProduct = Product(
            id = "prod-dig-01",
            businessId = "biz-01",
            name = "Pulsa Telkomsel 50k",
            productType = ProductType.DIGITAL,
            trackStock = true
        )
        assertFalse(digitalProduct.productType.requiresPhysicalStock)
        assertFalse(digitalProduct.isInventoryTracked)
    }

    @Test
    fun `zero and positive stock are valid on branch override`() {
        val zeroStock = BranchProductOverride(
            branchId = "branch-01",
            productId = "prod-01",
            stock = 0.0
        )
        assertEquals(0.0, zeroStock.stock, 0.001)

        val positiveStock = zeroStock.withStock(25.5)
        assertEquals(25.5, positiveStock.stock, 0.001)
    }

    @Test
    fun `negative stock is rejected by default when allowNegativeStock is false`() {
        val override = BranchProductOverride(
            branchId = "branch-01",
            productId = "prod-01",
            stock = 5.0
        )

        assertThrows(IllegalArgumentException::class.java) {
            override.withStock(-1.0, allowNegativeStock = false)
        }

        assertThrows(IllegalArgumentException::class.java) {
            override.withStockDelta(-10.0, allowNegativeStock = false)
        }
    }

    @Test
    fun `negative stock is accepted when allowNegativeStock is true`() {
        val profileWithNegativeAllowed = BusinessProfile(
            name = "Warung Fleksibel",
            allowNegativeStock = true
        )
        assertTrue(profileWithNegativeAllowed.allowNegativeStock)

        val override = BranchProductOverride(
            branchId = "branch-01",
            productId = "prod-01",
            stock = 5.0
        )

        val negativeResult = override.withStockDelta(
            delta = -10.0,
            allowNegativeStock = profileWithNegativeAllowed.allowNegativeStock
        )
        assertEquals(-5.0, negativeResult.stock, 0.001)
    }

    @Test
    fun `stock adjustment requires PHYSICAL product and rejects SERVICE or DIGITAL`() {
        val physicalProduct = Product(
            id = "prod-phys",
            businessId = "biz-01",
            name = "Oli Mesin",
            productType = ProductType.PHYSICAL
        )
        val serviceProduct = Product(
            id = "prod-srv",
            businessId = "biz-01",
            name = "Jasa Ganti Oli",
            productType = ProductType.SERVICE
        )
        val digitalProduct = Product(
            id = "prod-dig",
            businessId = "biz-01",
            name = "Token PLN",
            productType = ProductType.DIGITAL
        )

        val attribution = TransactionAttribution(
            businessId = "biz-01",
            branchId = "branch-01",
            userId = "user-01",
            deviceId = "dev-01",
            createdAt = 1000L
        )

        // PHYSICAL product succeeds
        val validAdjustment = StockAdjustment.create(
            id = "adj-01",
            product = physicalProduct,
            previousStock = 10.0,
            adjustedStock = 8.0,
            reason = StockAdjustmentReason.DAMAGED,
            attribution = attribution
        )
        assertEquals(-2.0, validAdjustment.deltaQuantity, 0.001)

        // SERVICE product fails
        assertThrows(IllegalArgumentException::class.java) {
            StockAdjustment.create(
                id = "adj-02",
                product = serviceProduct,
                previousStock = 0.0,
                adjustedStock = 5.0,
                reason = StockAdjustmentReason.STOCK_OPNAME,
                attribution = attribution
            )
        }

        // DIGITAL product fails
        assertThrows(IllegalArgumentException::class.java) {
            StockAdjustment.create(
                id = "adj-03",
                product = digitalProduct,
                previousStock = 0.0,
                adjustedStock = 100.0,
                reason = StockAdjustmentReason.STOCK_OPNAME,
                attribution = attribution
            )
        }
    }

    @Test
    fun `all five contract-mandated stock adjustment reasons are supported`() {
        val expectedReasons = setOf(
            StockAdjustmentReason.STOCK_OPNAME,
            StockAdjustmentReason.DAMAGED,
            StockAdjustmentReason.EXPIRED,
            StockAdjustmentReason.LOST,
            StockAdjustmentReason.INTERNAL_USE
        )
        assertEquals(5, StockAdjustmentReason.values().size)
        assertEquals(expectedReasons, StockAdjustmentReason.values().toSet())
    }

    @Test
    fun `stock adjustment creation validates reasons and calculates delta correctly`() {
        val opname = StockAdjustment.create(
            id = "adj-opname",
            businessId = "biz-01",
            branchId = "branch-01",
            productId = "prod-01",
            productType = ProductType.PHYSICAL,
            previousStock = 12.0,
            adjustedStock = 15.0,
            reason = StockAdjustmentReason.STOCK_OPNAME,
            userId = "user-01",
            deviceId = "dev-01",
            createdAt = 1000L
        )
        assertEquals(3.0, opname.deltaQuantity, 0.001)
        assertEquals(StockAdjustmentReason.STOCK_OPNAME, opname.reason)

        val expired = StockAdjustment.create(
            id = "adj-exp",
            businessId = "biz-01",
            branchId = "branch-01",
            productId = "prod-01",
            productType = ProductType.PHYSICAL,
            previousStock = 10.0,
            adjustedStock = 7.0,
            reason = StockAdjustmentReason.EXPIRED,
            userId = "user-01",
            deviceId = "dev-01",
            createdAt = 1000L,
            notes = "Barang expired bulan lalu"
        )
        assertEquals(-3.0, expired.deltaQuantity, 0.001)
        assertEquals("Barang expired bulan lalu", expired.notes)
    }

    @Test
    fun `stock adjustment rejects cross-business mismatch`() {
        val product = Product(
            id = "prod-01",
            businessId = "biz-A",
            name = "Indomie Goreng",
            productType = ProductType.PHYSICAL
        )

        val attributionBranchB = TransactionAttribution(
            businessId = "biz-B",
            branchId = "branch-01",
            userId = "user-01",
            deviceId = "dev-01",
            createdAt = 1000L
        )

        assertThrows(IllegalArgumentException::class.java) {
            StockAdjustment.create(
                id = "adj-cross",
                product = product,
                previousStock = 10.0,
                adjustedStock = 5.0,
                reason = StockAdjustmentReason.DAMAGED,
                attribution = attributionBranchB
            )
        }
    }

    @Test
    fun `stock adjustment has zero direct side effects on financial ledgers`() {
        val adjustment = StockAdjustment.create(
            id = "adj-audit",
            businessId = "biz-01",
            branchId = "branch-01",
            productId = "prod-01",
            productType = ProductType.PHYSICAL,
            previousStock = 10.0,
            adjustedStock = 6.0,
            reason = StockAdjustmentReason.LOST,
            userId = "user-01",
            deviceId = "dev-01",
            createdAt = 1000L
        )

        val cashRegister = CashRegister(
            id = "cash-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Kasir 1",
            currentBalance = 1000000L,
            updatedAt = 1000L
        )

        val customerDebt = CustomerDebt.create(
            id = "cust-debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            customerId = "cust-01",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-01",
            originalAmount = 150000L,
            createdAt = 1000L
        )

        val supplierDebt = SupplierDebt.create(
            id = "sup-debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            supplierId = "sup-01",
            purchaseId = "purch-01",
            originalAmount = 500000L,
            createdAt = 1000L
        )

        val digitalDeposit = DigitalDepositAccount(
            id = "dep-01",
            businessId = "biz-01",
            branchId = "branch-01",
            currentBalance = 2000000L,
            updatedAt = 1000L
        )

        // Adjustment exists independently and alters none of the financial balances
        assertEquals(1000000L, cashRegister.currentBalance)
        assertEquals(150000L, customerDebt.remainingAmount)
        assertEquals(500000L, supplierDebt.remainingAmount)
        assertEquals(2000000L, digitalDeposit.currentBalance)
        assertEquals(-4.0, adjustment.deltaQuantity, 0.001)
    }
}
