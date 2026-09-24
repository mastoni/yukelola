package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.CustomerEntity

/**
 * Room Data Access Object for [CustomerEntity].
 * Provides minimal deterministic persistence operations for business-scoped customer profiles.
 */
@Dao
interface CustomerDao {

    @Upsert
    fun upsert(customer: CustomerEntity)

    @Query("SELECT * FROM customers WHERE id = :id")
    fun findById(id: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE business_id = :businessId ORDER BY name ASC")
    fun findByBusinessId(businessId: String): List<CustomerEntity>

    @Query("SELECT * FROM customers WHERE business_id = :businessId AND branch_id = :branchId ORDER BY name ASC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<CustomerEntity>
}
