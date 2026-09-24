package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.SupplierDebtEntity

/**
 * Room Data Access Object for [SupplierDebtEntity].
 * Provides minimal deterministic persistence operations for branch-scoped vendor debt payables.
 */
@Dao
interface SupplierDebtDao {

    @Upsert
    fun upsert(supplierDebt: SupplierDebtEntity)

    @Query("SELECT * FROM supplier_debts WHERE id = :id")
    fun findById(id: String): SupplierDebtEntity?

    @Query("SELECT * FROM supplier_debts WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): SupplierDebtEntity?

    @Query("SELECT * FROM supplier_debts WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): SupplierDebtEntity?

    @Query("SELECT * FROM supplier_debts WHERE business_id = :businessId ORDER BY created_at DESC")
    fun findByBusinessId(businessId: String): List<SupplierDebtEntity>

    @Query("SELECT * FROM supplier_debts WHERE business_id = :businessId AND branch_id = :branchId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<SupplierDebtEntity>

    @Query("SELECT * FROM supplier_debts WHERE business_id = :businessId AND supplier_id = :supplierId ORDER BY created_at DESC")
    fun findByBusinessIdAndSupplierId(businessId: String, supplierId: String): List<SupplierDebtEntity>
}
