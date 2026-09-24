package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.ProductEntity

/**
 * Room Data Access Object for [ProductEntity].
 * Provides minimal deterministic persistence operations for the business-scoped Product catalog.
 */
@Dao
interface ProductDao {

    @Upsert
    fun upsert(product: ProductEntity)

    @Query("SELECT * FROM products WHERE id = :id")
    fun findById(id: String): ProductEntity?

    @Query("SELECT * FROM products WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): ProductEntity?

    @Query("SELECT * FROM products WHERE business_id = :businessId")
    fun findByBusinessId(businessId: String): List<ProductEntity>
}
