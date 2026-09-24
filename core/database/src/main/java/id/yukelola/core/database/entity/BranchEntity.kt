package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room persistence entity for the operational [Branch] aggregate.
 * Backed by canonical Contract v1.3.0 Section 3.
 * Preserves strict Business ownership boundary via foreign key to [BusinessEntity].
 */
@Entity(
    tableName = "branches",
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
        Index(value = ["business_id", "code"], unique = true)
    ]
)
data class BranchEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "code")
    val code: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "profile_name")
    val profileName: String,
    @ColumnInfo(name = "profile_phone")
    val profilePhone: String? = null,
    @ColumnInfo(name = "profile_address")
    val profileAddress: String? = null,
    @ColumnInfo(name = "receipt_header")
    val receiptHeader: String? = null,
    @ColumnInfo(name = "receipt_footer")
    val receiptFooter: String? = null,
    @ColumnInfo(name = "currency")
    val currency: String = "IDR",
    @ColumnInfo(name = "timezone")
    val timezone: String = "Asia/Jakarta",
    @ColumnInfo(name = "allow_negative_stock")
    val allowNegativeStock: Boolean = false,
    @ColumnInfo(name = "business_model")
    val businessModel: String,
    @ColumnInfo(name = "enabled_capabilities")
    val enabledCapabilities: String = "",
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
