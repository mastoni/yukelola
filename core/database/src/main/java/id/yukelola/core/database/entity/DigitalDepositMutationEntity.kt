package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.digital.DigitalDepositMutation

/**
 * Room persistence entity for [DigitalDepositMutation].
 * Backed by canonical Contract v1.3.0 and Financial Effect Matrix Section 1 & 3.
 *
 * Invariant:
 * Traceable historical movement record of digital deposit funds.
 */
@Entity(
    tableName = "digital_deposit_mutations",
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
            entity = DigitalDepositAccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["business_id"]),
        Index(value = ["branch_id"]),
        Index(value = ["account_id"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "account_id"]),
        Index(value = ["business_id", "branch_id", "account_id"])
    ]
)
data class DigitalDepositMutationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "branch_id")
    val branchId: String,
    @ColumnInfo(name = "account_id")
    val accountId: String,
    @ColumnInfo(name = "mutation_type")
    val mutationType: String,
    @ColumnInfo(name = "amount")
    val amount: Long,
    @ColumnInfo(name = "balance_before")
    val balanceBefore: Long,
    @ColumnInfo(name = "balance_after")
    val balanceAfter: Long,
    @ColumnInfo(name = "reference_id")
    val referenceId: String? = null,
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
