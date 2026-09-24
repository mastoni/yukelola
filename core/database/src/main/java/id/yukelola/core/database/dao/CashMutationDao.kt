package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.CashMutationEntity

/**
 * Room Data Access Object for [CashMutationEntity].
 * Provides minimal deterministic persistence operations for historical cash movement audit records.
 */
@Dao
interface CashMutationDao {

    @Upsert
    fun upsert(cashMutation: CashMutationEntity)

    @Query("SELECT * FROM cash_mutations WHERE id = :id")
    fun findById(id: String): CashMutationEntity?

    @Query("SELECT * FROM cash_mutations WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): CashMutationEntity?

    @Query("SELECT * FROM cash_mutations WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): CashMutationEntity?

    @Query("SELECT * FROM cash_mutations WHERE business_id = :businessId ORDER BY created_at DESC")
    fun findByBusinessId(businessId: String): List<CashMutationEntity>

    @Query("SELECT * FROM cash_mutations WHERE business_id = :businessId AND branch_id = :branchId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<CashMutationEntity>

    @Query("SELECT * FROM cash_mutations WHERE business_id = :businessId AND register_id = :registerId ORDER BY created_at DESC")
    fun findByBusinessIdAndRegisterId(businessId: String, registerId: String): List<CashMutationEntity>

    @Query("SELECT * FROM cash_mutations WHERE business_id = :businessId AND branch_id = :branchId AND register_id = :registerId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndRegisterId(businessId: String, branchId: String, registerId: String): List<CashMutationEntity>
}
