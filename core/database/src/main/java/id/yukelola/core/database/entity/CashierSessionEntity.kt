package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.actor.CashierSession

/**
 * Room persistence entity for [CashierSession].
 * Backed by canonical Contract v1.3.0 Section 3.1 & 3.2.14.
 *
 * Invariant:
 * Active cash drawer shift session binding an operator (User), a terminal (Device), and a Branch.
 * Storage only: does not implement session authentication, shift reconciliation, or cash mutation.
 */
@Entity(
    tableName = "cashier_sessions",
    foreignKeys = [
        ForeignKey(
            entity = BranchEntity::class,
            parentColumns = ["id"],
            childColumns = ["branch_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["branch_id"]),
        Index(value = ["user_id"]),
        Index(value = ["device_id"]),
        Index(value = ["status"]),
        Index(value = ["opened_at"]),
        Index(value = ["branch_id", "status"]),
        Index(value = ["branch_id", "user_id"]),
        Index(value = ["branch_id", "device_id"])
    ]
)
data class CashierSessionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "branch_id")
    val branchId: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "device_id")
    val deviceId: String,
    @ColumnInfo(name = "opening_balance")
    val openingBalance: Long,
    @ColumnInfo(name = "closing_balance")
    val closingBalance: Long? = null,
    @ColumnInfo(name = "opened_at")
    val openedAt: Long,
    @ColumnInfo(name = "closed_at")
    val closedAt: Long? = null,
    @ColumnInfo(name = "status")
    val status: String
)
