package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.SaleEntity

/**
 * Room Data Access Object for [SaleEntity].
 * Provides minimal deterministic persistence operations for sales aggregate roots.
 */
@Dao
interface SaleDao {

    @Upsert
    fun upsert(sale: SaleEntity)

    @Query("SELECT * FROM sales WHERE id = :id")
    fun findById(id: String): SaleEntity?

    @Query("SELECT * FROM sales WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): SaleEntity?

    @Query("SELECT * FROM sales WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): SaleEntity?

    @Query("SELECT * FROM sales WHERE business_id = :businessId ORDER BY created_at DESC")
    fun findByBusinessId(businessId: String): List<SaleEntity>

    @Query("SELECT * FROM sales WHERE business_id = :businessId AND branch_id = :branchId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<SaleEntity>

    @Query("SELECT * FROM sales WHERE business_id = :businessId AND customer_id = :customerId ORDER BY created_at DESC")
    fun findByBusinessIdAndCustomerId(businessId: String, customerId: String): List<SaleEntity>

    @Query("SELECT * FROM sales WHERE business_id = :businessId AND branch_id = :branchId AND customer_id = :customerId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndCustomerId(
        businessId: String,
        branchId: String,
        customerId: String
    ): List<SaleEntity>

    @Query("SELECT * FROM sales WHERE business_id = :businessId AND branch_id = :branchId AND status = :status ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndStatus(
        businessId: String,
        branchId: String,
        status: String
    ): List<SaleEntity>
}
