package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.ServiceOrderItemEntity

/**
 * Room Data Access Object for [ServiceOrderItemEntity].
 * Provides minimal deterministic persistence operations for service order line item snapshots.
 */
@Dao
interface ServiceOrderItemDao {

    @Upsert
    fun upsert(item: ServiceOrderItemEntity)

    @Upsert
    fun upsertAll(items: List<ServiceOrderItemEntity>)

    @Query("SELECT * FROM service_order_items WHERE id = :id")
    fun findById(id: String): ServiceOrderItemEntity?

    @Query("SELECT * FROM service_order_items WHERE order_id = :orderId")
    fun findByOrderId(orderId: String): List<ServiceOrderItemEntity>
}
