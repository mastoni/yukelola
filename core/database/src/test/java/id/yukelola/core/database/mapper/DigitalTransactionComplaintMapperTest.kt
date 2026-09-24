package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.DigitalTransactionComplaintEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.digital.DigitalTransactionComplaint
import id.yukelola.core.domain.model.digital.DigitalTransactionComplaintReason
import id.yukelola.core.domain.model.digital.DigitalTransactionComplaintStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DigitalTransactionComplaintMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly with active open complaint`() {
        val attribution = TransactionAttribution(
            businessId = "biz-01",
            branchId = "branch-01",
            userId = "user-01",
            deviceId = "dev-01",
            cashierSessionId = "sess-01",
            createdAt = 1700000000000L
        )

        val domain = DigitalTransactionComplaint(
            id = "comp-01",
            digitalTransactionId = "dt-01",
            attribution = attribution,
            reason = DigitalTransactionComplaintReason.PRODUCT_NOT_RECEIVED,
            description = "Pulsa 50k has not entered customer phone",
            status = DigitalTransactionComplaintStatus.OPEN,
            resolutionNotes = null,
            resolvedAt = null,
            createdAt = 1700000000000L,
            updatedAt = 1700000000000L
        )

        val entity = domain.toEntity()

        assertEquals("comp-01", entity.id)
        assertEquals("dt-01", entity.digitalTransactionId)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("user-01", entity.userId)
        assertEquals("dev-01", entity.deviceId)
        assertEquals("sess-01", entity.cashierSessionId)
        assertEquals("PRODUCT_NOT_RECEIVED", entity.reason)
        assertEquals("Pulsa 50k has not entered customer phone", entity.description)
        assertEquals("OPEN", entity.status)
        assertNull(entity.resolutionNotes)
        assertNull(entity.resolvedAt)
        assertEquals(1700000000000L, entity.createdAt)
        assertEquals(1700000000000L, entity.updatedAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly with resolved complaint`() {
        val entity = DigitalTransactionComplaintEntity(
            id = "comp-02",
            digitalTransactionId = "dt-02",
            businessId = "biz-02",
            branchId = "branch-02",
            userId = "user-02",
            deviceId = "dev-02",
            cashierSessionId = null,
            reason = "WRONG_RESULT",
            description = "Token was invalid",
            status = "RESOLVED",
            resolutionNotes = "Replacement token issued by provider",
            resolvedAt = 1700000060000L,
            createdAt = 1700000000000L,
            updatedAt = 1700000060000L
        )

        val domain = entity.toDomain()

        assertEquals("comp-02", domain.id)
        assertEquals("dt-02", domain.digitalTransactionId)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals("user-02", domain.attribution.userId)
        assertEquals("dev-02", domain.attribution.deviceId)
        assertNull(domain.attribution.cashierSessionId)
        assertEquals(DigitalTransactionComplaintReason.WRONG_RESULT, domain.reason)
        assertEquals("Token was invalid", domain.description)
        assertEquals(DigitalTransactionComplaintStatus.RESOLVED, domain.status)
        assertEquals("Replacement token issued by provider", domain.resolutionNotes)
        assertEquals(1700000060000L, domain.resolvedAt)
        assertEquals(1700000000000L, domain.createdAt)
        assertEquals(1700000060000L, domain.updatedAt)
    }

    @Test
    fun `full round trip preserves exact domain identity across all complaint reasons and statuses`() {
        val attribution = TransactionAttribution(
            businessId = "biz-03",
            branchId = "branch-03",
            userId = "user-03",
            deviceId = "dev-03",
            cashierSessionId = "sess-03",
            createdAt = 1700000000000L
        )

        val combinations = listOf(
            Pair(DigitalTransactionComplaintReason.PRODUCT_NOT_RECEIVED, DigitalTransactionComplaintStatus.OPEN),
            Pair(DigitalTransactionComplaintReason.WRONG_RESULT, DigitalTransactionComplaintStatus.INVESTIGATING),
            Pair(DigitalTransactionComplaintReason.TRANSACTION_STUCK, DigitalTransactionComplaintStatus.RESOLVED),
            Pair(DigitalTransactionComplaintReason.DESTINATION_PROBLEM, DigitalTransactionComplaintStatus.REJECTED),
            Pair(DigitalTransactionComplaintReason.PROVIDER_RESULT_MISMATCH, DigitalTransactionComplaintStatus.OPEN),
            Pair(DigitalTransactionComplaintReason.CUSTOMER_DISPUTE, DigitalTransactionComplaintStatus.RESOLVED),
            Pair(DigitalTransactionComplaintReason.OTHER, DigitalTransactionComplaintStatus.REJECTED)
        )

        combinations.forEachIndexed { index, (reason, status) ->
            val resolvedAt = if (status == DigitalTransactionComplaintStatus.RESOLVED || status == DigitalTransactionComplaintStatus.REJECTED) {
                1700000050000L + index
            } else null

            val original = DigitalTransactionComplaint(
                id = "comp-round-$index",
                digitalTransactionId = "dt-round-$index",
                attribution = attribution,
                reason = reason,
                description = "Complaint reason: $reason",
                status = status,
                resolutionNotes = if (resolvedAt != null) "Outcome for $reason" else null,
                resolvedAt = resolvedAt,
                createdAt = 1700000000000L,
                updatedAt = 1700000010000L + index
            )

            val entity = original.toEntity()
            val restored = entity.toDomain()

            assertEquals(original, restored)
            assertEquals(reason, restored.reason)
            assertEquals(status, restored.status)
        }
    }
}
