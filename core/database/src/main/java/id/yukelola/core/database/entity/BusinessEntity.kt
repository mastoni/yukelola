package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room persistence entity for the top-level tenant root [Business].
 * Backed by canonical Contract v1.3.0 Section 3.
 */
@Entity(tableName = "businesses")
data class BusinessEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "legal_name")
    val legalName: String,
    @ColumnInfo(name = "owner_user_id")
    val ownerUserId: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
