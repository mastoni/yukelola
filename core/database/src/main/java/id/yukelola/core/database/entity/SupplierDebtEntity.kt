package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.debt.SupplierDebt

/**
 * Room persistence entity for [SupplierDebt].
 * Backed by canonical Contract v1.3.0 Section 3.
 *
 * Invariant:
 * Outstanding credit payable owed by a branch to a vendor / supplier.
 */
@Entity(
    tableName = "supplier_debts",
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
            entity = SupplierEntity::class,
            parentColumns = ["id"],
            childColumns = ["supplier_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["business_id"]),
        Index(value = ["branch_id"]),
        Index(value = ["supplier_id"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "status"])
    ]
)
data class SupplierDebtEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "branch_id")
    val branchId: String,
    @ColumnInfo(name = "supplier_id")
    val supplierId: String,
    @ColumnInfo(name = "purchase_id")
    val purchaseId: String,
    @ColumnInfo(name = "original_amount")
    val originalAmount: Long,
    @ColumnInfo(name = "remaining_amount")
    val remainingAmount: Long,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "due_date")
    val dueDate: Long? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
