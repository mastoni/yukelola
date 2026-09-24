package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.DigitalDepositMutationEntity

/**
 * Room Data Access Object for [DigitalDepositMutationEntity].
 * Provides minimal deterministic persistence operations for historical digital deposit fund movement records.
 */
@Dao
interface DigitalDepositMutationDao {

    @Upsert
    fun upsert(mutation: DigitalDepositMutationEntity)

    @Query("SELECT * FROM digital_deposit_mutations WHERE id = :id")
    fun findById(id: String): DigitalDepositMutationEntity?

    @Query("SELECT * FROM digital_deposit_mutations WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): DigitalDepositMutationEntity?

    @Query("SELECT * FROM digital_deposit_mutations WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): DigitalDepositMutationEntity?

    @Query("SELECT * FROM digital_deposit_mutations WHERE business_id = :businessId ORDER BY created_at DESC")
    fun findByBusinessId(businessId: String): List<DigitalDepositMutationEntity>

    @Query("SELECT * FROM digital_deposit_mutations WHERE business_id = :businessId AND branch_id = :branchId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<DigitalDepositMutationEntity>

    @Query("SELECT * FROM digital_deposit_mutations WHERE business_id = :businessId AND account_id = :accountId ORDER BY created_at DESC")
    fun findByBusinessIdAndAccountId(businessId: String, accountId: String): List<DigitalDepositMutationEntity>

    @Query("SELECT * FROM digital_deposit_mutations WHERE business_id = :businessId AND branch_id = :branchId AND account_id = :accountId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndAccountId(
        businessId: String,
        branchId: String,
        accountId: String
    ): List<DigitalDepositMutationEntity>
}
