package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.CustomerDebtEntity

/**
 * Room Data Access Object for [CustomerDebtEntity].
 * Provides minimal deterministic persistence operations for branch-scoped customer credit receivables.
 */
@Dao
interface CustomerDebtDao {

    @Upsert
    fun upsert(customerDebt: CustomerDebtEntity)

    @Query("SELECT * FROM customer_debts WHERE id = :id")
    fun findById(id: String): CustomerDebtEntity?

    @Query("SELECT * FROM customer_debts WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): CustomerDebtEntity?

    @Query("SELECT * FROM customer_debts WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): CustomerDebtEntity?

    @Query("SELECT * FROM customer_debts WHERE business_id = :businessId ORDER BY created_at DESC")
    fun findByBusinessId(businessId: String): List<CustomerDebtEntity>

    @Query("SELECT * FROM customer_debts WHERE business_id = :businessId AND branch_id = :branchId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<CustomerDebtEntity>

    @Query("SELECT * FROM customer_debts WHERE business_id = :businessId AND customer_id = :customerId ORDER BY created_at DESC")
    fun findByBusinessIdAndCustomerId(businessId: String, customerId: String): List<CustomerDebtEntity>
}
