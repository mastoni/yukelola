package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.DebtPaymentEntity

/**
 * Room Data Access Object for [DebtPaymentEntity].
 * Provides minimal deterministic persistence operations for debt payment events.
 */
@Dao
interface DebtPaymentDao {

    @Upsert
    fun upsert(debtPayment: DebtPaymentEntity)

    @Query("SELECT * FROM debt_payments WHERE id = :id")
    fun findById(id: String): DebtPaymentEntity?

    @Query("SELECT * FROM debt_payments WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): DebtPaymentEntity?

    @Query("SELECT * FROM debt_payments WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): DebtPaymentEntity?

    @Query("SELECT * FROM debt_payments WHERE business_id = :businessId ORDER BY created_at DESC")
    fun findByBusinessId(businessId: String): List<DebtPaymentEntity>

    @Query("SELECT * FROM debt_payments WHERE business_id = :businessId AND branch_id = :branchId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<DebtPaymentEntity>

    @Query("SELECT * FROM debt_payments WHERE business_id = :businessId AND debt_id = :debtId ORDER BY created_at DESC")
    fun findByBusinessIdAndDebtId(businessId: String, debtId: String): List<DebtPaymentEntity>

    @Query("SELECT * FROM debt_payments WHERE business_id = :businessId AND branch_id = :branchId AND debt_id = :debtId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndDebtId(businessId: String, branchId: String, debtId: String): List<DebtPaymentEntity>
}
