package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.UserEntity

/**
 * Room Data Access Object for [UserEntity].
 * Provides deterministic persistence operations for staff and system operators.
 *
 * Invariant:
 * DAO is storage-only. It does NOT implement authentication, PIN/password checking, or token issuance.
 */
@Dao
interface UserDao {

    @Upsert
    fun upsert(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    fun findById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): UserEntity?

    @Query("SELECT * FROM users WHERE business_id = :businessId ORDER BY username ASC")
    fun findByBusinessId(businessId: String): List<UserEntity>

    @Query("SELECT * FROM users WHERE business_id = :businessId AND branch_id = :branchId ORDER BY username ASC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<UserEntity>

    @Query("SELECT * FROM users WHERE business_id = :businessId AND username = :username")
    fun findByBusinessIdAndUsername(businessId: String, username: String): UserEntity?

    @Query("SELECT * FROM users WHERE business_id = :businessId AND is_active = 1 ORDER BY username ASC")
    fun findActiveByBusinessId(businessId: String): List<UserEntity>

    @Query("SELECT * FROM users WHERE business_id = :businessId AND role = :role ORDER BY username ASC")
    fun findByBusinessIdAndRole(businessId: String, role: String): List<UserEntity>

    @Query("SELECT * FROM users WHERE branch_id = :branchId ORDER BY username ASC")
    fun findByBranchId(branchId: String): List<UserEntity>
}
