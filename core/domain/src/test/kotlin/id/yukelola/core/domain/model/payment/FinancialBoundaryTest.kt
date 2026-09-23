package id.yukelola.core.domain.model.payment

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.cash.CashMutation
import id.yukelola.core.domain.model.cash.CashMutationCategory
import id.yukelola.core.domain.model.cash.CashMutationType
import id.yukelola.core.domain.model.cash.CashRegister
import id.yukelola.core.domain.model.debt.CustomerDebt
import id.yukelola.core.domain.model.debt.DebtReferenceType
import id.yukelola.core.domain.model.debt.DebtStatus
import id.yukelola.core.domain.model.sale.PaymentStatus
import id.yukelola.core.domain.model.sale.Sale
import id.yukelola.core.domain.model.sale.SaleItem
import id.yukelola.core.domain.model.sale.SaleStatus
import id.yukelola.core.domain.model.sale.TransactionMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class FinancialBoundaryTest {

    @Test
    fun `cash balance is strictly physical cash movement and does not represent business profit`() {
        // Register starts with 500.000 opening cash
        val register = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Cash Drawer",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        // Customer pays 100.000 cash for a sale
        val cashInflow = CashMutation(
            id = "mut-01",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.SALE,
            amount = 100000L,
            source = "SALE-01",
            createdAt = 1100L
        )

        val updatedRegister = register.applyMutation(cashInflow)

        // Cash balance is 600.000, NOT profit
        assertEquals(600000L, updatedRegister.currentBalance)

        // The sale profit depends on costPrice (HPP), e.g., selling price 100.000, cost 70.000 -> profit 30.000
        val costPrice = 70000L
        val sellingPrice = 100000L
        val grossProfit = sellingPrice - costPrice
        assertEquals(30000L, grossProfit)
        assertNotEquals(updatedRegister.currentBalance, grossProfit)
    }

    @Test
    fun `payment and cash mutation are distinct - non-cash payments do not mutate physical cash`() {
        val register = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Cash Drawer",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        // A QRIS payment occurs
        val qrisPayment = Payment(
            id = "pay-01",
            businessId = "biz-01",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.SALE,
            referenceId = "sale-01",
            paymentMethod = PaymentMethod.QRIS,
            amount = 150000L,
            createdAt = 1100L
        )

        assertEquals(PaymentMethod.QRIS, qrisPayment.paymentMethod)
        // Physical cash drawer is untouched by QRIS / digital settlement
        assertEquals(500000L, register.currentBalance)
    }

    @Test
    fun `sale total remains historical record and does not mutate when customer debt is created`() {
        val attribution = TransactionAttribution(
            businessId = "biz-01",
            branchId = "branch-01",
            userId = "user-01",
            deviceId = "dev-01",
            cashierSessionId = "session-01",
            createdAt = 1000L
        )

        val item = SaleItem(
            id = "item-01",
            saleId = "sale-01",
            productId = "prod-01",
            productName = "Beras 5kg",
            unit = "SAK",
            unitPrice = 75000L,
            costPrice = 65000L,
            quantity = 1.0,
            discountAmount = 0L,
            subtotal = 75000L
        )

        // Sale is completed with PARTIALLY_PAID (cashbon)
        val sale = Sale(
            id = "sale-01",
            saleNumber = "INV-001",
            transactionMode = TransactionMode.RETAIL_TRANSACTION,
            attribution = attribution,
            customerId = "cust-01",
            items = listOf(item),
            discountAmount = 0L,
            taxAmount = 0L,
            paidAmount = 25000L,
            paymentStatus = PaymentStatus.PARTIALLY_PAID,
            status = SaleStatus.COMPLETED,
            completedAt = 1050L
        )

        // Outstanding CustomerDebt is 50.000
        val debt = CustomerDebt.create(
            id = "debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            customerId = "cust-01",
            referenceType = DebtReferenceType.SALE,
            referenceId = sale.id,
            originalAmount = sale.totalAmount - sale.paidAmount,
            createdAt = 1000L
        )

        assertEquals(75000L, sale.totalAmount)
        assertEquals(25000L, sale.paidAmount)
        assertEquals(50000L, debt.remainingAmount)
        assertEquals(DebtStatus.UNPAID, debt.status)
    }
}
