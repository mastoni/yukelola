package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.DebtPaymentEntity
import id.yukelola.core.domain.model.debt.DebtPayment
import id.yukelola.core.domain.model.debt.DebtType
import id.yukelola.core.domain.model.payment.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DebtPaymentMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly with notes populated`() {
        val domain = DebtPayment(
            id = "dp-cust-01",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = "cust-debt-01",
            amount = 75000L,
            paymentMethod = PaymentMethod.CASH,
            notes = "Cicilan kasbon pelanggan",
            createdAt = 1700000000000L
        )

        val entity = domain.toEntity()

        assertEquals("dp-cust-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("CUSTOMER", entity.debtType)
        assertEquals("cust-debt-01", entity.debtId)
        assertEquals(75000L, entity.amount)
        assertEquals("CASH", entity.paymentMethod)
        assertEquals("Cicilan kasbon pelanggan", entity.notes)
        assertEquals(1700000000000L, entity.createdAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly with notes null`() {
        val entity = DebtPaymentEntity(
            id = "dp-sup-02",
            businessId = "biz-02",
            branchId = "branch-02",
            debtType = "SUPPLIER",
            debtId = "sup-debt-02",
            amount = 1500000L,
            paymentMethod = "TRANSFER",
            notes = null,
            createdAt = 1700000000000L
        )

        val domain = entity.toDomain()

        assertEquals("dp-sup-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals(DebtType.SUPPLIER, domain.debtType)
        assertEquals("sup-debt-02", domain.debtId)
        assertEquals(1500000L, domain.amount)
        assertEquals(PaymentMethod.TRANSFER, domain.paymentMethod)
        assertNull(domain.notes)
        assertEquals(1700000000000L, domain.createdAt)
    }

    @Test
    fun `full round trip preserves exact domain identity and precision for CUSTOMER and SUPPLIER`() {
        val customerPayment = DebtPayment(
            id = "dp-round-01",
            businessId = "biz-03",
            branchId = "branch-03",
            debtType = DebtType.CUSTOMER,
            debtId = "cust-debt-03",
            amount = 25000L,
            paymentMethod = PaymentMethod.QRIS,
            notes = "QRIS bayar hutang",
            createdAt = 1700000000000L
        )

        val supplierPayment = DebtPayment(
            id = "dp-round-02",
            businessId = "biz-03",
            branchId = "branch-03",
            debtType = DebtType.SUPPLIER,
            debtId = "sup-debt-03",
            amount = 5000000L,
            paymentMethod = PaymentMethod.OTHER,
            notes = "Giro bilyet",
            createdAt = 1700000000000L
        )

        assertEquals(customerPayment, customerPayment.toEntity().toDomain())
        assertEquals(supplierPayment, supplierPayment.toEntity().toDomain())
    }
}
