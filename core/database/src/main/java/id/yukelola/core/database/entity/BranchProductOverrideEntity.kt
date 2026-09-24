package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Room persistence entity for [BranchProductOverride].
 * Backed by canonical Contract v1.3.0 Section 3.
 *
 * Invariant: Governs branch-specific stock counts, reorder thresholds, pricing overrides, and availability.
 */
@Entity(
    tableName = "branch_product_overrides",
    primaryKeys = ["branch_id", "product_id"],
    foreignKeys = [
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
        Index(value = ["branch_id"]),
        Index(value = ["product_id"])
    ]
)
data class BranchProductOverrideEntity(
    @ColumnInfo(name = "branch_id")
    val branchId: String,
    @ColumnInfo(name = "product_id")
    val productId: String,
    @ColumnInfo(name = "stock")
    val stock: Double = 0.0,
    @ColumnInfo(name = "min_stock")
    val minStock: Double = 0.0,
    @ColumnInfo(name = "local_cost_price")
    val localCostPrice: Long? = null,
    @ColumnInfo(name = "local_selling_price")
    val localSellingPrice: Long? = null,
    @ColumnInfo(name = "is_available")
    val isAvailable: Boolean = true
)
