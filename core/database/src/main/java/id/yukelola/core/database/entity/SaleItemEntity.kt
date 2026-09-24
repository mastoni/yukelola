package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.sale.SaleItem

/**
 * Room persistence entity for [SaleItem] line item snapshot.
 * Backed by canonical Contract v1.3.0 Section 3.2.13.
 *
 * Invariant:
 * Immutable line item snapshot representing a Product sold within a Sale transaction.
 * Preserves historical unit price, cost price, and name at the moment of sale.
 */
@Entity(
    tableName = "sale_items",
    foreignKeys = [
        ForeignKey(
            entity = SaleEntity::class,
            parentColumns = ["id"],
            childColumns = ["sale_id"],
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
        Index(value = ["sale_id"]),
        Index(value = ["product_id"])
    ]
)
data class SaleItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "sale_id")
    val saleId: String,
    @ColumnInfo(name = "product_id")
    val productId: String,
    @ColumnInfo(name = "product_name")
    val productName: String,
    @ColumnInfo(name = "unit")
    val unit: String = "PCS",
    @ColumnInfo(name = "unit_price")
    val unitPrice: Long,
    @ColumnInfo(name = "cost_price")
    val costPrice: Long = 0L,
    @ColumnInfo(name = "quantity")
    val quantity: Double = 1.0,
    @ColumnInfo(name = "discount_amount")
    val discountAmount: Long = 0L,
    @ColumnInfo(name = "subtotal")
    val subtotal: Long
)
