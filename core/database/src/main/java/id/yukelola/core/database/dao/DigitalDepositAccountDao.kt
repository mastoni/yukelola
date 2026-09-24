package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.DigitalDepositAccountEntity

/**
 * Room Data Access Object for [DigitalDepositAccountEntity].
 * Provides minimal deterministic persistence operations for branch-scoped digital deposit accounts.
 */
@Dao
interface DigitalDepositAccountDao {

    @Upsert
    fun upsert(account: DigitalDepositAccountEntity)

    @Query("SELECT * FROM digital_deposit_accounts WHERE id = :id")
    fun findById(id: String): DigitalDepositAccountEntity?

    @Query("SELECT * FROM digital_deposit_accounts WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): DigitalDepositAccountEntity?

    @Query("SELECT * FROM digital_deposit_accounts WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): DigitalDepositAccountEntity?

    @Query("SELECT * FROM digital_deposit_accounts WHERE business_id = :businessId ORDER BY id ASC")
    fun findByBusinessId(businessId: String): List<DigitalDepositAccountEntity>

    @Query("SELECT * FROM digital_deposit_accounts WHERE business_id = :businessId AND branch_id = :branchId ORDER BY id ASC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<DigitalDepositAccountEntity>
}
