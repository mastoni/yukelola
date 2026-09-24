package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.CashierSessionEntity

/**
 * Room Data Access Object for [CashierSessionEntity].
 * Provides deterministic persistence operations for shift sessions.
 *
 * Invariant:
 * DAO is storage-only. It does NOT implement authentication, PIN verification, or cash mutation.
 */
@Dao
interface CashierSessionDao {

    @Upsert
    fun upsert(session: CashierSessionEntity)

    @Query("SELECT * FROM cashier_sessions WHERE id = :id")
    fun findById(id: String): CashierSessionEntity?

    @Query("SELECT * FROM cashier_sessions WHERE id = :id AND branch_id = :branchId")
    fun findByIdAndBranchId(id: String, branchId: String): CashierSessionEntity?

    @Query("SELECT * FROM cashier_sessions WHERE branch_id = :branchId ORDER BY opened_at DESC")
    fun findByBranchId(branchId: String): List<CashierSessionEntity>

    @Query("SELECT * FROM cashier_sessions WHERE branch_id = :branchId AND status = :status ORDER BY opened_at DESC")
    fun findByBranchIdAndStatus(branchId: String, status: String): List<CashierSessionEntity>

    @Query("SELECT * FROM cashier_sessions WHERE branch_id = :branchId AND user_id = :userId ORDER BY opened_at DESC")
    fun findByBranchIdAndUserId(branchId: String, userId: String): List<CashierSessionEntity>

    @Query("SELECT * FROM cashier_sessions WHERE branch_id = :branchId AND device_id = :deviceId ORDER BY opened_at DESC")
    fun findByBranchIdAndDeviceId(branchId: String, deviceId: String): List<CashierSessionEntity>

    @Query("SELECT * FROM cashier_sessions WHERE branch_id = :branchId AND user_id = :userId AND status = 'OPEN' ORDER BY opened_at DESC LIMIT 1")
    fun findOpenSessionByBranchIdAndUserId(branchId: String, userId: String): CashierSessionEntity?

    @Query("SELECT * FROM cashier_sessions WHERE branch_id = :branchId AND device_id = :deviceId AND status = 'OPEN' ORDER BY opened_at DESC LIMIT 1")
    fun findOpenSessionByBranchIdAndDeviceId(branchId: String, deviceId: String): CashierSessionEntity?
}
