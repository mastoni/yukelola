package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.StockAdjustmentEntity
import id.yukelola.core.domain.model.stock.StockAdjustment
import id.yukelola.core.domain.model.stock.StockAdjustmentReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StockAdjustmentMapperTest {

    @Test
    fun `toEntity maps all domain fields losslessly`() {
        val domain = StockAdjustment(
            id = "adj-001",
            businessId = "biz-001",
            branchId = "branch-001",
            productId = "prod-001",
            previousStock = 15.5,
            adjustedStock = 20.0,
            reason = StockAdjustmentReason.STOCK_OPNAME,
            userId = "user-001",
            deviceId = "dev-001",
            notes = "Monthly physical count reconciliation",
            createdAt = 1700000000000L
        )

        val entity = StockAdjustmentMapper.toEntity(domain)

        assertEquals("adj-001", entity.id)
        assertEquals("biz-001", entity.businessId)
        assertEquals("branch-001", entity.branchId)
        assertEquals("prod-001", entity.productId)
        assertEquals(15.5, entity.previousStock, 0.0001)
        assertEquals(20.0, entity.adjustedStock, 0.0001)
        assertEquals("STOCK_OPNAME", entity.reason)
        assertEquals("user-001", entity.userId)
        assertEquals("dev-001", entity.deviceId)
        assertEquals("Monthly physical count reconciliation", entity.notes)
        assertEquals(1700000000000L, entity.createdAt)
    }

    @Test
    fun `toDomain maps all entity fields losslessly`() {
        val entity = StockAdjustmentEntity(
            id = "adj-002",
            businessId = "biz-001",
            branchId = "branch-002",
            productId = "prod-002",
            previousStock = 50.0,
            adjustedStock = 45.25,
            reason = "DAMAGED",
            userId = "user-002",
            deviceId = "dev-002",
            notes = "Broken during transport",
            createdAt = 1700000001000L
        )

        val domain = StockAdjustmentMapper.toDomain(entity)

        assertEquals("adj-002", domain.id)
        assertEquals("biz-001", domain.businessId)
        assertEquals("branch-002", domain.branchId)
        assertEquals("prod-002", domain.productId)
        assertEquals(50.0, domain.previousStock, 0.0001)
        assertEquals(45.25, domain.adjustedStock, 0.0001)
        assertEquals(-4.75, domain.deltaQuantity, 0.0001)
        assertEquals(StockAdjustmentReason.DAMAGED, domain.reason)
        assertEquals("user-002", domain.userId)
        assertEquals("dev-002", domain.deviceId)
        assertEquals("Broken during transport", domain.notes)
        assertEquals(1700000001000L, domain.createdAt)
    }

    @Test
    fun `preserves all 5 canonical StockAdjustmentReason values`() {
        val allReasons = StockAdjustmentReason.values()
        assertEquals(5, allReasons.size)

        for (reason in allReasons) {
            val domain = StockAdjustment(
                id = "adj-reason-${reason.name}",
                businessId = "biz-001",
                branchId = "branch-001",
                productId = "prod-001",
                previousStock = 10.0,
                adjustedStock = 12.0,
                reason = reason,
                userId = "user-001",
                deviceId = "dev-001",
                notes = "Reason test for $reason",
                createdAt = 1700000000000L
            )

            val entity = StockAdjustmentMapper.toEntity(domain)
            assertEquals(reason.name, entity.reason)

            val roundTrip = StockAdjustmentMapper.toDomain(entity)
            assertEquals(reason, roundTrip.reason)
        }
    }

    @Test
    fun `preserves null notes correctly`() {
        val domain = StockAdjustment(
            id = "adj-null-notes",
            businessId = "biz-001",
            branchId = "branch-001",
            productId = "prod-001",
            previousStock = 10.0,
            adjustedStock = 8.0,
            reason = StockAdjustmentReason.LOST,
            userId = "user-001",
            deviceId = "dev-001",
            notes = null,
            createdAt = 1700000000000L
        )

        val entity = StockAdjustmentMapper.toEntity(domain)
        assertNull(entity.notes)

        val roundTrip = StockAdjustmentMapper.toDomain(entity)
        assertNull(roundTrip.notes)
    }

    @Test
    fun `preserves floating point double quantity precision exactly`() {
        val domain = StockAdjustment(
            id = "adj-precision",
            businessId = "biz-001",
            branchId = "branch-001",
            productId = "prod-001",
            previousStock = 12345.6789,
            adjustedStock = 12345.1234,
            reason = StockAdjustmentReason.EXPIRED,
            userId = "user-001",
            deviceId = "dev-001",
            notes = "Precision test",
            createdAt = 1700000000000L
        )

        val entity = StockAdjustmentMapper.toEntity(domain)
        val roundTrip = StockAdjustmentMapper.toDomain(entity)

        assertEquals(12345.6789, roundTrip.previousStock, 0.0)
        assertEquals(12345.1234, roundTrip.adjustedStock, 0.0)
        assertEquals(12345.1234 - 12345.6789, roundTrip.deltaQuantity, 0.0000001)
    }
}
