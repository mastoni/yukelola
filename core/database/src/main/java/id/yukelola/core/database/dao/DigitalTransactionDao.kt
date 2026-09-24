package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.DigitalTransactionEntity

/**
 * Room Data Access Object for [DigitalTransactionEntity].
 * Provides minimal deterministic persistence operations for digital transactions.
 */
@Dao
interface DigitalTransactionDao {

    @Upsert
    fun upsert(digitalTransaction: DigitalTransactionEntity)

    @Query("SELECT * FROM digital_transactions WHERE id = :id")
    fun findById(id: String): DigitalTransactionEntity?

    @Query("SELECT * FROM digital_transactions WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): DigitalTransactionEntity?

    @Query("SELECT * FROM digital_transactions WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): DigitalTransactionEntity?

    @Query("SELECT * FROM digital_transactions WHERE business_id = :businessId ORDER BY created_at DESC")
    fun findByBusinessId(businessId: String): List<DigitalTransactionEntity>

    @Query("SELECT * FROM digital_transactions WHERE business_id = :businessId AND branch_id = :branchId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<DigitalTransactionEntity>

    @Query("SELECT * FROM digital_transactions WHERE business_id = :businessId AND branch_id = :branchId AND fulfillment_status = :status ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndStatus(
        businessId: String,
        branchId: String,
        status: String
    ): List<DigitalTransactionEntity>

    @Query("SELECT * FROM digital_transactions WHERE business_id = :businessId AND branch_id = :branchId AND target_number = :targetNumber ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndTargetNumber(
        businessId: String,
        branchId: String,
        targetNumber: String
    ): List<DigitalTransactionEntity>

    @Query("SELECT * FROM digital_transactions WHERE business_id = :businessId AND sale_id = :saleId ORDER BY created_at DESC")
    fun findByBusinessIdAndSaleId(businessId: String, saleId: String): List<DigitalTransactionEntity>
}
