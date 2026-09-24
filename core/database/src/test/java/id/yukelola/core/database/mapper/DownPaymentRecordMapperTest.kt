package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.DownPaymentRecordEntity
import id.yukelola.core.domain.model.payment.PaymentMethod
import id.yukelola.core.domain.model.serviceorder.DownPaymentRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DownPaymentRecordMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly`() {
        val domain = DownPaymentRecord(
            id = "dp-01",
            businessId = "biz-01",
            branchId = "branch-01",
            orderId = "so-01",
            amount = 50000L,
            paymentMethod = PaymentMethod.QRIS,
            notes = "QRIS payment confirmed",
            createdAt = 1700000000000L
        )

        val entity = domain.toEntity()

        assertEquals("dp-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("so-01", entity.orderId)
        assertEquals(50000L, entity.amount)
        assertEquals("QRIS", entity.paymentMethod)
        assertEquals("QRIS payment confirmed", entity.notes)
        assertEquals(1700000000000L, entity.createdAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly with nullable notes null`() {
        val entity = DownPaymentRecordEntity(
            id = "dp-02",
            businessId = "biz-02",
            branchId = "branch-02",
            orderId = "so-02",
            amount = 100000L,
            paymentMethod = "TRANSFER",
            notes = null,
            createdAt = 1700000000000L
        )

        val domain = entity.toDomain()

        assertEquals("dp-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals("so-02", domain.orderId)
        assertEquals(100000L, domain.amount)
        assertEquals(PaymentMethod.TRANSFER, domain.paymentMethod)
        assertNull(domain.notes)
        assertEquals(1700000000000L, domain.createdAt)
    }

    @Test
    fun `full round trip preserves exact domain identity and precision`() {
        val original = DownPaymentRecord(
            id = "dp-03",
            businessId = "biz-03",
            branchId = "branch-03",
            orderId = "so-03",
            amount = 75000L,
            paymentMethod = PaymentMethod.CASH,
            notes = "Cash at cashier",
            createdAt = 1700000000000L
        )

        val entity = original.toEntity()
        val restored = entity.toDomain()

        assertEquals(original, restored)
    }
}
