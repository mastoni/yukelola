package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.debt.DebtPayment

/**
 * Room persistence entity for [DebtPayment].
 * Backed by canonical Contract v1.3.0 Section 3.
 *
 * Invariant:
 * Financial event recording settlement or partial reduction of an outstanding CustomerDebt or SupplierDebt.
 */
@Entity(
    tableName = "debt_payments",
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
        Index(value = ["debt_id"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "debt_id"]),
        Index(value = ["business_id", "branch_id", "debt_id"])
    ]
)
data class DebtPaymentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "branch_id")
    val branchId: String,
    @ColumnInfo(name = "debt_type")
    val debtType: String,
    @ColumnInfo(name = "debt_id")
    val debtId: String,
    @ColumnInfo(name = "amount")
    val amount: Long,
    @ColumnInfo(name = "payment_method")
    val paymentMethod: String,
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
