package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.catalog.ProductUnit

/**
 * Room persistence entity for [ProductUnit].
 * Backed by canonical Contract v1.3.0 and catalog domain model.
 *
 * Invariant: ProductUnit is business-scoped measurement unit definition.
 */
@Entity(
    tableName = "product_units",
    foreignKeys = [
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["id"],
            childColumns = ["business_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["business_id"])
    ]
)
data class ProductUnitEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "symbol")
    val symbol: String,
    @ColumnInfo(name = "conversion_factor")
    val conversionFactor: Double = 1.0
)
