package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.DeviceEntity

/**
 * Room Data Access Object for [DeviceEntity].
 * Provides deterministic persistence operations for physical branch terminals.
 *
 * Invariant:
 * DAO is storage-only. It does NOT implement device registration, pairing, discovery, or trust workflows.
 */
@Dao
interface DeviceDao {

    @Upsert
    fun upsert(device: DeviceEntity)

    @Query("SELECT * FROM devices WHERE id = :id")
    fun findById(id: String): DeviceEntity?

    @Query("SELECT * FROM devices WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): DeviceEntity?

    @Query("SELECT * FROM devices WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): DeviceEntity?

    @Query("SELECT * FROM devices WHERE business_id = :businessId ORDER BY device_name ASC")
    fun findByBusinessId(businessId: String): List<DeviceEntity>

    @Query("SELECT * FROM devices WHERE business_id = :businessId AND branch_id = :branchId ORDER BY device_name ASC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<DeviceEntity>

    @Query("SELECT * FROM devices WHERE business_id = :businessId AND branch_id = :branchId AND is_active = 1 ORDER BY device_name ASC")
    fun findActiveByBusinessIdAndBranchId(businessId: String, branchId: String): List<DeviceEntity>

    @Query("SELECT * FROM devices WHERE business_id = :businessId AND branch_id = :branchId AND device_type = :deviceType ORDER BY device_name ASC")
    fun findByBusinessIdAndBranchIdAndDeviceType(businessId: String, branchId: String, deviceType: String): List<DeviceEntity>

    @Query("SELECT * FROM devices WHERE branch_id = :branchId ORDER BY device_name ASC")
    fun findByBranchId(branchId: String): List<DeviceEntity>
}
