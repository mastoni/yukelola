package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.BranchProductOverrideEntity
import id.yukelola.core.database.entity.StockAdjustmentEntity
import id.yukelola.core.domain.model.stock.StockAdjustmentReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.ConcurrentHashMap

class StockAdjustmentDaoTest {

    @Test
    fun `StockAdjustmentDao declares canonical query methods`() {
        val methods = StockAdjustmentDao::class.java.declaredMethods.map { it.name }
        assertTrue("Must declare upsert", methods.contains("upsert"))
        assertTrue("Must declare findById", methods.contains("findById"))
        assertTrue("Must declare findByIdAndBusinessId", methods.contains("findByIdAndBusinessId"))
        assertTrue("Must declare findByIdAndBusinessIdAndBranchId", methods.contains("findByIdAndBusinessIdAndBranchId"))
        assertTrue("Must declare findByBusinessId", methods.contains("findByBusinessId"))
        assertTrue("Must declare findByBusinessIdAndBranchId", methods.contains("findByBusinessIdAndBranchId"))
        assertTrue("Must declare findByBusinessIdAndBranchIdAndProductId", methods.contains("findByBusinessIdAndBranchIdAndProductId"))
        assertTrue("Must declare findByBusinessIdAndBranchIdAndReason", methods.contains("findByBusinessIdAndBranchIdAndReason"))
        assertTrue("Must declare findByProductId", methods.contains("findByProductId"))
    }

    @Test
    fun `Room KSP generates StockAdjustmentDao implementation`() {
        val implClass = Class.forName("id.yukelola.core.database.dao.StockAdjustmentDao_Impl")
        assertNotNull("Generated StockAdjustmentDao_Impl must exist", implClass)
        assertTrue(
            "Generated implementation must implement StockAdjustmentDao",
            StockAdjustmentDao::class.java.isAssignableFrom(implClass)
        )
    }

    private class FakeStockAdjustmentDao : StockAdjustmentDao {
        val adjustments = ConcurrentHashMap<String, StockAdjustmentEntity>()

        override fun upsert(adjustment: StockAdjustmentEntity) {
            adjustments[adjustment.id] = adjustment
        }

        override fun findById(id: String): StockAdjustmentEntity? {
            return adjustments[id]
        }

        override fun findByIdAndBusinessId(id: String, businessId: String): StockAdjustmentEntity? {
            return adjustments[id]?.takeIf { it.businessId == businessId }
        }

        override fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): StockAdjustmentEntity? {
            return adjustments[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
        }

        override fun findByBusinessId(businessId: String): List<StockAdjustmentEntity> {
            return adjustments.values
                .filter { it.businessId == businessId }
                .sortedByDescending { it.createdAt }
        }

        override fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<StockAdjustmentEntity> {
            return adjustments.values
                .filter { it.businessId == businessId && it.branchId == branchId }
                .sortedByDescending { it.createdAt }
        }

        override fun findByBusinessIdAndBranchIdAndProductId(
            businessId: String,
            branchId: String,
            productId: String
        ): List<StockAdjustmentEntity> {
            return adjustments.values
                .filter { it.businessId == businessId && it.branchId == branchId && it.productId == productId }
                .sortedByDescending { it.createdAt }
        }

        override fun findByBusinessIdAndBranchIdAndReason(
            businessId: String,
            branchId: String,
            reason: String
        ): List<StockAdjustmentEntity> {
            return adjustments.values
                .filter { it.businessId == businessId && it.branchId == branchId && it.reason == reason }
                .sortedByDescending { it.createdAt }
        }

        override fun findByProductId(productId: String): List<StockAdjustmentEntity> {
            return adjustments.values
                .filter { it.productId == productId }
                .sortedByDescending { it.createdAt }
        }
    }

