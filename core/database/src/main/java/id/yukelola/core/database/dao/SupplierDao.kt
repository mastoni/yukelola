package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.SupplierEntity

/**
 * Room Data Access Object for [SupplierEntity].
 * Provides minimal deterministic persistence operations for business-scoped vendor profiles.
 */
@Dao
interface SupplierDao {

    @Upsert
    fun upsert(supplier: SupplierEntity)

    @Query("SELECT * FROM suppliers WHERE id = :id")
    fun findById(id: String): SupplierEntity?

    @Query("SELECT * FROM suppliers WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): SupplierEntity?

    @Query("SELECT * FROM suppliers WHERE business_id = :businessId ORDER BY name ASC")
    fun findByBusinessId(businessId: String): List<SupplierEntity>
}
