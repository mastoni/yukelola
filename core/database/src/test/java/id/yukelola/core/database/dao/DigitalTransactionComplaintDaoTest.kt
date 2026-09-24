package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.DigitalTransactionComplaintEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.digital.DigitalTransactionComplaint
import id.yukelola.core.domain.model.digital.DigitalTransactionComplaintReason
import id.yukelola.core.domain.model.digital.DigitalTransactionComplaintStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DigitalTransactionComplaintDaoTest {

    @Test
    fun `DigitalTransactionComplaintDao interface defines required persistence operations`() {
        val upsertMethod =
            DigitalTransactionComplaintDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("DigitalTransactionComplaintDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(DigitalTransactionComplaintEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod =
            DigitalTransactionComplaintDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("DigitalTransactionComplaintDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(DigitalTransactionComplaintEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            DigitalTransactionComplaintDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull(
            "DigitalTransactionComplaintDao must declare findByIdAndBusinessId method",
            findByIdAndBusinessIdMethod
        )
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(DigitalTransactionComplaintEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByIdAndBusinessIdAndBranchIdMethod =
            DigitalTransactionComplaintDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessIdAndBranchId" }
        assertNotNull(
            "DigitalTransactionComplaintDao must declare findByIdAndBusinessIdAndBranchId method",
            findByIdAndBusinessIdAndBranchIdMethod
        )
        assertEquals(3, findByIdAndBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[2])
        assertEquals(DigitalTransactionComplaintEntity::class.java, findByIdAndBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdMethod =
            DigitalTransactionComplaintDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull(
            "DigitalTransactionComplaintDao must declare findByBusinessId method",
            findByBusinessIdMethod
        )
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            DigitalTransactionComplaintDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull(
            "DigitalTransactionComplaintDao must declare findByBusinessIdAndBranchId method",
            findByBusinessIdAndBranchIdMethod
        )
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdAndBranchIdAndStatusMethod =
            DigitalTransactionComplaintDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndStatus" }
        assertNotNull(
            "DigitalTransactionComplaintDao must declare findByBusinessIdAndBranchIdAndStatus method",
            findByBusinessIdAndBranchIdAndStatusMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndStatusMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndStatusMethod.returnType)

        val findByBusinessIdAndDigitalTransactionIdMethod =
            DigitalTransactionComplaintDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndDigitalTransactionId" }
        assertNotNull(
            "DigitalTransactionComplaintDao must declare findByBusinessIdAndDigitalTransactionId method",
            findByBusinessIdAndDigitalTransactionIdMethod
        )
        assertEquals(2, findByBusinessIdAndDigitalTransactionIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndDigitalTransactionIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndDigitalTransactionIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndDigitalTransactionIdMethod.returnType)

        val findByBusinessIdAndBranchIdAndReasonMethod =
            DigitalTransactionComplaintDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndReason" }
        assertNotNull(
            "DigitalTransactionComplaintDao must declare findByBusinessIdAndBranchIdAndReason method",
            findByBusinessIdAndBranchIdAndReasonMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndReasonMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndReasonMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndReasonMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndReasonMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndReasonMethod.returnType)
    }

    @Test
    fun `Room KSP generates DigitalTransactionComplaintDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.DigitalTransactionComplaintDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement DigitalTransactionComplaintDao",
            DigitalTransactionComplaintDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : DigitalTransactionComplaintDao {
            private val storage = mutableMapOf<String, DigitalTransactionComplaintEntity>()

            override fun upsert(complaint: DigitalTransactionComplaintEntity) {
                storage[complaint.id] = complaint
            }

            override fun findById(id: String): DigitalTransactionComplaintEntity? = storage[id]

            override fun findByIdAndBusinessId(
                id: String,
                businessId: String
            ): DigitalTransactionComplaintEntity? =
                storage[id]?.takeIf { it.businessId == businessId }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): DigitalTransactionComplaintEntity? =
                storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }

            override fun findByBusinessId(businessId: String): List<DigitalTransactionComplaintEntity> =
                storage.values.filter { it.businessId == businessId }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<DigitalTransactionComplaintEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndStatus(
                businessId: String,
                branchId: String,
                status: String
            ): List<DigitalTransactionComplaintEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.status == status
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndDigitalTransactionId(
                businessId: String,
                digitalTransactionId: String
            ): List<DigitalTransactionComplaintEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.digitalTransactionId == digitalTransactionId
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndReason(
                businessId: String,
                branchId: String,
                reason: String
            ): List<DigitalTransactionComplaintEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.reason == reason
                }.sortedByDescending { it.createdAt }
        }

        val domain = DigitalTransactionComplaint(
            id = "comp-dao-01",
            digitalTransactionId = "dt-100",
            attribution = TransactionAttribution(
                businessId = "biz-01",
                branchId = "branch-01",
                userId = "user-01",
                deviceId = "dev-01",
                cashierSessionId = "sess-01",
                createdAt = 1700000000000L
            ),
            reason = DigitalTransactionComplaintReason.TRANSACTION_STUCK,
            description = "Transaction pending for 2 hours",
            status = DigitalTransactionComplaintStatus.INVESTIGATING,
            createdAt = 1700000000000L,
            updatedAt = 1700000005000L
        )

        fakeDao.upsert(domain.toEntity())

        val retrievedEntity = fakeDao.findById("comp-dao-01")
        assertNotNull(retrievedEntity)
        val retrievedDomain = retrievedEntity!!.toDomain()

        assertEquals(domain, retrievedDomain)
        assertEquals(DigitalTransactionComplaintStatus.INVESTIGATING, retrievedDomain.status)
        assertEquals(DigitalTransactionComplaintReason.TRANSACTION_STUCK, retrievedDomain.reason)
        assertEquals("dt-100", retrievedDomain.digitalTransactionId)
    }

    @Test
    fun `business and branch and status and digitalTransactionId and reason isolation strictly enforced`() {
        val fakeDao = object : DigitalTransactionComplaintDao {
            private val storage = mutableMapOf<String, DigitalTransactionComplaintEntity>()

            override fun upsert(complaint: DigitalTransactionComplaintEntity) {
                storage[complaint.id] = complaint
            }

            override fun findById(id: String): DigitalTransactionComplaintEntity? = storage[id]

            override fun findByIdAndBusinessId(
                id: String,
                businessId: String
            ): DigitalTransactionComplaintEntity? =
                storage[id]?.takeIf { it.businessId == businessId }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): DigitalTransactionComplaintEntity? =
                storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }

            override fun findByBusinessId(businessId: String): List<DigitalTransactionComplaintEntity> =
                storage.values.filter { it.businessId == businessId }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<DigitalTransactionComplaintEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndStatus(
                businessId: String,
                branchId: String,
                status: String
            ): List<DigitalTransactionComplaintEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.status == status
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndDigitalTransactionId(
                businessId: String,
                digitalTransactionId: String
            ): List<DigitalTransactionComplaintEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.digitalTransactionId == digitalTransactionId
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndReason(
                businessId: String,
                branchId: String,
                reason: String
            ): List<DigitalTransactionComplaintEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.reason == reason
                }.sortedByDescending { it.createdAt }
        }

        val comp1 = DigitalTransactionComplaint(
            id = "comp-iso-01",
            digitalTransactionId = "dt-01",
            attribution = TransactionAttribution("biz-01", "branch-a", "user-01", "dev-01", createdAt = 1700000000001L),
            reason = DigitalTransactionComplaintReason.PRODUCT_NOT_RECEIVED,
            description = "Issue 1",
            status = DigitalTransactionComplaintStatus.OPEN
        )

        val comp2 = DigitalTransactionComplaint(
            id = "comp-iso-02",
            digitalTransactionId = "dt-02",
            attribution = TransactionAttribution("biz-01", "branch-b", "user-01", "dev-01", createdAt = 1700000000002L),
            reason = DigitalTransactionComplaintReason.WRONG_RESULT,
            description = "Issue 2",
            status = DigitalTransactionComplaintStatus.RESOLVED,
            resolutionNotes = "Fixed",
            resolvedAt = 1700000010000L
        )

        val comp3 = DigitalTransactionComplaint(
            id = "comp-iso-03",
            digitalTransactionId = "dt-01",
            attribution = TransactionAttribution("biz-02", "branch-a", "user-02", "dev-02", createdAt = 1700000000003L),
            reason = DigitalTransactionComplaintReason.PRODUCT_NOT_RECEIVED,
            description = "Issue 3",
            status = DigitalTransactionComplaintStatus.OPEN
        )

        fakeDao.upsert(comp1.toEntity())
        fakeDao.upsert(comp2.toEntity())
        fakeDao.upsert(comp3.toEntity())

        // Business isolation on findById
        assertNull(fakeDao.findByIdAndBusinessId("comp-iso-01", "biz-02"))
        assertNotNull(fakeDao.findByIdAndBusinessId("comp-iso-01", "biz-01"))

        // Branch isolation on findById
        assertNull(fakeDao.findByIdAndBusinessIdAndBranchId("comp-iso-01", "biz-01", "branch-b"))
        assertNotNull(fakeDao.findByIdAndBusinessIdAndBranchId("comp-iso-01", "biz-01", "branch-a"))

        // Multi-record business isolation
        val biz1Complaints = fakeDao.findByBusinessId("biz-01")
        assertEquals(2, biz1Complaints.size)
        assertEquals(listOf("comp-iso-02", "comp-iso-01"), biz1Complaints.map { it.id })

        val biz2Complaints = fakeDao.findByBusinessId("biz-02")
        assertEquals(1, biz2Complaints.size)
        assertEquals("comp-iso-03", biz2Complaints[0].id)

        // Status isolation
        val openBranchA = fakeDao.findByBusinessIdAndBranchIdAndStatus("biz-01", "branch-a", "OPEN")
        assertEquals(1, openBranchA.size)
        assertEquals("comp-iso-01", openBranchA[0].id)

        val resolvedBranchB = fakeDao.findByBusinessIdAndBranchIdAndStatus("biz-01", "branch-b", "RESOLVED")
        assertEquals(1, resolvedBranchB.size)
        assertEquals("comp-iso-02", resolvedBranchB[0].id)

        // DigitalTransactionId isolation
        val dt1Biz1 = fakeDao.findByBusinessIdAndDigitalTransactionId("biz-01", "dt-01")
        assertEquals(1, dt1Biz1.size)
        assertEquals("comp-iso-01", dt1Biz1[0].id)

        val dt1Biz2 = fakeDao.findByBusinessIdAndDigitalTransactionId("biz-02", "dt-01")
        assertEquals(1, dt1Biz2.size)
        assertEquals("comp-iso-03", dt1Biz2[0].id)

        // Reason isolation
        val notReceivedBranchA = fakeDao.findByBusinessIdAndBranchIdAndReason(
            "biz-01",
            "branch-a",
            "PRODUCT_NOT_RECEIVED"
        )
        assertEquals(1, notReceivedBranchA.size)
        assertEquals("comp-iso-01", notReceivedBranchA[0].id)
    }
}
