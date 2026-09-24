package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.serviceorder.DownPaymentRecord

/**
 * Room persistence entity for [DownPaymentRecord] upfront deposit.
 * Backed by canonical Contract v1.3.0 Section 3.2.14.
 *
 * Invariant:
 * Down payment represents customer unearned deposit / advance payment.
 * It is NOT recognized immediately as sales profit.
 */
@Entity(
    tableName = "down_payment_records",
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
            entity = ServiceOrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["order_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["business_id"]),
        Index(value = ["branch_id"]),
        Index(value = ["order_id"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "order_id"])
    ]
)
data class DownPaymentRecordEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "branch_id")
    val branchId: String,
    @ColumnInfo(name = "order_id")
    val orderId: String,
    @ColumnInfo(name = "amount")
    val amount: Long,
    @ColumnInfo(name = "payment_method")
    val paymentMethod: String,
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
