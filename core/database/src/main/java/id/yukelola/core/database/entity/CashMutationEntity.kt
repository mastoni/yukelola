package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.cash.CashMutation

/**
 * Room persistence entity for [CashMutation].
 * Backed by canonical Contract v1.3.0 and Financial Effect Matrix Section 1 & 3.
 *
 * Invariant:
 * Historical cash drawer movement record representing physical cash inflow or outflow.
 */
@Entity(
    tableName = "cash_mutations",
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
        ),
        ForeignKey(
            entity = CashRegisterEntity::class,
            parentColumns = ["id"],
            childColumns = ["register_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["business_id"]),
        Index(value = ["branch_id"]),
        Index(value = ["register_id"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "register_id"]),
        Index(value = ["business_id", "branch_id", "register_id"])
    ]
)
data class CashMutationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "branch_id")
    val branchId: String,
    @ColumnInfo(name = "register_id")
    val registerId: String,
    @ColumnInfo(name = "cashier_session_id")
    val cashierSessionId: String? = null,
    @ColumnInfo(name = "mutation_type")
    val mutationType: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "amount")
    val amount: Long,
    @ColumnInfo(name = "source")
    val source: String,
    @ColumnInfo(name = "reference_id")
    val referenceId: String? = null,
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
