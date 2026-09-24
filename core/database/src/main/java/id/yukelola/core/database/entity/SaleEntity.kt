package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.sale.Sale

/**
 * Room persistence entity for [Sale] aggregate root.
 * Backed by canonical Contract v1.3.0 Section 3.2.12.
 *
 * Invariant:
 * Historical commercial retail transaction aggregate.
 * Completed sales are immutable historical records.
 */
@Entity(
    tableName = "sales",
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
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customer_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["business_id"]),
        Index(value = ["branch_id"]),
        Index(value = ["customer_id"]),
        Index(value = ["sale_number"]),
        Index(value = ["status"]),
        Index(value = ["payment_status"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "branch_id", "status"]),
        Index(value = ["business_id", "customer_id"])
    ]
)
data class SaleEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "branch_id")
    val branchId: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "device_id")
    val deviceId: String,
    @ColumnInfo(name = "cashier_session_id")
    val cashierSessionId: String? = null,
    @ColumnInfo(name = "sale_number")
    val saleNumber: String,
    @ColumnInfo(name = "transaction_mode")
    val transactionMode: String,
    @ColumnInfo(name = "customer_id")
    val customerId: String? = null,
    @ColumnInfo(name = "discount_amount")
    val discountAmount: Long = 0L,
    @ColumnInfo(name = "tax_amount")
    val taxAmount: Long = 0L,
    @ColumnInfo(name = "paid_amount")
    val paidAmount: Long = 0L,
    @ColumnInfo(name = "payment_status")
    val paymentStatus: String,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null,
    @ColumnInfo(name = "cancelled_at")
    val cancelledAt: Long? = null
)
