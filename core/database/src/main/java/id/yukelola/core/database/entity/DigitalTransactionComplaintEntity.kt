package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.digital.DigitalTransactionComplaint

/**
 * Room persistence entity for [DigitalTransactionComplaint].
 * Backed by canonical Contract v1.3.0 Section 3.2.16.
 *
 * Invariant:
 * Formal customer or operator complaint / issue report linked to a specific DigitalTransaction.
 * Bound to Business and Branch via immutable attribution.
 */
@Entity(
    tableName = "digital_transaction_complaints",
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
            entity = DigitalTransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["digital_transaction_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["business_id"]),
        Index(value = ["branch_id"]),
        Index(value = ["digital_transaction_id"]),
        Index(value = ["status"]),
        Index(value = ["reason"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "branch_id", "status"]),
        Index(value = ["business_id", "digital_transaction_id"])
    ]
)
data class DigitalTransactionComplaintEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "digital_transaction_id")
    val digitalTransactionId: String,
    @ColumnInfo(name = "business_id")
    val businessId: String,
    @ColumnInfo(name = "branch_id")
    val branchId: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "device_id")
    val deviceId: String,
    @ColumnInfo(name = "cashier_session_id")
    val cashierSessionId: String? = null,
    @ColumnInfo(name = "reason")
    val reason: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "resolution_notes")
    val resolutionNotes: String? = null,
    @ColumnInfo(name = "resolved_at")
    val resolvedAt: Long? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
