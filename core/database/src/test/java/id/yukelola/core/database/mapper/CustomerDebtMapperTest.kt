package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.CustomerDebtEntity
import id.yukelola.core.domain.model.debt.DebtReferenceType
import id.yukelola.core.domain.model.debt.DebtStatus
import id.yukelola.core.domain.model.debt.CustomerDebt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CustomerDebtMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly with dueDate populated`() {
        val domain = CustomerDebt(
            id = "cust-debt-01",
            businessId = "biz-01",
            branchId = "branch-01",
            customerId = "cust-01",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-01",
            originalAmount = 150000L,
            remainingAmount = 75000L,
            status = DebtStatus.PARTIALLY_PAID,
            dueDate = 1750000000000L,
            createdAt = 1700000000000L
        )

        val entity = domain.toEntity()

        assertEquals("cust-debt-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("cust-01", entity.customerId)
        assertEquals("SALE", entity.referenceType)
        assertEquals("sale-01", entity.referenceId)
        assertEquals(150000L, entity.originalAmount)
        assertEquals(75000L, entity.remainingAmount)
        assertEquals("PARTIALLY_PAID", entity.status)
        assertEquals(1750000000000L, entity.dueDate)
        assertEquals(1700000000000L, entity.createdAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly with dueDate null`() {
        val entity = CustomerDebtEntity(
            id = "cust-debt-02",
            businessId = "biz-02",
            branchId = "branch-02",
            customerId = "cust-02",
            referenceType = "SERVICE_ORDER",
            referenceId = "srv-02",
            originalAmount = 300000L,
            remainingAmount = 300000L,
            status = "UNPAID",
            dueDate = null,
            createdAt = 1700000000000L
        )

        val domain = entity.toDomain()

        assertEquals("cust-debt-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals("cust-02", domain.customerId)
        assertEquals(DebtReferenceType.SERVICE_ORDER, domain.referenceType)
        assertEquals("srv-02", domain.referenceId)
        assertEquals(300000L, domain.originalAmount)
        assertEquals(300000L, domain.remainingAmount)
        assertEquals(DebtStatus.UNPAID, domain.status)
        assertNull(domain.dueDate)
        assertEquals(1700000000000L, domain.createdAt)
    }

    @Test
    fun `full round trip preserves exact domain identity and precision`() {
        val original = CustomerDebt(
            id = "cust-debt-03",
            businessId = "biz-03",
            branchId = "branch-03",
            customerId = "cust-03",
            referenceType = DebtReferenceType.SALE,
            referenceId = "sale-03",
            originalAmount = 500000L,
            remainingAmount = 0L,
            status = DebtStatus.SETTLED,
            dueDate = 1760000000000L,
            createdAt = 1700000000000L
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }
}
