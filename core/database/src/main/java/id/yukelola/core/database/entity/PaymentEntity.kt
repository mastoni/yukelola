package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.payment.Payment

/**
 * Room persistence entity for [Payment].
 * Backed by canonical Contract v1.3.0 Section 3.2.18.
 *
 * Invariant:
 * Financial settlement record applied to a Sale, ServiceOrder, Purchase, or Debt reduction.
 */
@Entity(
    tableName = "payments",
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
        )
    ],
    indices = [
        Index(value = ["business_id"]),
        Index(value = ["branch_id"]),
        Index(value = ["reference_id"]),
        Index(value = ["transaction_type"]),
        Index(value = ["payment_method"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "reference_id"]),
        Index(value = ["business_id", "branch_id", "reference_id"])
    ]
)
data class PaymentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "branch_id")
    val branchId: String,
    @ColumnInfo(name = "transaction_type")
    val transactionType: String,
    @ColumnInfo(name = "reference_id")
    val referenceId: String,
    @ColumnInfo(name = "payment_method")
    val paymentMethod: String,
    @ColumnInfo(name = "amount")
    val amount: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
