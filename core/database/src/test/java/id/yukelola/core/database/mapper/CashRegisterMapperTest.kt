package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.CashRegisterEntity
import id.yukelola.core.domain.model.cash.CashRegister
import org.junit.Assert.assertEquals
import org.junit.Test

class CashRegisterMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly`() {
        val domain = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Main Cash Drawer",
            currentBalance = 750000L,
            updatedAt = 1700000000000L
        )

        val entity = domain.toEntity()

        assertEquals("reg-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("Main Cash Drawer", entity.name)
        assertEquals(750000L, entity.currentBalance)
        assertEquals(1700000000000L, entity.updatedAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly`() {
        val entity = CashRegisterEntity(
            id = "reg-02",
            businessId = "biz-02",
            branchId = "branch-02",
            name = "Secondary Drawer",
            currentBalance = 0L,
            updatedAt = 1710000000000L
        )

        val domain = entity.toDomain()

        assertEquals("reg-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals("Secondary Drawer", domain.name)
        assertEquals(0L, domain.currentBalance)
        assertEquals(1710000000000L, domain.updatedAt)
    }

    @Test
    fun `full round trip preserves exact domain identity and Long monetary precision`() {
        val original = CashRegister(
            id = "reg-03",
            businessId = "biz-03",
            branchId = "branch-03",
            name = "Kasir Utama",
            currentBalance = 12500000L,
            updatedAt = 1720000000000L
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }
}
