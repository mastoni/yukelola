package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.SaleItemEntity

/**
 * Room Data Access Object for [SaleItemEntity].
 * Provides minimal deterministic persistence operations for sale line item snapshots.
 */
@Dao
interface SaleItemDao {

    @Upsert
    fun upsert(item: SaleItemEntity)

    @Upsert
    fun upsertAll(items: List<SaleItemEntity>)

    @Query("SELECT * FROM sale_items WHERE id = :id")
    fun findById(id: String): SaleItemEntity?

    @Query("SELECT * FROM sale_items WHERE sale_id = :saleId")
    fun findBySaleId(saleId: String): List<SaleItemEntity>
}
