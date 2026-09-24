package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.DigitalTransactionComplaintEntity

/**
 * Room Data Access Object for [DigitalTransactionComplaintEntity].
 * Provides minimal deterministic persistence operations for digital transaction complaints.
 */
@Dao
interface DigitalTransactionComplaintDao {

    @Upsert
    fun upsert(complaint: DigitalTransactionComplaintEntity)

    @Query("SELECT * FROM digital_transaction_complaints WHERE id = :id")
    fun findById(id: String): DigitalTransactionComplaintEntity?

    @Query("SELECT * FROM digital_transaction_complaints WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): DigitalTransactionComplaintEntity?

    @Query("SELECT * FROM digital_transaction_complaints WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(
        id: String,
        businessId: String,
        branchId: String
    ): DigitalTransactionComplaintEntity?

    @Query("SELECT * FROM digital_transaction_complaints WHERE business_id = :businessId ORDER BY created_at DESC")
    fun findByBusinessId(businessId: String): List<DigitalTransactionComplaintEntity>

    @Query("SELECT * FROM digital_transaction_complaints WHERE business_id = :businessId AND branch_id = :branchId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchId(
        businessId: String,
        branchId: String
    ): List<DigitalTransactionComplaintEntity>

    @Query("SELECT * FROM digital_transaction_complaints WHERE business_id = :businessId AND branch_id = :branchId AND status = :status ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndStatus(
        businessId: String,
        branchId: String,
        status: String
    ): List<DigitalTransactionComplaintEntity>

    @Query("SELECT * FROM digital_transaction_complaints WHERE business_id = :businessId AND digital_transaction_id = :digitalTransactionId ORDER BY created_at DESC")
    fun findByBusinessIdAndDigitalTransactionId(
        businessId: String,
        digitalTransactionId: String
    ): List<DigitalTransactionComplaintEntity>

    @Query("SELECT * FROM digital_transaction_complaints WHERE business_id = :businessId AND branch_id = :branchId AND reason = :reason ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndReason(
        businessId: String,
        branchId: String,
        reason: String
    ): List<DigitalTransactionComplaintEntity>
}
