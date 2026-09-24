package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.cash.CashRegister

/**
 * Room persistence entity for [CashRegister].
 * Backed by canonical Contract v1.3.0 and Financial Effect Matrix Section 1 & 3.
 *
 * Invariant:
 * Physical cash drawer aggregate tracking physical cash balance and state for a Branch.
 */
@Entity(
    tableName = "cash_registers",
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
        Index(value = ["business_id", "branch_id"])
    ]
)
data class CashRegisterEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "branch_id")
    val branchId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "current_balance")
    val currentBalance: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
