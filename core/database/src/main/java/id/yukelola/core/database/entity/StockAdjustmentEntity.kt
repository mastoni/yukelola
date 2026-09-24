package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.stock.StockAdjustment

/**
 * Room persistence entity for [StockAdjustment] immutable audit record.
 * Backed by canonical Contract v1.3.0 Section 3.
 *
 * Invariant:
 * StockAdjustment is an immutable historical record of physical stock correction (opname, damage, loss, etc.).
 * It is NOT the authoritative current stock state (BranchProductOverride.stock is the sole authority).
 */
@Entity(
    tableName = "stock_adjustments",
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
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["business_id"]),
        Index(value = ["branch_id"]),
        Index(value = ["product_id"]),
        Index(value = ["reason"]),
        Index(value = ["created_at"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "branch_id", "product_id"]),
        Index(value = ["business_id", "branch_id", "reason"])
    ]
)
data class StockAdjustmentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "branch_id")
    val branchId: String,
    @ColumnInfo(name = "product_id")
    val productId: String,
    @ColumnInfo(name = "previous_stock")
    val previousStock: Double,
    @ColumnInfo(name = "adjusted_stock")
    val adjustedStock: Double,
    @ColumnInfo(name = "reason")
    val reason: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "device_id")
    val deviceId: String,
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
