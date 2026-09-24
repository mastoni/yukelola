package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.serviceorder.ServiceOrder

/**
 * Room persistence entity for [ServiceOrder] aggregate root.
 * Backed by canonical Contract v1.3.0 Section 3.2.14.
 *
 * Invariant:
 * Unified asynchronous service order aggregate serving Laundry, Workshop, Printing, and Photocopy businesses.
 * Completed orders are immutable historical records.
 */
@Entity(
    tableName = "service_orders",
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
        Index(value = ["order_number"]),
        Index(value = ["order_type"]),
        Index(value = ["status"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "branch_id", "status"]),
        Index(value = ["business_id", "customer_id"])
    ]
)
data class ServiceOrderEntity(
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
    @ColumnInfo(name = "order_number")
    val orderNumber: String,
    @ColumnInfo(name = "order_type")
    val orderType: String,
    @ColumnInfo(name = "transaction_mode")
    val transactionMode: String,
    @ColumnInfo(name = "customer_id")
    val customerId: String? = null,
    @ColumnInfo(name = "discount_amount")
    val discountAmount: Long = 0L,
    @ColumnInfo(name = "tax_amount")
    val taxAmount: Long = 0L,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "context_metadata_json")
    val contextMetadataJson: String? = null,
    @ColumnInfo(name = "estimated_completion_date")
    val estimatedCompletionDate: Long? = null,
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null,
    @ColumnInfo(name = "cancelled_at")
    val cancelledAt: Long? = null
)
