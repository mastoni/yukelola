package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.SupplierDebtEntity
import id.yukelola.core.domain.model.debt.DebtStatus
import id.yukelola.core.domain.model.debt.SupplierDebt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SupplierDebtMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly with dueDate populated`() {
        val domain = SupplierDebt(
            id = "sup-debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            supplierId = "sup-01",
            purchaseId = "purch-01",
            originalAmount = 3000000L,
            remainingAmount = 1500000L,
            status = DebtStatus.PARTIALLY_PAID,
            dueDate = 1750000000000L,
            createdAt = 1700000000000L
        )

        val entity = domain.toEntity()

        assertEquals("sup-debt-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("sup-01", entity.supplierId)
        assertEquals("purch-01", entity.purchaseId)
        assertEquals(3000000L, entity.originalAmount)
        assertEquals(1500000L, entity.remainingAmount)
        assertEquals("PARTIALLY_PAID", entity.status)
        assertEquals(1750000000000L, entity.dueDate)
        assertEquals(1700000000000L, entity.createdAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly with dueDate null`() {
        val entity = SupplierDebtEntity(
            id = "sup-debt-02",
            businessId = "biz-02",
            branchId = "branch-02",
            supplierId = "sup-02",
            purchaseId = "purch-02",
            originalAmount = 5000000L,
            remainingAmount = 5000000L,
            status = "UNPAID",
            dueDate = null,
            createdAt = 1700000000000L
        )

        val domain = entity.toDomain()

        assertEquals("sup-debt-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals("sup-02", domain.supplierId)
        assertEquals("purch-02", domain.purchaseId)
        assertEquals(5000000L, domain.originalAmount)
        assertEquals(5000000L, domain.remainingAmount)
        assertEquals(DebtStatus.UNPAID, domain.status)
        assertNull(domain.dueDate)
        assertEquals(1700000000000L, domain.createdAt)
    }

    @Test
    fun `full round trip preserves exact domain identity and precision`() {
        val original = SupplierDebt(
            id = "sup-debt-03",
            businessId = "biz-03",
            branchId = "branch-03",
            supplierId = "sup-03",
            purchaseId = "purch-03",
            originalAmount = 10000000L,
            remainingAmount = 0L,
            status = DebtStatus.SETTLED,
            dueDate = 1760000000000L,
            createdAt = 1700000000000L
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }
}
