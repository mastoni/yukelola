package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.InquiryEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.digital.Inquiry
import id.yukelola.core.domain.model.digital.InquiryStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InquiryMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly with active initiated inquiry`() {
        val attribution = TransactionAttribution(
            businessId = "biz-01",
            branchId = "branch-01",
            userId = "user-01",
            deviceId = "dev-01",
            cashierSessionId = "sess-01",
            createdAt = 1700000000000L
        )

        val domain = Inquiry(
            id = "inq-01",
            attribution = attribution,
            targetNumber = "12345678901",
            productCode = "PLN_POSTPAID",
            customerName = null,
            billAmount = null,
            adminFee = 2500L,
            inquiryDataJson = null,
            status = InquiryStatus.INITIATED,
            inquiryReference = null,
            failureReason = null,
            createdAt = 1700000000000L,
            expiresAt = null,
            updatedAt = 1700000000000L
        )

        val entity = domain.toEntity()

        assertEquals("inq-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("user-01", entity.userId)
        assertEquals("dev-01", entity.deviceId)
        assertEquals("sess-01", entity.cashierSessionId)
        assertEquals("12345678901", entity.targetNumber)
        assertEquals("PLN_POSTPAID", entity.productCode)
        assertNull(entity.customerName)
        assertNull(entity.billAmount)
        assertEquals(2500L, entity.adminFee)
        assertNull(entity.inquiryDataJson)
        assertEquals("INITIATED", entity.status)
        assertNull(entity.inquiryReference)
        assertNull(entity.failureReason)
        assertEquals(1700000000000L, entity.createdAt)
        assertNull(entity.expiresAt)
        assertEquals(1700000000000L, entity.updatedAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly with full successful bill inquiry`() {
        val entity = InquiryEntity(
            id = "inq-02",
            businessId = "biz-02",
            branchId = "branch-02",
            userId = "user-02",
            deviceId = "dev-02",
            cashierSessionId = null,
            targetNumber = "5123456789",
            productCode = "BPJS_KES",
            customerName = "Budi Santoso",
            billAmount = 150000L,
            adminFee = 2500L,
            inquiryDataJson = "{\"period\":\"2026-09\",\"members\":3}",
            status = "SUCCESS",
            inquiryReference = "INQ-REF-2026-001",
            failureReason = null,
            createdAt = 1700000000000L,
            expiresAt = 1700000300000L,
            updatedAt = 1700000005000L
        )

        val domain = entity.toDomain()

        assertEquals("inq-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals("user-02", domain.attribution.userId)
        assertEquals("dev-02", domain.attribution.deviceId)
        assertNull(domain.attribution.cashierSessionId)
        assertEquals("5123456789", domain.targetNumber)
        assertEquals("BPJS_KES", domain.productCode)
        assertEquals("Budi Santoso", domain.customerName)
        assertEquals(150000L, domain.billAmount)
        assertEquals(2500L, domain.adminFee)
        assertEquals(152500L, domain.totalBillAmount)
        assertEquals("{\"period\":\"2026-09\",\"members\":3}", domain.inquiryDataJson)
        assertEquals(InquiryStatus.SUCCESS, domain.status)
        assertEquals("INQ-REF-2026-001", domain.inquiryReference)
        assertNull(domain.failureReason)
        assertEquals(1700000000000L, domain.createdAt)
        assertEquals(1700000300000L, domain.expiresAt)
        assertEquals(1700000005000L, domain.updatedAt)
    }

    @Test
    fun `full round trip preserves exact domain identity and precision across lifecycle statuses`() {
        val attribution = TransactionAttribution(
            businessId = "biz-03",
            branchId = "branch-03",
            userId = "user-03",
            deviceId = "dev-03",
            cashierSessionId = "sess-03",
            createdAt = 1700000000000L
        )

        val statuses = listOf(
            InquiryStatus.INITIATED to Triple(null, null, null),
            InquiryStatus.SUCCESS to Triple("Ahmad Yani", 275000L, "REF-SUC-01"),
            InquiryStatus.FAILED to Triple(null, null, null),
            InquiryStatus.EXPIRED to Triple("Ahmad Yani", 275000L, "REF-SUC-01")
        )

        statuses.forEachIndexed { index, (status, details) ->
            val (name, amount, ref) = details
            val reason = if (status == InquiryStatus.FAILED) "CUSTOMER_NUMBER_NOT_FOUND" else null
            val original = Inquiry(
                id = "inq-round-$index",
                attribution = attribution,
                targetNumber = "0219876543$index",
                productCode = "TELKOM_INDIHOME",
                customerName = name,
                billAmount = amount,
                adminFee = 3000L,
                inquiryDataJson = if (name != null) "{\"speed\":\"100Mbps\"}" else null,
                status = status,
                inquiryReference = ref,
                failureReason = reason,
                createdAt = 1700000000000L,
                expiresAt = if (status == InquiryStatus.SUCCESS || status == InquiryStatus.EXPIRED) 1700000600000L else null,
                updatedAt = 1700000010000L + index
            )

            val entity = original.toEntity()
            val restored = entity.toDomain()

            assertEquals(original, restored)
            if (amount != null) {
                assertEquals(amount + 3000L, restored.totalBillAmount)
            }
            assertEquals(status, restored.status)
        }
    }
}