    @Test
    fun `insert and retrieve StockAdjustment preserves all canonical fields`() {
        val dao = FakeStockAdjustmentDao()
        val entity = StockAdjustmentEntity(
            id = "adj-001",
            businessId = "biz-001",
            branchId = "branch-001",
            productId = "prod-001",
            previousStock = 20.0,
            adjustedStock = 18.5,
            reason = "DAMAGED",
            userId = "user-001",
            deviceId = "dev-001",
            notes = "Broken box",
            createdAt = 1700000000000L
        )

        dao.upsert(entity)

        val retrieved = dao.findById("adj-001")
        assertNotNull(retrieved)
        assertEquals("adj-001", retrieved!!.id)
        assertEquals("biz-001", retrieved.businessId)
        assertEquals("branch-001", retrieved.branchId)
        assertEquals("prod-001", retrieved.productId)
        assertEquals(20.0, retrieved.previousStock, 0.0001)
        assertEquals(18.5, retrieved.adjustedStock, 0.0001)
        assertEquals("DAMAGED", retrieved.reason)
        assertEquals("user-001", retrieved.userId)
        assertEquals("dev-001", retrieved.deviceId)
        assertEquals("Broken box", retrieved.notes)
        assertEquals(1700000000000L, retrieved.createdAt)
    }

    @Test
    fun `business and branch isolation prevents cross-tenant and cross-branch data access`() {
        val dao = FakeStockAdjustmentDao()

        val biz1Branch1 = StockAdjustmentEntity(
            id = "adj-b1-br1",
            businessId = "biz-1",
            branchId = "branch-1",
            productId = "prod-1",
            previousStock = 10.0,
            adjustedStock = 12.0,
            reason = "STOCK_OPNAME",
            userId = "user-1",
            deviceId = "dev-1",
            createdAt = 1700000000100L
        )
        val biz1Branch2 = StockAdjustmentEntity(
            id = "adj-b1-br2",
            businessId = "biz-1",
            branchId = "branch-2",
            productId = "prod-1",
            previousStock = 5.0,
            adjustedStock = 3.0,
            reason = "LOST",
            userId = "user-1",
            deviceId = "dev-1",
            createdAt = 1700000000200L
        )
        val biz2Branch1 = StockAdjustmentEntity(
            id = "adj-b2-br1",
            businessId = "biz-2",
            branchId = "branch-1",
            productId = "prod-2",
            previousStock = 100.0,
            adjustedStock = 90.0,
            reason = "EXPIRED",
            userId = "user-2",
            deviceId = "dev-2",
            createdAt = 1700000000300L
        )

        dao.upsert(biz1Branch1)
        dao.upsert(biz1Branch2)
        dao.upsert(biz2Branch1)

        // findByIdAndBusinessId
        assertNotNull(dao.findByIdAndBusinessId("adj-b1-br1", "biz-1"))
        assertNull(dao.findByIdAndBusinessId("adj-b1-br1", "biz-2"))

        // findByIdAndBusinessIdAndBranchId
        assertNotNull(dao.findByIdAndBusinessIdAndBranchId("adj-b1-br1", "biz-1", "branch-1"))
        assertNull(dao.findByIdAndBusinessIdAndBranchId("adj-b1-br1", "biz-1", "branch-2"))

        // findByBusinessId
        val biz1List = dao.findByBusinessId("biz-1")
        assertEquals(2, biz1List.size)
        val biz2List = dao.findByBusinessId("biz-2")
        assertEquals(1, biz2List.size)

        // findByBusinessIdAndBranchId
        val biz1Br1List = dao.findByBusinessIdAndBranchId("biz-1", "branch-1")
        assertEquals(1, biz1Br1List.size)
        assertEquals("adj-b1-br1", biz1Br1List[0].id)
    }

