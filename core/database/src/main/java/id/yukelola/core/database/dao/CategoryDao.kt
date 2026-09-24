package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.CategoryEntity

/**
 * Room Data Access Object for [CategoryEntity].
 * Provides minimal deterministic persistence operations for business-scoped categories.
 */
@Dao
interface CategoryDao {

    @Upsert
    fun upsert(category: CategoryEntity)

    @Query("SELECT * FROM categories WHERE id = :id")
    fun findById(id: String): CategoryEntity?

    @Query("SELECT * FROM categories WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): CategoryEntity?

    @Query("SELECT * FROM categories WHERE business_id = :businessId ORDER BY sort_order ASC, name ASC")
    fun findByBusinessId(businessId: String): List<CategoryEntity>
}
