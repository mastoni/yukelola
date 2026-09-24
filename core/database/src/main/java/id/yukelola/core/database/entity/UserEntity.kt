package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.actor.User

/**
 * Room persistence entity for [User].
 * Backed by canonical Contract v1.3.0 Section 3.1 & Section 3.2.14.
 *
 * Invariant:
 * Represents system operators / staff members belonging to a Business and optionally assigned to a Branch.
 * Storage only: contains zero authentication credentials, password hashes, or session authorization logic.
 */
@Entity(
    tableName = "users",
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
        Index(value = ["username"]),
        Index(value = ["role"]),
        Index(value = ["is_active"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "username"], unique = true),
        Index(value = ["business_id", "role"])
    ]
)
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "branch_id")
    val branchId: String? = null,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "full_name")
    val fullName: String,
    @ColumnInfo(name = "role")
    val role: String,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
