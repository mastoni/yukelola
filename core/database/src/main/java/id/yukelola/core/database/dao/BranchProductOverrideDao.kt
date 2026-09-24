package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.BranchProductOverrideEntity

/**
 * Room Data Access Object for [BranchProductOverrideEntity].
 * Provides minimal deterministic persistence operations for branch-specific inventory overrides.
 */
@Dao
interface BranchProductOverrideDao {

    @Upsert
    fun upsert(override: BranchProductOverrideEntity)

    @Query("SELECT * FROM branch_product_overrides WHERE branch_id = :branchId AND product_id = :productId")
    fun findByBranchAndProduct(branchId: String, productId: String): BranchProductOverrideEntity?

    @Query("SELECT * FROM branch_product_overrides WHERE branch_id = :branchId")
    fun findByBranchId(branchId: String): List<BranchProductOverrideEntity>

    @Query("SELECT * FROM branch_product_overrides WHERE product_id = :productId")
    fun findByProductId(productId: String): List<BranchProductOverrideEntity>
}
