package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room persistence entity for the business-level master [Product] catalog.
 * Backed by canonical Contract v1.3.0 Section 3.
 *
 * Invariant: Master Product is business-scoped and does NOT store mutable branch stock/prices.
 */
@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["id"],
            childColumns = ["business_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["business_id"]),
        Index(value = ["business_id", "sku"]),
        Index(value = ["business_id", "barcode"])
    ]
)
data class ProductEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "category_id")
    val categoryId: String? = null,
    @ColumnInfo(name = "sku")
    val sku: String? = null,
    @ColumnInfo(name = "barcode")
    val barcode: String? = null,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "product_type")
    val productType: String,
    @ColumnInfo(name = "base_unit")
    val baseUnit: String = "PCS",
    @ColumnInfo(name = "default_cost_price")
    val defaultCostPrice: Long = 0L,
    @ColumnInfo(name = "default_selling_price")
    val defaultSellingPrice: Long = 0L,
    @ColumnInfo(name = "track_stock")
    val trackStock: Boolean = true,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
