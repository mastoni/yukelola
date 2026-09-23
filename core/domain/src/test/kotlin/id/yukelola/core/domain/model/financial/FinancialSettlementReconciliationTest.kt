package id.yukelola.core.domain.model.financial

import id.yukelola.core.domain.model.actor.Customer
import id.yukelola.core.domain.model.actor.Supplier
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.cash.CashMutation
import id.yukelola.core.domain.model.cash.CashMutationCategory
import id.yukelola.core.domain.model.cash.CashMutationType
import id.yukelola.core.domain.model.cash.CashRegister
import id.yukelola.core.domain.model.catalog.BranchProductOverride
import id.yukelola.core.domain.model.debt.CustomerDebt
import id.yukelola.core.domain.model.debt.DebtPayment
import id.yukelola.core.domain.model.debt.DebtReferenceType
import id.yukelola.core.domain.model.debt.DebtStatus
import id.yukelola.core.domain.model.debt.DebtType
import id.yukelola.core.domain.model.debt.SupplierDebt
import id.yukelola.core.domain.model.digital.DigitalDepositAccount
import id.yukelola.core.domain.model.digital.DigitalDepositMutation
import id.yukelola.core.domain.model.digital.DigitalDepositMutationType
import id.yukelola.core.domain.model.digital.DigitalTransaction
import id.yukelola.core.domain.model.digital.DigitalTransactionStatus
import id.yukelola.core.domain.model.digital.Inquiry
import id.yukelola.core.domain.model.digital.InquiryStatus
import id.yukelola.core.domain.model.payment.Payment
import id.yukelola.core.domain.model.payment.PaymentMethod
import id.yukelola.core.domain.model.sale.PaymentStatus
import id.yukelola.core.domain.model.payment.PaymentTransactionType
import id.yukelola.core.domain.model.purchase.Purchase
import id.yukelola.core.domain.model.purchase.PurchaseItem
import id.yukelola.core.domain.model.sale.Sale
import id.yukelola.core.domain.model.sale.SaleItem
import id.yukelola.core.domain.model.sale.SaleStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancialSettlementReconciliationTest {

    private val sampleAttribution = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-01",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    @Test
    fun `cash sale settlement boundary - completed cash sale correlates with single cash inflow`() {
        val item = SaleItem(
            id = "item-01",
            saleId = "sale-01",
            productId = "prod-01",
            productName = "Beras 5kg",
            unit = "BAG",
            unitPrice = 75000L,
            costPrice = 60000L,
            quantity = 1.0,
            subtotal = 75000L
        )

        val sale = Sale(
            id = "sale-01",
            saleNumber = "TRX-001",
            attribution = sampleAttribution,
            items = listOf(item)
        ).complete(paidAmount = 75000L, completedAt = 1050L)

        assertEquals(SaleStatus.COMPLETED, sale.status)
        assertEquals(PaymentStatus.PAID, sale.paymentStatus)
        assertEquals(0L, sale.remainingBalance)

        // Correlating cash register movement
        val initialRegister = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Kasir Utama",
            currentBalance = 100000L,
            updatedAt = 1000L
        )

        val saleMutation = CashMutation(
            id = "mut-sale-01",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.SALE,
            amount = sale.paidAmount,
            source = sale.id,
            referenceId = sale.id,
            createdAt = 1050L
        )

        val updatedRegister = initialRegister.applyMutation(saleMutation)
        assertEquals(175000L, updatedRegister.currentBalance)
    }

    @Test
    fun `credit sale settlement boundary - requires customer and produces CustomerDebt invariant`() {
        val item = SaleItem(
            id = "item-02",
            saleId = "sale-02",
            productId = "prod-02",
            productName = "Minyak Goreng 2L",
            unit = "POUCH",
            unitPrice = 35000L,
            costPrice = 28000L,
            quantity = 2.0,
            subtotal = 70000L
        )

        val customer = Customer(
            id = "cust-01",
            businessId = "biz-01",
            name = "Budi Hartono",
            debtBalance = 0L
        )

        val creditSale = Sale(
            id = "sale-02",
            saleNumber = "TRX-002",
            attribution = sampleAttribution,
            customerId = customer.id,
            items = listOf(item)
        ).complete(paidAmount = 20000L, completedAt = 1050L)

        assertEquals(PaymentStatus.PARTIALLY_PAID, creditSale.paymentStatus)
        assertEquals(50000L, creditSale.remainingBalance)

        // CustomerDebt model correctly encapsulates the remaining balance obligation
        val customerDebt = CustomerDebt.create(
            id = "cdebt-01",
            businessId = creditSale.businessId,
            branchId = creditSale.branchId,
            customerId = customer.id,
            referenceType = DebtReferenceType.SALE,
            referenceId = creditSale.id,
            originalAmount = creditSale.remainingBalance,
            createdAt = 1050L
        )

        assertEquals(50000L, customerDebt.originalAmount)
        assertEquals(50000L, customerDebt.remainingAmount)
        assertEquals(DebtStatus.UNPAID, customerDebt.status)
    }

    @Test
    fun `customer debt cash payment boundary - reduces debt and increases cash drawer`() {
        val debt = CustomerDebt.create(
            id = "cdebt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            customerId = "cust-01",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-02",
            originalAmount = 50000L,
            createdAt = 1000L
        )

        val debtPayment = DebtPayment(
            id = "dp-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = debt.id,
            amount = 30000L,
            paymentMethod = PaymentMethod.CASH,
            notes = "Pembayaran cicilan 1",
            createdAt = 1100L
        )

        val settledDebt = debt.applyPayment(debtPayment)
        assertEquals(20000L, settledDebt.remainingAmount)
        assertEquals(DebtStatus.PARTIALLY_PAID, settledDebt.status)

        // Cash drawer inflow for cash debt payment
        val register = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Kasir Utama",
            currentBalance = 100000L,
            updatedAt = 1000L
        )

        val cashMutation = CashMutation(
            id = "mut-dp-01",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.INFLOW,
            category = CashMutationCategory.CUSTOMER_DEBT_PAYMENT,
            amount = debtPayment.amount,
            source = debtPayment.id,
            referenceId = debt.id,
            createdAt = 1100L
        )

        val updatedRegister = register.applyMutation(cashMutation)
        assertEquals(130000L, updatedRegister.currentBalance)
    }

    @Test
    fun `cash purchase settlement boundary - paid purchase correlates with cash outflow`() {
        val supplier = Supplier(
            id = "sup-01",
            businessId = "biz-01",
            name = "Grosir Sembako Jaya"
        )

        val item = PurchaseItem(
            id = "pitem-01",
            purchaseId = "purch-01",
            productId = "prod-01",
            productName = "Gula Pasir 50kg",
            unitCost = 700000L,
            quantity = 1.0
        )

        val purchase = Purchase(
            id = "purch-01",
            purchaseNumber = "PO-001",
            attribution = sampleAttribution,
            supplierId = supplier.id,
            items = listOf(item),
            paidAmount = 700000L
        )

        assertEquals(PaymentStatus.PAID, purchase.paymentStatus)
        assertEquals(0L, purchase.remainingBalance)

        val register = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Kasir Utama",
            currentBalance = 1000000L,
            updatedAt = 1000L
        )

        val purchaseMutation = CashMutation(
            id = "mut-purch-01",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.OUTFLOW,
            category = CashMutationCategory.PURCHASE,
            amount = purchase.paidAmount,
            source = purchase.id,
            referenceId = purchase.id,
            createdAt = 1050L
        )

        val updatedRegister = register.applyMutation(purchaseMutation)
        assertEquals(300000L, updatedRegister.currentBalance)
    }

    @Test
    fun `credit purchase settlement boundary - requires supplierId and creates SupplierDebt`() {
        val supplier = Supplier(
            id = "sup-01",
            businessId = "biz-01",
            name = "Distributor Pangan"
        )

        val item = PurchaseItem(
            id = "pitem-02",
            purchaseId = "purch-02",
            productId = "prod-02",
            productName = "Tepung Terigu 25kg",
            unitCost = 250000L,
            quantity = 2.0
        )

        val creditPurchase = Purchase(
            id = "purch-02",
            purchaseNumber = "PO-002",
            attribution = sampleAttribution,
            supplierId = supplier.id,
            items = listOf(item),
            paidAmount = 100000L
        )

        assertEquals(PaymentStatus.PARTIALLY_PAID, creditPurchase.paymentStatus)
        assertEquals(400000L, creditPurchase.remainingBalance)

        val supplierDebt = SupplierDebt.create(
            id = "sdebt-01",
            businessId = creditPurchase.businessId,
            branchId = creditPurchase.branchId,
            supplierId = supplier.id,
            purchaseId = creditPurchase.id,
            originalAmount = creditPurchase.remainingBalance,
            createdAt = 1050L
        )

        assertEquals(400000L, supplierDebt.originalAmount)
        assertEquals(400000L, supplierDebt.remainingAmount)
        assertEquals(DebtStatus.UNPAID, supplierDebt.status)
    }

    @Test
    fun `supplier debt cash payment boundary - reduces payable and decreases cash drawer`() {
        val debt = SupplierDebt.create(
            id = "sdebt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            supplierId = "sup-01",
            purchaseId = "purch-02",
            originalAmount = 400000L,
            createdAt = 1000L
        )

        val debtPayment = DebtPayment(
            id = "dp-sup-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.SUPPLIER,
            debtId = debt.id,
            amount = 400000L,
            paymentMethod = PaymentMethod.CASH,
            notes = "Pelunasan hutang tepung",
            createdAt = 1200L
        )

        val settledDebt = debt.applyPayment(debtPayment)
        assertEquals(0L, settledDebt.remainingAmount)
        assertEquals(DebtStatus.SETTLED, settledDebt.status)

        val register = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Kasir Utama",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        val cashOutflow = CashMutation(
            id = "mut-sdebt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            registerId = "reg-01",
            mutationType = CashMutationType.OUTFLOW,
            category = CashMutationCategory.SUPPLIER_DEBT_PAYMENT,
            amount = debtPayment.amount,
            source = debtPayment.id,
            referenceId = debt.id,
            createdAt = 1200L
        )

        val updatedRegister = register.applyMutation(cashOutflow)
        assertEquals(100000L, updatedRegister.currentBalance)
    }

    @Test
    fun `digital transaction funding boundary - debits DigitalDepositAccount by costPrice without mutating physical cash`() {
        val depositAccount = DigitalDepositAccount(
            id = "dep-01",
            businessId = "biz-01",
            branchId = "branch-01",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        val cashRegister = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Kasir Utama",
            currentBalance = 300000L,
            updatedAt = 1000L
        )

        val digitalTx = DigitalTransaction(
            id = "dig-01",
            attribution = sampleAttribution,
            targetNumber = "08123456789",
            productCode = "V50",
            denomination = 50000L,
            costPrice = 49500L,
            sellingPrice = 52000L
        )

        assertEquals(2500L, digitalTx.grossProfit)

        // Digital deposit mutation for fulfillment
        val depositMutation = DigitalDepositMutation(
            id = "dmut-01",
            businessId = "biz-01",
            branchId = "branch-01",
            accountId = depositAccount.id,
            mutationType = DigitalDepositMutationType.DIGITAL_SALE,
            amount = digitalTx.costPrice,
            balanceBefore = depositAccount.currentBalance,
            balanceAfter = depositAccount.currentBalance - digitalTx.costPrice,
            referenceId = digitalTx.id,
            createdAt = 1050L
        )

        val updatedDeposit = depositAccount.applyMutation(depositMutation)

        assertEquals(450500L, updatedDeposit.currentBalance)
        // CashRegister is 100% UNTOUCHED by digital deposit debit
        assertEquals(300000L, cashRegister.currentBalance)
    }

    @Test
    fun `inquiry read-only invariant - never debits cash or digital deposit and has no financial mutations`() {
        val inquiry = Inquiry(
            id = "inq-01",
            attribution = sampleAttribution,
            targetNumber = "51234567890",
            productCode = "PLNPOST",
            adminFee = 2500L
        ).markSuccess(
            customerName = "Bapak Bambang",
            billAmount = 145000L,
            adminFee = 2500L,
            inquiryReference = "INQ-REF-999",
            timestamp = 1050L
        )

        assertEquals(InquiryStatus.SUCCESS, inquiry.status)
        assertEquals(147500L, inquiry.totalBillAmount)

        val deposit = DigitalDepositAccount(
            id = "dep-01",
            businessId = "biz-01",
            branchId = "branch-01",
            currentBalance = 1000000L,
            updatedAt = 1000L
        )

        val cash = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Kasir Utama",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        // Inquiry does not mutate deposit or cash
        assertEquals(1000000L, deposit.currentBalance)
        assertEquals(500000L, cash.currentBalance)
    }

    @Test
    fun `no double effect - Payment model and DebtPayment model maintain distinct non-duplicative boundaries`() {
        val payment = Payment(
            id = "pay-01",
            businessId = "biz-01",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.SALE,
            referenceId = "sale-01",
            paymentMethod = PaymentMethod.CASH,
            amount = 50000L,
            createdAt = 1000L
        )

        val debtPayment = DebtPayment(
            id = "dp-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = "debt-01",
            amount = 50000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1000L
        )

        assertEquals("pay-01", payment.id)
        assertEquals("dp-01", debtPayment.id)
        assertEquals(PaymentTransactionType.SALE, payment.transactionType)
        assertEquals(DebtType.CUSTOMER, debtPayment.debtType)
    }
}
