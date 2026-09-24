package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.BranchEntity

/**
 * Room Data Access Object for [BranchEntity].
 * Provides minimal deterministic persistence operations for the Branch slice,
 * preserving Business tenant ownership boundaries.
 */
@Dao
interface BranchDao {

    @Upsert
    fun upsert(branch: BranchEntity)

    @Query("SELECT * FROM branches WHERE id = :id")
    fun findById(id: String): BranchEntity?

    @Query("SELECT * FROM branches WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): BranchEntity?

    @Query("SELECT * FROM branches WHERE business_id = :businessId")
    fun findByBusinessId(businessId: String): List<BranchEntity>
}
