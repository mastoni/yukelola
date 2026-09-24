package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.serviceorder.ServiceOrderItem

/**
 * Room persistence entity for [ServiceOrderItem] line item snapshot.
 * Backed by canonical Contract v1.3.0 Section 3.2.14.
 *
 * Invariant:
 * Immutable line item snapshot representing a service labor or physical part within a ServiceOrder.
 * Preserves historical unit price, cost price, item descriptions, and snapshot at time of service order.
 */
@Entity(
    tableName = "service_order_items",
    foreignKeys = [
        ForeignKey(
            entity = ServiceOrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["order_id"],
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
        Index(value = ["order_id"]),
        Index(value = ["product_id"]),
        Index(value = ["item_type"])
    ]
)
data class ServiceOrderItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "order_id")
    val orderId: String,
    @ColumnInfo(name = "product_id")
    val productId: String,
    @ColumnInfo(name = "product_name")
    val productName: String,
    @ColumnInfo(name = "item_type")
    val itemType: String,
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
