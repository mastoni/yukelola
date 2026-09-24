package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.ProductUnitEntity

/**
 * Room Data Access Object for [ProductUnitEntity].
 * Provides minimal deterministic persistence operations for business-scoped measurement units.
 */
@Dao
interface ProductUnitDao {

    @Upsert
    fun upsert(productUnit: ProductUnitEntity)

    @Query("SELECT * FROM product_units WHERE id = :id")
    fun findById(id: String): ProductUnitEntity?

    @Query("SELECT * FROM product_units WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): ProductUnitEntity?

    @Query("SELECT * FROM product_units WHERE business_id = :businessId ORDER BY name ASC")
    fun findByBusinessId(businessId: String): List<ProductUnitEntity>
}
