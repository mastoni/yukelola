package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.purchase.PurchaseItem

/**
 * Room persistence entity for [PurchaseItem] line item snapshot.
 * Backed by canonical Contract v1.3.0 Section 3.2.17.
 *
 * Invariant:
 * Immutable line item snapshot representing goods procured within a Purchase record.
 * Preserves historical unit cost, quantity, and product snapshot at time of purchase.
 */
@Entity(
    tableName = "purchase_items",
    foreignKeys = [
        ForeignKey(
            entity = PurchaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["purchase_id"],
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
        Index(value = ["purchase_id"]),
        Index(value = ["product_id"])
    ]
)
data class PurchaseItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "purchase_id")
    val purchaseId: String,
    @ColumnInfo(name = "product_id")
    val productId: String,
    @ColumnInfo(name = "product_name")
    val productName: String,
    @ColumnInfo(name = "unit")
    val unit: String = "PCS",
    @ColumnInfo(name = "unit_cost")
    val unitCost: Long,
    @ColumnInfo(name = "quantity")
    val quantity: Double = 1.0,
    @ColumnInfo(name = "subtotal")
    val subtotal: Long
)
