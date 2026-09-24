package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.PurchaseItemEntity

/**
 * Room Data Access Object for [PurchaseItemEntity].
 * Provides minimal deterministic persistence operations for purchase line item snapshots.
 */
@Dao
interface PurchaseItemDao {

    @Upsert
    fun upsert(item: PurchaseItemEntity)

    @Upsert
    fun upsertAll(items: List<PurchaseItemEntity>)

    @Query("SELECT * FROM purchase_items WHERE id = :id")
    fun findById(id: String): PurchaseItemEntity?

    @Query("SELECT * FROM purchase_items WHERE purchase_id = :purchaseId")
    fun findByPurchaseId(purchaseId: String): List<PurchaseItemEntity>
}
