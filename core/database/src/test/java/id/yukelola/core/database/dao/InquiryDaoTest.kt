package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.InquiryEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.digital.Inquiry
import id.yukelola.core.domain.model.digital.InquiryStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InquiryDaoTest {

    @Test
    fun `InquiryDao interface defines required persistence operations`() {
        val upsertMethod = InquiryDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("InquiryDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(InquiryEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = InquiryDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("InquiryDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(InquiryEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            InquiryDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("InquiryDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(InquiryEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByIdAndBusinessIdAndBranchIdMethod =
            InquiryDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessIdAndBranchId" }
        assertNotNull(
            "InquiryDao must declare findByIdAndBusinessIdAndBranchId method",
            findByIdAndBusinessIdAndBranchIdMethod
        )
        assertEquals(3, findByIdAndBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[2])
        assertEquals(InquiryEntity::class.java, findByIdAndBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdMethod =
            InquiryDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("InquiryDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            InquiryDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull(
            "InquiryDao must declare findByBusinessIdAndBranchId method",
            findByBusinessIdAndBranchIdMethod
        )
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdAndBranchIdAndStatusMethod =
            InquiryDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndStatus" }
        assertNotNull(
            "InquiryDao must declare findByBusinessIdAndBranchIdAndStatus method",
            findByBusinessIdAndBranchIdAndStatusMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndStatusMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndStatusMethod.returnType)

        val findByBusinessIdAndBranchIdAndTargetNumberMethod =
            InquiryDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndTargetNumber" }
        assertNotNull(
            "InquiryDao must declare findByBusinessIdAndBranchIdAndTargetNumber method",
            findByBusinessIdAndBranchIdAndTargetNumberMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndTargetNumberMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndTargetNumberMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndTargetNumberMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndTargetNumberMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndTargetNumberMethod.returnType)

        val findByBusinessIdAndInquiryReferenceMethod =
            InquiryDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndInquiryReference" }
        assertNotNull(
            "InquiryDao must declare findByBusinessIdAndInquiryReference method",
            findByBusinessIdAndInquiryReferenceMethod
        )
        assertEquals(2, findByBusinessIdAndInquiryReferenceMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndInquiryReferenceMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndInquiryReferenceMethod.parameterTypes[1])
        assertEquals(InquiryEntity::class.java, findByBusinessIdAndInquiryReferenceMethod.returnType)
    }

    @Test
    fun `Room KSP generates InquiryDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.InquiryDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement InquiryDao",
            InquiryDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : InquiryDao {
            private val storage = mutableMapOf<String, InquiryEntity>()

            override fun upsert(inquiry: InquiryEntity) {
                storage[inquiry.id] = inquiry
            }

            override fun findById(id: String): InquiryEntity? = storage[id]

            override fun findByIdAndBusinessId(id: String, businessId: String): InquiryEntity? =
                storage[id]?.takeIf { it.businessId == businessId }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): InquiryEntity? =
                storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }

            override fun findByBusinessId(businessId: String): List<InquiryEntity> =
                storage.values.filter { it.businessId == businessId }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<InquiryEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndStatus(
                businessId: String,
                branchId: String,
                status: String
            ): List<InquiryEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.status == status
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndTargetNumber(
                businessId: String,
                branchId: String,
                targetNumber: String
            ): List<InquiryEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.targetNumber == targetNumber
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndInquiryReference(
                businessId: String,
                inquiryReference: String
            ): InquiryEntity? =
                storage.values.firstOrNull {
                    it.businessId == businessId && it.inquiryReference == inquiryReference
                }
        }

        val domain = Inquiry(
            id = "inq-dao-01",
            attribution = TransactionAttribution(
                businessId = "biz-01",
                branchId = "branch-01",
                userId = "user-01",
                deviceId = "dev-01",
                cashierSessionId = "sess-01",
                createdAt = 1700000000000L
            ),
            targetNumber = "14123456789",
            productCode = "PLN_PASCABAYAR",
            customerName = "Sri Wahyuni",
            billAmount = 210000L,
            adminFee = 3000L,
            inquiryDataJson = "{\"stand_meter\":\"012345-012567\"}",
            status = InquiryStatus.SUCCESS,
            inquiryReference = "REF-INQ-PLN-001",
            createdAt = 1700000000000L,
            expiresAt = 1700000600000L,
            updatedAt = 1700000005000L
        )

        fakeDao.upsert(domain.toEntity())

        val retrievedEntity = fakeDao.findById("inq-dao-01")
        assertNotNull(retrievedEntity)
        val retrievedDomain = retrievedEntity!!.toDomain()

        assertEquals(domain, retrievedDomain)
        assertEquals(InquiryStatus.SUCCESS, retrievedDomain.status)
        assertEquals("Sri Wahyuni", retrievedDomain.customerName)
        assertEquals(210000L, retrievedDomain.billAmount)
        assertEquals(213000L, retrievedDomain.totalBillAmount)
    }

    @Test
    fun `business and branch and status and target and inquiryReference isolation strictly enforced`() {
        val fakeDao = object : InquiryDao {
            private val storage = mutableMapOf<String, InquiryEntity>()

            override fun upsert(inquiry: InquiryEntity) {
                storage[inquiry.id] = inquiry
            }

            override fun findById(id: String): InquiryEntity? = storage[id]

            override fun findByIdAndBusinessId(id: String, businessId: String): InquiryEntity? =
                storage[id]?.takeIf { it.businessId == businessId }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): InquiryEntity? =
                storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }

            override fun findByBusinessId(businessId: String): List<InquiryEntity> =
                storage.values.filter { it.businessId == businessId }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<InquiryEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndStatus(
                businessId: String,
                branchId: String,
                status: String
            ): List<InquiryEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.status == status
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndTargetNumber(
                businessId: String,
                branchId: String,
                targetNumber: String
            ): List<InquiryEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.targetNumber == targetNumber
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndInquiryReference(
                businessId: String,
                inquiryReference: String
            ): InquiryEntity? =
                storage.values.firstOrNull {
                    it.businessId == businessId && it.inquiryReference == inquiryReference
                }
        }

        val inq1 = Inquiry(
            id = "inq-iso-01",
            attribution = TransactionAttribution("biz-01", "branch-a", "user-01", "dev-01", createdAt = 1700000000001L),
            targetNumber = "0811111111",
            productCode = "HALO_POSTPAID",
            customerName = "Cust A",
            billAmount = 100000L,
            status = InquiryStatus.SUCCESS,
            inquiryReference = "REF-001"
        )

        val inq2 = Inquiry(
            id = "inq-iso-02",
            attribution = TransactionAttribution("biz-01", "branch-b", "user-01", "dev-01", createdAt = 1700000000002L),
            targetNumber = "0822222222",
            productCode = "MATRIX_POSTPAID",
            status = InquiryStatus.FAILED,
            failureReason = "INVALID_ACCOUNT"
        )

        val inq3 = Inquiry(
            id = "inq-iso-03",
            attribution = TransactionAttribution("biz-02", "branch-a", "user-02", "dev-02", createdAt = 1700000000003L),
            targetNumber = "0811111111",
            productCode = "HALO_POSTPAID",
            customerName = "Cust A",
            billAmount = 100000L,
            status = InquiryStatus.SUCCESS,
            inquiryReference = "REF-001"
        )

        fakeDao.upsert(inq1.toEntity())
        fakeDao.upsert(inq2.toEntity())
        fakeDao.upsert(inq3.toEntity())

        // Business isolation on findById
        assertNull(fakeDao.findByIdAndBusinessId("inq-iso-01", "biz-02"))
        assertNotNull(fakeDao.findByIdAndBusinessId("inq-iso-01", "biz-01"))

        // Branch isolation on findById
        assertNull(fakeDao.findByIdAndBusinessIdAndBranchId("inq-iso-01", "biz-01", "branch-b"))
        assertNotNull(fakeDao.findByIdAndBusinessIdAndBranchId("inq-iso-01", "biz-01", "branch-a"))

        // Multi-record business isolation
        val biz1Inquiries = fakeDao.findByBusinessId("biz-01")
        assertEquals(2, biz1Inquiries.size)
        assertEquals(listOf("inq-iso-02", "inq-iso-01"), biz1Inquiries.map { it.id })

        val biz2Inquiries = fakeDao.findByBusinessId("biz-02")
        assertEquals(1, biz2Inquiries.size)
        assertEquals("inq-iso-03", biz2Inquiries[0].id)

        // Status isolation
        val successBranchA = fakeDao.findByBusinessIdAndBranchIdAndStatus("biz-01", "branch-a", "SUCCESS")
        assertEquals(1, successBranchA.size)
        assertEquals("inq-iso-01", successBranchA[0].id)

        val failedBranchB = fakeDao.findByBusinessIdAndBranchIdAndStatus("biz-01", "branch-b", "FAILED")
        assertEquals(1, failedBranchB.size)
        assertEquals("inq-iso-02", failedBranchB[0].id)

        // Target number isolation
        val target1Biz1 = fakeDao.findByBusinessIdAndBranchIdAndTargetNumber("biz-01", "branch-a", "0811111111")
        assertEquals(1, target1Biz1.size)
        assertEquals("inq-iso-01", target1Biz1[0].id)

        // Inquiry reference isolation
        val ref1Biz1 = fakeDao.findByBusinessIdAndInquiryReference("biz-01", "REF-001")
        assertNotNull(ref1Biz1)
        assertEquals("inq-iso-01", ref1Biz1!!.id)

        val ref1Biz2 = fakeDao.findByBusinessIdAndInquiryReference("biz-02", "REF-001")
        assertNotNull(ref1Biz2)
        assertEquals("inq-iso-03", ref1Biz2!!.id)
    }
}
