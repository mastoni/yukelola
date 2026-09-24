package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.CashRegisterEntity

/**
 * Room Data Access Object for [CashRegisterEntity].
 * Provides minimal deterministic persistence operations for branch-scoped physical cash registers.
 */
@Dao
interface CashRegisterDao {

    @Upsert
    fun upsert(cashRegister: CashRegisterEntity)

    @Query("SELECT * FROM cash_registers WHERE id = :id")
    fun findById(id: String): CashRegisterEntity?

    @Query("SELECT * FROM cash_registers WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): CashRegisterEntity?

    @Query("SELECT * FROM cash_registers WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): CashRegisterEntity?

    @Query("SELECT * FROM cash_registers WHERE business_id = :businessId ORDER BY name ASC")
    fun findByBusinessId(businessId: String): List<CashRegisterEntity>

    @Query("SELECT * FROM cash_registers WHERE business_id = :businessId AND branch_id = :branchId ORDER BY name ASC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<CashRegisterEntity>
}
