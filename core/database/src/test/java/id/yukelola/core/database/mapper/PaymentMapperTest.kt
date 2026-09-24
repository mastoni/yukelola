package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.PaymentEntity
import id.yukelola.core.domain.model.payment.Payment
import id.yukelola.core.domain.model.payment.PaymentMethod
import id.yukelola.core.domain.model.payment.PaymentTransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class PaymentMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly`() {
        val domain = Payment(
            id = "pay-01",
            businessId = "biz-01",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.SALE,
            referenceId = "sale-01",
            paymentMethod = PaymentMethod.CASH,
            amount = 125000L,
            createdAt = 1700000000000L
        )

        val entity = domain.toEntity()

        assertEquals("pay-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("SALE", entity.transactionType)
        assertEquals("sale-01", entity.referenceId)
        assertEquals("CASH", entity.paymentMethod)
        assertEquals(125000L, entity.amount)
        assertEquals(1700000000000L, entity.createdAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly`() {
        val entity = PaymentEntity(
            id = "pay-02",
            businessId = "biz-02",
            branchId = "branch-02",
            transactionType = "SERVICE_ORDER",
            referenceId = "order-01",
            paymentMethod = "QRIS",
            amount = 500000L,
            createdAt = 1700000000000L
        )

        val domain = entity.toDomain()

        assertEquals("pay-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals(PaymentTransactionType.SERVICE_ORDER, domain.transactionType)
        assertEquals("order-01", domain.referenceId)
        assertEquals(PaymentMethod.QRIS, domain.paymentMethod)
        assertEquals(500000L, domain.amount)
        assertEquals(1700000000000L, domain.createdAt)
    }

    @Test
    fun `full round trip preserves exact domain identity and precision across transaction types and payment methods`() {
        val saleCash = Payment(
            id = "pay-round-01",
            businessId = "biz-01",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.SALE,
            referenceId = "sale-100",
            paymentMethod = PaymentMethod.CASH,
            amount = 75000L,
            createdAt = 1700000000000L
        )

        val purchaseTransfer = Payment(
            id = "pay-round-02",
            businessId = "biz-01",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.PURCHASE,
            referenceId = "purch-200",
            paymentMethod = PaymentMethod.TRANSFER,
            amount = 3500000L,
            createdAt = 1700000000000L
        )

        val customerDebtDebt = Payment(
            id = "pay-round-03",
            businessId = "biz-01",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.CUSTOMER_DEBT,
            referenceId = "cdebt-300",
            paymentMethod = PaymentMethod.DEBT,
            amount = 150000L,
            createdAt = 1700000000000L
        )

        val supplierDebtOther = Payment(
            id = "pay-round-04",
            businessId = "biz-01",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.SUPPLIER_DEBT,
            referenceId = "sdebt-400",
            paymentMethod = PaymentMethod.OTHER,
            amount = 890000L,
            createdAt = 1700000000000L
        )

        assertEquals(saleCash, saleCash.toEntity().toDomain())
        assertEquals(purchaseTransfer, purchaseTransfer.toEntity().toDomain())
        assertEquals(customerDebtDebt, customerDebtDebt.toEntity().toDomain())
        assertEquals(supplierDebtOther, supplierDebtOther.toEntity().toDomain())
    }
}
