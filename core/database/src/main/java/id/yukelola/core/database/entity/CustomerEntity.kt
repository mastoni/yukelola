package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.actor.Customer

/**
 * Room persistence entity for [Customer].
 * Backed by canonical Contract v1.3.0 Section 3.
 *
 * Invariant: Customer profile is owned at Business tenant level with optional home branch scope.
 */
@Entity(
    tableName = "customers",
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
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["business_id"]),
        Index(value = ["branch_id"])
    ]
)
data class CustomerEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "branch_id")
    val branchId: String? = null,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "phone")
    val phone: String? = null,
    @ColumnInfo(name = "debt_balance")
    val debtBalance: Long = 0L,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
