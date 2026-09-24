package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.digital.Inquiry

/**
 * Room persistence entity for [Inquiry].
 * Backed by canonical Contract v1.3.0 Section 3.2.16.
 *
 * Invariant:
 * Read-only pre-transaction validation and bill inquiry record before digital settlement.
 * Bound to Business and Branch via immutable attribution.
 */
@Entity(
    tableName = "inquiries",
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
        Index(value = ["status"]),
        Index(value = ["product_code"]),
        Index(value = ["target_number"]),
        Index(value = ["inquiry_reference"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "branch_id", "status"])
    ]
)
data class InquiryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
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
    @ColumnInfo(name = "target_number")
    val targetNumber: String,
    @ColumnInfo(name = "product_code")
    val productCode: String,
    @ColumnInfo(name = "customer_name")
    val customerName: String? = null,
    @ColumnInfo(name = "bill_amount")
    val billAmount: Long? = null,
    @ColumnInfo(name = "admin_fee")
    val adminFee: Long = 0L,
    @ColumnInfo(name = "inquiry_data_json")
    val inquiryDataJson: String? = null,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "inquiry_reference")
    val inquiryReference: String? = null,
    @ColumnInfo(name = "failure_reason")
    val failureReason: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "expires_at")
    val expiresAt: Long? = null,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
