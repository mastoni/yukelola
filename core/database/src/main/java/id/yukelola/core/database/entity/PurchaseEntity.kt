package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.purchase.Purchase

/**
 * Room persistence entity for [Purchase] aggregate root.
 * Backed by canonical Contract v1.3.0 Section 3.2.16.
 *
 * Invariant:
 * Inventory replenishment procurement aggregate record from a supplier.
 */
@Entity(
    tableName = "purchases",
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
        Index(value = ["purchase_number"]),
        Index(value = ["payment_status"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "supplier_id"])
    ]
)
data class PurchaseEntity(
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
    @ColumnInfo(name = "purchase_number")
    val purchaseNumber: String,
    @ColumnInfo(name = "supplier_id")
    val supplierId: String? = null,
    @ColumnInfo(name = "paid_amount")
    val paidAmount: Long = 0L,
    @ColumnInfo(name = "payment_status")
    val paymentStatus: String,
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
