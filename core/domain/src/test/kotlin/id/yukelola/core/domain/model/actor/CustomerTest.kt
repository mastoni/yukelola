package id.yukelola.core.domain.model.actor

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.cash.CashRegister
import id.yukelola.core.domain.model.debt.CustomerDebt
import id.yukelola.core.domain.model.debt.DebtReferenceType
import id.yukelola.core.domain.model.debt.DebtStatus
import id.yukelola.core.domain.model.debt.SupplierDebt
import id.yukelola.core.domain.model.sale.PaymentStatus
import id.yukelola.core.domain.model.sale.Sale
import id.yukelola.core.domain.model.sale.SaleItem
import id.yukelola.core.domain.model.sale.SaleStatus
import id.yukelola.core.domain.model.sale.TransactionMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomerTest {

    @Test
    fun `valid customer creation with standard and default fields`() {
        val customer = Customer(
            id = "cust-001",
            businessId = "biz-001",
            branchId = "branch-001",
            name = "Ahmad Dani",
            phone = "081298765432",
            debtBalance = 250000L,
            isActive = true
        )

        assertEquals("cust-001", customer.id)
        assertEquals("biz-001", customer.businessId)
        assertEquals("branch-001", customer.branchId)
        assertEquals("Ahmad Dani", customer.name)
        assertEquals("081298765432", customer.phone)
        assertEquals(250000L, customer.debtBalance)
        assertTrue(customer.isActive)

        val defaultCustomer = Customer(
            id = "cust-002",
            businessId = "biz-001",
            name = "Siti Nurhaliza"
        )
        assertNull(defaultCustomer.branchId)
        assertNull(defaultCustomer.phone)
        assertEquals(0L, defaultCustomer.debtBalance)
        assertTrue(defaultCustomer.isActive)
    }

    @Test
    fun `customer rejects blank id`() {
        assertThrows(IllegalArgumentException::class.java) {
            Customer(
                id = "",
                businessId = "biz-001",
                name = "Pelanggan A"
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            Customer(
                id = "   ",
                businessId = "biz-001",
                name = "Pelanggan A"
            )
        }
    }

    @Test
    fun `customer rejects blank businessId`() {
        assertThrows(IllegalArgumentException::class.java) {
            Customer(
                id = "cust-001",
                businessId = "",
                name = "Pelanggan A"
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            Customer(
                id = "cust-001",
                businessId = "   ",
                name = "Pelanggan A"
            )
        }
    }

    @Test
    fun `customer rejects blank branchId when provided`() {
        assertThrows(IllegalArgumentException::class.java) {
            Customer(
                id = "cust-001",
                businessId = "biz-001",
                branchId = "   ",
                name = "Pelanggan A"
            )
        }
    }

    @Test
    fun `customer rejects blank name`() {
        assertThrows(IllegalArgumentException::class.java) {
            Customer(
                id = "cust-001",
                businessId = "biz-001",
                name = ""
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            Customer(
                id = "cust-001",
                businessId = "biz-001",
                name = "   "
            )
        }
    }

    @Test
    fun `customer rejects negative debt balance`() {
        assertThrows(IllegalArgumentException::class.java) {
            Customer(
                id = "cust-001",
                businessId = "biz-001",
                name = "Pelanggan A",
                debtBalance = -1L
            )
        }
    }

    @Test
    fun `customer identity remains stable across property updates`() {
        val original = Customer(
            id = "cust-001",
            businessId = "biz-001",
            name = "Budi Santoso",
            phone = "081111111",
            debtBalance = 0L,
            isActive = true
        )

        val updated = original.copy(
            name = "Budi Santoso S.Kom",
            phone = "082222222",
            debtBalance = 50000L,
            isActive = false
        )

        assertEquals(original.id, updated.id)
        assertEquals(original.businessId, updated.businessId)
        assertEquals("Budi Santoso S.Kom", updated.name)
        assertEquals("082222222", updated.phone)
        assertEquals(50000L, updated.debtBalance)
        assertFalse(updated.isActive)
    }

    @Test
    fun `customer belongs to one business and cross-business association is rejected`() {
        val customer = Customer(
            id = "cust-001",
            businessId = "biz-ALPHA",
            name = "Pelanggan Alpha"
        )

        Customer.validateBusinessOwnership(customer, "biz-ALPHA")

        assertThrows(IllegalArgumentException::class.java) {
            Customer.validateBusinessOwnership(customer, "biz-BETA")
        }
    }

    @Test
    fun `customer and customer debt remain separate aggregates`() {
        val customer = Customer(
            id = "cust-001",
            businessId = "biz-01",
            name = "Joko Widodo",
            debtBalance = 0L
        )

        val customerDebt = CustomerDebt.create(
            id = "cust-debt-001",
            businessId = "biz-01",
            branchId = "branch-01",
            customerId = customer.id,
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-100",
            originalAmount = 350000L,
            createdAt = 1000L
        )

        // CustomerDebt aggregate maintains its own independent lifecycle
        assertEquals(350000L, customerDebt.originalAmount)
        assertEquals(350000L, customerDebt.remainingAmount)
        assertEquals(DebtStatus.UNPAID, customerDebt.status)
        assertEquals(0L, customer.debtBalance)
    }

    @Test
    fun `customer creation has zero side effects on cash and supplier debt ledgers`() {
        val customer = Customer(
            id = "cust-001",
            businessId = "biz-01",
            name = "Dewi Lestari",
            debtBalance = 100000L
        )

        val register = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Laci 1",
            currentBalance = 1500000L,
            updatedAt = 1000L
        )

        val supplierDebt = SupplierDebt.create(
            id = "sup-debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            supplierId = "sup-01",
            purchaseId = "purch-01",
            originalAmount = 800000L,
            createdAt = 1000L
        )

        // Customer has no mutations on CashRegister or SupplierDebt
        assertEquals(1500000L, register.currentBalance)
        assertEquals(800000L, supplierDebt.remainingAmount)
    }

    @Test
    fun `guest sale without customer is valid for paid transactions`() {
        val attribution = TransactionAttribution(
            businessId = "biz-01",
            branchId = "branch-01",
            userId = "user-01",
            deviceId = "dev-01",
            createdAt = 1000L
        )

        val item = SaleItem(
            id = "item-01",
            saleId = "sale-01",
            productId = "prod-01",
            productName = "Kopi Susu",
            unit = "CUP",
            unitPrice = 18000L,
            costPrice = 10000L,
            quantity = 1.0,
            subtotal = 18000L
        )

        val guestSale = Sale(
            id = "sale-01",
            saleNumber = "TRX-001",
            attribution = attribution,
            customerId = null,
            items = listOf(item)
        ).complete(paidAmount = 18000L, completedAt = 1100L)

        assertNull(guestSale.customerId)
        assertEquals(SaleStatus.COMPLETED, guestSale.status)
        assertEquals(PaymentStatus.PAID, guestSale.paymentStatus)
    }

    @Test
    fun `credit sale strictly requires an identified customer`() {
        val attribution = TransactionAttribution(
            businessId = "biz-01",
            branchId = "branch-01",
            userId = "user-01",
            deviceId = "dev-01",
            createdAt = 1000L
        )

        val item = SaleItem(
            id = "item-01",
            saleId = "sale-02",
            productId = "prod-01",
            productName = "Gula Pasir 1kg",
            unit = "PCS",
            unitPrice = 16000L,
            costPrice = 13000L,
            quantity = 2.0,
            subtotal = 32000L
        )

        val draftSaleWithoutCustomer = Sale(
            id = "sale-02",
            saleNumber = "TRX-002",
            attribution = attribution,
            customerId = null,
            items = listOf(item)
        )

        // Attempting to complete unpaid/credit sale without customer must fail
        assertThrows(IllegalArgumentException::class.java) {
            draftSaleWithoutCustomer.complete(paidAmount = 10000L, completedAt = 1100L)
        }

        // Completing with customer succeeds
        val customer = Customer(
            id = "cust-001",
            businessId = "biz-01",
            name = "Pak RT"
        )

        val completedCreditSale = draftSaleWithoutCustomer.copy(customerId = customer.id)
            .complete(paidAmount = 10000L, completedAt = 1100L)

        assertEquals(customer.id, completedCreditSale.customerId)
        assertEquals(PaymentStatus.PARTIALLY_PAID, completedCreditSale.paymentStatus)
        assertEquals(22000L, completedCreditSale.remainingBalance)
    }
}
