package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.actor.Device

/**
 * Room persistence entity for [Device].
 * Backed by canonical Contract v1.3.0 Section 3.1 & Section 3.2.14.
 *
 * Invariant:
 * Physical Android terminal device registered and paired to an operational Branch.
 * Storage only: contains zero authentication credentials, mDNS/LAN discovery, or network pairing secrets.
 */
@Entity(
    tableName = "devices",
    foreignKeys = [
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["id"],
            childColumns = ["business_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = BranchEntity::class,
            parentColumns = ["id"],
            childColumns = ["branch_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["business_id"]),
        Index(value = ["branch_id"]),
        Index(value = ["device_type"]),
        Index(value = ["is_active"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "branch_id", "device_type"]),
        Index(value = ["business_id", "branch_id", "is_active"])
    ]
)
data class DeviceEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "branch_id")
    val branchId: String,
    @ColumnInfo(name = "device_name")
    val deviceName: String,
    @ColumnInfo(name = "device_type")
    val deviceType: String,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
