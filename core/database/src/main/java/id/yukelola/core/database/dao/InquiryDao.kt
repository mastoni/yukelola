package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.InquiryEntity

/**
 * Room Data Access Object for [InquiryEntity].
 * Provides minimal deterministic persistence operations for digital inquiries.
 */
@Dao
interface InquiryDao {

    @Upsert
    fun upsert(inquiry: InquiryEntity)

    @Query("SELECT * FROM inquiries WHERE id = :id")
    fun findById(id: String): InquiryEntity?

    @Query("SELECT * FROM inquiries WHERE id = :id AND business_id = :businessId")
    fun findByIdAndBusinessId(id: String, businessId: String): InquiryEntity?

    @Query("SELECT * FROM inquiries WHERE id = :id AND business_id = :businessId AND branch_id = :branchId")
    fun findByIdAndBusinessIdAndBranchId(id: String, businessId: String, branchId: String): InquiryEntity?

    @Query("SELECT * FROM inquiries WHERE business_id = :businessId ORDER BY created_at DESC")
    fun findByBusinessId(businessId: String): List<InquiryEntity>

    @Query("SELECT * FROM inquiries WHERE business_id = :businessId AND branch_id = :branchId ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchId(businessId: String, branchId: String): List<InquiryEntity>

    @Query("SELECT * FROM inquiries WHERE business_id = :businessId AND branch_id = :branchId AND status = :status ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndStatus(
        businessId: String,
        branchId: String,
        status: String
    ): List<InquiryEntity>

    @Query("SELECT * FROM inquiries WHERE business_id = :businessId AND branch_id = :branchId AND target_number = :targetNumber ORDER BY created_at DESC")
    fun findByBusinessIdAndBranchIdAndTargetNumber(
        businessId: String,
        branchId: String,
        targetNumber: String
    ): List<InquiryEntity>

    @Query("SELECT * FROM inquiries WHERE business_id = :businessId AND inquiry_reference = :inquiryReference")
    fun findByBusinessIdAndInquiryReference(
        businessId: String,
        inquiryReference: String
    ): InquiryEntity?
}
