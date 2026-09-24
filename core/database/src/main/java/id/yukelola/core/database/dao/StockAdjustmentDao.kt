package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.StockAdjustmentEntity

/**
 * Room Data Access Object for [StockAdjustmentEntity].
 * Provides deterministic persistence operations for immutable stock adjustment audit records.
 *
 * Invariant:
 * DAO is storage-only. It does NOT modify current stock balances or perform inventory calculations.
 */
@Dao
interface StockAdjustmentDao {

    @Upsert
    fun upsert(adjustment: StockAdjustmentEntity)

    @Query("SELECT * FROM stock_adjustments WHERE id = :id")
    fun findById(id: String): StockAdjustmentEntity?

    @Query("SELECT * FROM stock_adjustments WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): StockAdjustmentEntity?

    @Query("SELECT * FROM stock_adjustments WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): StockAdjustmentEntity?

    @Query("SELECT * FROM stock_adjustments WHERE business_id = :businessId ORDER BY created_at DESC")
    fun findByBusinessId(businessId: String): List<StockAdjustmentEntity>

    @Query("SELECT * FROM stock_adjustments WHERE business_id = :businessId AND branch_id = :branchId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<StockAdjustmentEntity>

    @Query("SELECT * FROM stock_adjustments WHERE business_id = :businessId AND branch_id = :branchId AND product_id = :productId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndProductId(businessId: String, branchId: String, productId: String): List<StockAdjustmentEntity>

    @Query("SELECT * FROM stock_adjustments WHERE business_id = :businessId AND branch_id = :branchId AND reason = :reason ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndReason(businessId: String, branchId: String, reason: String): List<StockAdjustmentEntity>

    @Query("SELECT * FROM stock_adjustments WHERE product_id = :productId ORDER BY created_at DESC")
    fun findByProductId(productId: String): List<StockAdjustmentEntity>
}
