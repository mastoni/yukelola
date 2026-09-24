package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.PaymentEntity

/**
 * Room Data Access Object for [PaymentEntity].
 * Provides minimal deterministic persistence operations for payment events.
 */
@Dao
interface PaymentDao {

    @Upsert
    fun upsert(payment: PaymentEntity)

    @Query("SELECT * FROM payments WHERE id = :id")
    fun findById(id: String): PaymentEntity?

    @Query("SELECT * FROM payments WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): PaymentEntity?

    @Query("SELECT * FROM payments WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): PaymentEntity?

    @Query("SELECT * FROM payments WHERE business_id = :businessId ORDER BY created_at DESC")
    fun findByBusinessId(businessId: String): List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE business_id = :businessId AND branch_id = :branchId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE business_id = :businessId AND reference_id = :referenceId ORDER BY created_at DESC")
    fun findByBusinessIdAndReferenceId(businessId: String, referenceId: String): List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE business_id = :businessId AND branch_id = :branchId AND reference_id = :referenceId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndReferenceId(
        businessId: String,
        branchId: String,
        referenceId: String
    ): List<PaymentEntity>
}
