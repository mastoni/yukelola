package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.PurchaseEntity

/**
 * Room Data Access Object for [PurchaseEntity].
 * Provides minimal deterministic persistence operations for purchase aggregate roots.
 */
@Dao
interface PurchaseDao {

    @Upsert
    fun upsert(purchase: PurchaseEntity)

    @Query("SELECT * FROM purchases WHERE id = :id")
    fun findById(id: String): PurchaseEntity?

    @Query("SELECT * FROM purchases WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): PurchaseEntity?

    @Query("SELECT * FROM purchases WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): PurchaseEntity?

    @Query("SELECT * FROM purchases WHERE business_id = :businessId ORDER BY created_at DESC")
    fun findByBusinessId(businessId: String): List<PurchaseEntity>

    @Query("SELECT * FROM purchases WHERE business_id = :businessId AND branch_id = :branchId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<PurchaseEntity>

    @Query("SELECT * FROM purchases WHERE business_id = :businessId AND supplier_id = :supplierId ORDER BY created_at DESC")
    fun findByBusinessIdAndSupplierId(businessId: String, supplierId: String): List<PurchaseEntity>

    @Query("SELECT * FROM purchases WHERE business_id = :businessId AND branch_id = :branchId AND supplier_id = :supplierId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndSupplierId(
        businessId: String,
        branchId: String,
        supplierId: String
    ): List<PurchaseEntity>
}
