package id.yukelola.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import id.yukelola.core.domain.model.digital.DigitalTransaction

/**
 * Room persistence entity for [DigitalTransaction].
 * Backed by canonical Contract v1.3.0 Section 3.2.16.
 *
 * Invariant:
 * Electronic digital transaction record for vouchers, tokens, and bill payments.
 * Bound to Business and Branch via immutable attribution.
 */
@Entity(
    tableName = "digital_transactions",
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
        Index(value = ["fulfillment_status"]),
        Index(value = ["product_code"]),
        Index(value = ["target_number"]),
        Index(value = ["sale_id"]),
        Index(value = ["deposit_mutation_id"]),
        Index(value = ["provider_reference"]),
        Index(value = ["business_id", "branch_id"]),
        Index(value = ["business_id", "branch_id", "fulfillment_status"])
    ]
)
data class DigitalTransactionEntity(
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
    @ColumnInfo(name = "denomination")
    val denomination: Long,
    @ColumnInfo(name = "cost_price")
    val costPrice: Long,
    @ColumnInfo(name = "selling_price")
    val sellingPrice: Long,
    @ColumnInfo(name = "sale_id")
    val saleId: String? = null,
    @ColumnInfo(name = "deposit_mutation_id")
    val depositMutationId: String? = null,
    @ColumnInfo(name = "fulfillment_status")
    val fulfillmentStatus: String,
    @ColumnInfo(name = "provider_reference")
    val providerReference: String? = null,
    @ColumnInfo(name = "failure_reason")
    val failureReason: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
