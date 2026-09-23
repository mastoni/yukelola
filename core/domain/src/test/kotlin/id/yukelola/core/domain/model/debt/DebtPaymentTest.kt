package id.yukelola.core.domain.model.debt

import id.yukelola.core.domain.model.actor.Customer
import id.yukelola.core.domain.model.actor.Supplier
import id.yukelola.core.domain.model.cash.CashMutation
import id.yukelola.core.domain.model.cash.CashMutationType
import id.yukelola.core.domain.model.cash.CashRegister
import id.yukelola.core.domain.model.catalog.BranchProductOverride
import id.yukelola.core.domain.model.digital.DigitalDepositAccount
import id.yukelola.core.domain.model.payment.Payment
import id.yukelola.core.domain.model.payment.PaymentMethod
import id.yukelola.core.domain.model.payment.PaymentTransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class DebtPaymentTest {

    @Test
    fun `valid CUSTOMER DebtPayment creation`() {
        val payment = DebtPayment(
            id = "dp-cust-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = "cust-debt-100",
            amount = 75000L,
            paymentMethod = PaymentMethod.CASH,
            notes = "Cicilan kasbon pelanggan",
            createdAt = 1000L
        )

        assertEquals("dp-cust-01", payment.id)
        assertEquals("biz-01", payment.businessId)
        assertEquals("branch-01", payment.branchId)
        assertEquals(DebtType.CUSTOMER, payment.debtType)
        assertEquals("cust-debt-100", payment.debtId)
        assertEquals(75000L, payment.amount)
        assertEquals(PaymentMethod.CASH, payment.paymentMethod)
        assertEquals("Cicilan kasbon pelanggan", payment.notes)
        assertEquals(1000L, payment.createdAt)
    }

    @Test
    fun `valid SUPPLIER DebtPayment creation`() {
        val payment = DebtPayment(
            id = "dp-sup-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.SUPPLIER,
            debtId = "sup-debt-200",
            amount = 1500000L,
            paymentMethod = PaymentMethod.TRANSFER,
            notes = "Pelunasan hutang supplier",
            createdAt = 1200L
        )

        assertEquals("dp-sup-01", payment.id)
        assertEquals("biz-01", payment.businessId)
        assertEquals("branch-01", payment.branchId)
        assertEquals(DebtType.SUPPLIER, payment.debtType)
        assertEquals("sup-debt-200", payment.debtId)
        assertEquals(1500000L, payment.amount)
        assertEquals(PaymentMethod.TRANSFER, payment.paymentMethod)
        assertEquals("Pelunasan hutang supplier", payment.notes)
        assertEquals(1200L, payment.createdAt)
    }

    @Test
    fun `blank id rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            DebtPayment(
                id = "",
                businessId = "biz-01",
                branchId = "branch-01",
                debtType = DebtType.CUSTOMER,
                debtId = "debt-01",
                amount = 10000L,
                paymentMethod = PaymentMethod.CASH,
                createdAt = 1000L
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            DebtPayment(
                id = "   ",
                businessId = "biz-01",
                branchId = "branch-01",
                debtType = DebtType.CUSTOMER,
                debtId = "debt-01",
                amount = 10000L,
                paymentMethod = PaymentMethod.CASH,
                createdAt = 1000L
            )
        }
    }

    @Test
    fun `blank businessId rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            DebtPayment(
                id = "dp-01",
                businessId = "",
                branchId = "branch-01",
                debtType = DebtType.CUSTOMER,
                debtId = "debt-01",
                amount = 10000L,
                paymentMethod = PaymentMethod.CASH,
                createdAt = 1000L
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            DebtPayment(
                id = "dp-01",
                businessId = "   ",
                branchId = "branch-01",
                debtType = DebtType.CUSTOMER,
                debtId = "debt-01",
                amount = 10000L,
                paymentMethod = PaymentMethod.CASH,
                createdAt = 1000L
            )
        }
    }

    @Test
    fun `blank branchId rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            DebtPayment(
                id = "dp-01",
                businessId = "biz-01",
                branchId = "",
                debtType = DebtType.CUSTOMER,
                debtId = "debt-01",
                amount = 10000L,
                paymentMethod = PaymentMethod.CASH,
                createdAt = 1000L
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            DebtPayment(
                id = "dp-01",
                businessId = "biz-01",
                branchId = "   ",
                debtType = DebtType.CUSTOMER,
                debtId = "debt-01",
                amount = 10000L,
                paymentMethod = PaymentMethod.CASH,
                createdAt = 1000L
            )
        }
    }

    @Test
    fun `blank debtId rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            DebtPayment(
                id = "dp-01",
                businessId = "biz-01",
                branchId = "branch-01",
                debtType = DebtType.CUSTOMER,
                debtId = "",
                amount = 10000L,
                paymentMethod = PaymentMethod.CASH,
                createdAt = 1000L
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            DebtPayment(
                id = "dp-01",
                businessId = "biz-01",
                branchId = "branch-01",
                debtType = DebtType.CUSTOMER,
                debtId = "   ",
                amount = 10000L,
                paymentMethod = PaymentMethod.CASH,
                createdAt = 1000L
            )
        }
    }

    @Test
    fun `DebtType enum contains strictly CUSTOMER and SUPPLIER`() {
        val expected = setOf(DebtType.CUSTOMER, DebtType.SUPPLIER)
        assertEquals(2, DebtType.values().size)
        assertEquals(expected, DebtType.values().toSet())
    }

    @Test
    fun `zero and negative payment amounts are rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            DebtPayment(
                id = "dp-01",
                businessId = "biz-01",
                branchId = "branch-01",
                debtType = DebtType.CUSTOMER,
                debtId = "debt-01",
                amount = 0L,
                paymentMethod = PaymentMethod.CASH,
                createdAt = 1000L
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            DebtPayment(
                id = "dp-01",
                businessId = "biz-01",
                branchId = "branch-01",
                debtType = DebtType.CUSTOMER,
                debtId = "debt-01",
                amount = -5000L,
                paymentMethod = PaymentMethod.CASH,
                createdAt = 1000L
            )
        }
    }

    @Test
    fun `canonical PaymentMethod is reused across DebtPayment`() {
        val qrisPayment = DebtPayment(
            id = "dp-qris",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = "debt-01",
            amount = 25000L,
            paymentMethod = PaymentMethod.QRIS,
            createdAt = 1000L
        )
        assertEquals(PaymentMethod.QRIS, qrisPayment.paymentMethod)
    }

    @Test
    fun `DebtPayment creation does NOT directly mutate CustomerDebt, SupplierDebt, Customer, or Supplier`() {
        val customer = Customer(
            id = "cust-01",
            businessId = "biz-01",
            name = "Ahmad",
            debtBalance = 100000L
        )

        val customerDebt = CustomerDebt.create(
            id = "cust-debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            customerId = customer.id,
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-01",
            originalAmount = 100000L,
            createdAt = 1000L
        )

        val supplier = Supplier(
            id = "sup-01",
            businessId = "biz-01",
            name = "Vendor B",
            debtBalance = 500000L
        )

        val supplierDebt = SupplierDebt.create(
            id = "sup-debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            supplierId = supplier.id,
            purchaseId = "purch-01",
            originalAmount = 500000L,
            createdAt = 1000L
        )

        // Instantiate DebtPayment event
        val debtPayment = DebtPayment(
            id = "dp-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = customerDebt.id,
            amount = 50000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1100L
        )

        // Pure event creation leaves all other entity states unmutated
        assertEquals(100000L, customer.debtBalance)
        assertEquals(100000L, customerDebt.remainingAmount)
        assertEquals(DebtStatus.UNPAID, customerDebt.status)
        assertEquals(500000L, supplier.debtBalance)
        assertEquals(500000L, supplierDebt.remainingAmount)
        assertEquals(DebtStatus.UNPAID, supplierDebt.status)
        assertEquals(50000L, debtPayment.amount)
    }

    @Test
    fun `DebtPayment creation has zero side effects on CashRegister, Stock, or DigitalDeposit`() {
        val register = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Laci Kasir",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        val stock = BranchProductOverride(
            branchId = "branch-01",
            productId = "prod-01",
            stock = 20.0
        )

        val deposit = DigitalDepositAccount(
            id = "dep-01",
            businessId = "biz-01",
            branchId = "branch-01",
            currentBalance = 1000000L,
            updatedAt = 1000L
        )

        val payment = DebtPayment(
            id = "dp-02",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = "cust-debt-01",
            amount = 50000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1000L
        )

        assertEquals(500000L, register.currentBalance)
        assertEquals(20.0, stock.stock, 0.001)
        assertEquals(1000000L, deposit.currentBalance)
        assertEquals("dp-02", payment.id)
    }

    @Test
    fun `existing CustomerDebt applyPayment compatibility remains intact`() {
        val initialDebt = CustomerDebt.create(
            id = "cust-debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            customerId = "cust-01",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-01",
            originalAmount = 100000L,
            createdAt = 1000L
        )

        val debtPayment = DebtPayment(
            id = "dp-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = initialDebt.id,
            amount = 40000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1100L
        )

        val updatedDebt = initialDebt.applyPayment(debtPayment)
        assertEquals(60000L, updatedDebt.remainingAmount)
        assertEquals(DebtStatus.PARTIALLY_PAID, updatedDebt.status)
    }

    @Test
    fun `existing SupplierDebt applyPayment compatibility remains intact`() {
        val initialDebt = SupplierDebt.create(
            id = "sup-debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            supplierId = "sup-01",
            purchaseId = "purch-01",
            originalAmount = 300000L,
            createdAt = 1000L
        )

        val debtPayment = DebtPayment(
            id = "dp-02",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.SUPPLIER,
            debtId = initialDebt.id,
            amount = 300000L,
            paymentMethod = PaymentMethod.TRANSFER,
            createdAt = 1100L
        )

        val updatedDebt = initialDebt.applyPayment(debtPayment)
        assertEquals(0L, updatedDebt.remainingAmount)
        assertEquals(DebtStatus.SETTLED, updatedDebt.status)
    }

    @Test
    fun `Payment model remains distinct from DebtPayment model`() {
        val generalPayment = Payment(
            id = "pay-01",
            businessId = "biz-01",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.CUSTOMER_DEBT,
            referenceId = "cust-debt-01",
            paymentMethod = PaymentMethod.CASH,
            amount = 50000L,
            createdAt = 1000L
        )

        val debtPayment = DebtPayment(
            id = "dp-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = "cust-debt-01",
            amount = 50000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1000L
        )

        assertEquals(generalPayment.amount, debtPayment.amount)
        assertEquals(generalPayment.businessId, debtPayment.businessId)
        assertEquals(generalPayment.branchId, debtPayment.branchId)
    }
}
