package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.ServiceOrderEntity

/**
 * Room Data Access Object for [ServiceOrderEntity].
 * Provides minimal deterministic persistence operations for service order aggregate roots.
 */
@Dao
interface ServiceOrderDao {

    @Upsert
    fun upsert(serviceOrder: ServiceOrderEntity)

    @Query("SELECT * FROM service_orders WHERE id = :id")
    fun findById(id: String): ServiceOrderEntity?

    @Query("SELECT * FROM service_orders WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): ServiceOrderEntity?

    @Query("SELECT * FROM service_orders WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): ServiceOrderEntity?

    @Query("SELECT * FROM service_orders WHERE business_id = :businessId ORDER BY created_at DESC")
    fun findByBusinessId(businessId: String): List<ServiceOrderEntity>

    @Query("SELECT * FROM service_orders WHERE business_id = :businessId AND branch_id = :branchId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<ServiceOrderEntity>

    @Query("SELECT * FROM service_orders WHERE business_id = :businessId AND customer_id = :customerId ORDER BY created_at DESC")
    fun findByBusinessIdAndCustomerId(businessId: String, customerId: String): List<ServiceOrderEntity>

    @Query("SELECT * FROM service_orders WHERE business_id = :businessId AND branch_id = :branchId AND customer_id = :customerId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndCustomerId(
        businessId: String,
        branchId: String,
        customerId: String
    ): List<ServiceOrderEntity>

    @Query("SELECT * FROM service_orders WHERE business_id = :businessId AND branch_id = :branchId AND status = :status ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndStatus(
        businessId: String,
        branchId: String,
        status: String
    ): List<ServiceOrderEntity>

    @Query("SELECT * FROM service_orders WHERE business_id = :businessId AND branch_id = :branchId AND order_type = :orderType ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndOrderType(
        businessId: String,
        branchId: String,
        orderType: String
    ): List<ServiceOrderEntity>
}