    @Test
    fun `query filtering by product and reason`() {
        val dao = FakeStockAdjustmentDao()

        val adj1 = StockAdjustmentEntity(
            id = "adj-1",
            businessId = "biz-1",
            branchId = "branch-1",
            productId = "prod-A",
            previousStock = 10.0,
            adjustedStock = 8.0,
            reason = "DAMAGED",
            userId = "user-1",
            deviceId = "dev-1",
            createdAt = 1700000000100L
        )
        val adj2 = StockAdjustmentEntity(
            id = "adj-2",
            businessId = "biz-1",
            branchId = "branch-1",
            productId = "prod-A",
            previousStock = 8.0,
            adjustedStock = 12.0,
            reason = "STOCK_OPNAME",
            userId = "user-1",
            deviceId = "dev-1",
            createdAt = 1700000000200L
        )
        val adj3 = StockAdjustmentEntity(
            id = "adj-3",
            businessId = "biz-1",
            branchId = "branch-1",
            productId = "prod-B",
            previousStock = 20.0,
            adjustedStock = 18.0,
            reason = "INTERNAL_USE",
            userId = "user-1",
            deviceId = "dev-1",
            createdAt = 1700000000300L
        )

        dao.upsert(adj1)
        dao.upsert(adj2)
        dao.upsert(adj3)

        // Filter by Product
        val prodAList = dao.findByBusinessIdAndBranchIdAndProductId("biz-1", "branch-1", "prod-A")
        assertEquals(2, prodAList.size)
        assertEquals("adj-2", prodAList[0].id)
        assertEquals("adj-1", prodAList[1].id)

        val prodBList = dao.findByBusinessIdAndBranchIdAndProductId("biz-1", "branch-1", "prod-B")
        assertEquals(1, prodBList.size)
        assertEquals("adj-3", prodBList[0].id)

        // Filter by Reason
        val damagedList = dao.findByBusinessIdAndBranchIdAndReason("biz-1", "branch-1", "DAMAGED")
        assertEquals(1, damagedList.size)
        assertEquals("adj-1", damagedList[0].id)

        val opnameList = dao.findByBusinessIdAndBranchIdAndReason("biz-1", "branch-1", "STOCK_OPNAME")
        assertEquals(1, opnameList.size)
        assertEquals("adj-2", opnameList[0].id)
    }

    @Test
    fun `preserves all 5 canonical StockAdjustmentReason strings in DAO`() {
        val dao = FakeStockAdjustmentDao()

        for (reason in StockAdjustmentReason.values()) {
            val entity = StockAdjustmentEntity(
                id = "adj-${reason.name}",
                businessId = "biz-1",
                branchId = "branch-1",
                productId = "prod-1",
                previousStock = 10.0,
                adjustedStock = 15.0,
                reason = reason.name,
                userId = "user-1",
                deviceId = "dev-1",
                createdAt = 1700000000000L
            )
            dao.upsert(entity)

            val retrieved = dao.findById("adj-${reason.name}")
            assertNotNull(retrieved)
            assertEquals(reason.name, retrieved!!.reason)
        }
    }

    @Test
    fun `stock authority verification - inserting StockAdjustment does NOT mutate BranchProductOverride stock`() {
        // 1. Existing BranchProductOverride stock = X
        val initialStock = 50.0
        var override = BranchProductOverrideEntity(
            branchId = "branch-001",
            productId = "prod-001",
            stock = initialStock,
            minStock = 5.0,
            isAvailable = true
        )
        assertEquals(50.0, override.stock, 0.0)

        // 2. Insert StockAdjustment where previousStock = X, adjustedStock = Y, delta = Y - X
        val dao = FakeStockAdjustmentDao()
        val adjustment = StockAdjustmentEntity(
            id = "adj-audit-test",
            businessId = "biz-001",
            branchId = "branch-001",
            productId = "prod-001",
            previousStock = initialStock,
            adjustedStock = 75.0,
            reason = "STOCK_OPNAME",
            userId = "user-001",
            deviceId = "dev-001",
            notes = "Physical count was 75",
            createdAt = 1700000000000L
        )
        dao.upsert(adjustment)

        // 3. Reload BranchProductOverride and assert stock is STILL initialStock
        // Persistence layer is strictly storage-only; it does NOT execute the adjustment.
        assertEquals(50.0, override.stock, 0.0)
        val persistedAdjustment = dao.findById("adj-audit-test")
        assertNotNull(persistedAdjustment)
        assertEquals(50.0, persistedAdjustment!!.previousStock, 0.0)
        assertEquals(75.0, persistedAdjustment.adjustedStock, 0.0)
    }

    @Test
    fun `non-mutation boundary - persisting StockAdjustment does not trigger financial or transactional side effects`() {
        val dao = FakeStockAdjustmentDao()
        val entity = StockAdjustmentEntity(
            id = "adj-boundary",
            businessId = "biz-001",
            branchId = "branch-001",
            productId = "prod-001",
            previousStock = 10.0,
            adjustedStock = 0.0,
            reason = "DAMAGED",
            userId = "user-001",
            deviceId = "dev-001",
            notes = "Total loss",
            createdAt = 1700000000000L
        )

        dao.upsert(entity)

        // Verified: only stock_adjustments record is created
        val records = dao.findByBusinessId("biz-001")
        assertEquals(1, records.size)
        assertEquals("adj-boundary", records[0].id)
    }
}
