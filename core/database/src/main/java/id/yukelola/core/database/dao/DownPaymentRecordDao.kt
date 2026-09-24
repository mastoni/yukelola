package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.DownPaymentRecordEntity

/**
 * Room Data Access Object for [DownPaymentRecordEntity].
 * Provides minimal deterministic persistence operations for upfront down payment records.
 */
@Dao
interface DownPaymentRecordDao {

    @Upsert
    fun upsert(record: DownPaymentRecordEntity)

    @Upsert
    fun upsertAll(records: List<DownPaymentRecordEntity>)

    @Query("SELECT * FROM down_payment_records WHERE id = :id")
    fun findById(id: String): DownPaymentRecordEntity?

    @Query("SELECT * FROM down_payment_records WHERE order_id = :orderId ORDER BY created_at ASC")
    fun findByOrderId(orderId: String): List<DownPaymentRecordEntity>

    @Query("SELECT * FROM down_payment_records WHERE business_id = :businessId AND branch_id = :branchId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<DownPaymentRecordEntity>
}
