package id.yukelola.core.domain.model.digital

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class InquiryLifecycleTest {

    private val attribution = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-01",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    private val validInquiry = Inquiry(
        id = "inq-01",
        attribution = attribution,
        targetNumber = "512345678901",
        productCode = "PLNPOSTPAID",
        createdAt = 1000L
    )

    @Test
    fun `inquiry executes happy path INITIATED to SUCCESS to EXPIRED`() {
        assertEquals(InquiryStatus.INITIATED, validInquiry.status)
        assertFalse(validInquiry.isTerminal)

        // Step 1: INITIATED -> SUCCESS
        val successInquiry = validInquiry.markSuccess(
            customerName = "BAPAK BUDI SANTOSO",
            billAmount = 275000L,
            adminFee = 3000L,
            inquiryReference = "INQ-REF-PLN-123",
            expiresAt = 1300L,
            timestamp = 1020L
        )

        assertEquals(InquiryStatus.SUCCESS, successInquiry.status)
        assertEquals("BAPAK BUDI SANTOSO", successInquiry.customerName)
        assertEquals(275000L, successInquiry.billAmount)
        assertEquals(3000L, successInquiry.adminFee)
        assertEquals(278000L, successInquiry.totalBillAmount)
        assertEquals(1300L, successInquiry.expiresAt)
        assertFalse(successInquiry.isTerminal)

        // Step 2: SUCCESS -> EXPIRED (Validity window closed)
        val expiredInquiry = successInquiry.markExpired(1301L)
        assertEquals(InquiryStatus.EXPIRED, expiredInquiry.status)
        assertTrue(expiredInquiry.isTerminal)
    }

    @Test
    fun `inquiry executes failure path INITIATED to FAILED`() {
        val failedInquiry = validInquiry.markFailed(
            failureReason = "Customer ID not found or bill already paid",
            timestamp = 1020L
        )

        assertEquals(InquiryStatus.FAILED, failedInquiry.status)
        assertEquals("Customer ID not found or bill already paid", failedInquiry.failureReason)
        assertTrue(failedInquiry.isTerminal)
    }

    @Test
    fun `terminal inquiry states reject further transitions`() {
        val failedInquiry = validInquiry.markFailed("Invalid ID", 1020L)

        // Attempting to mark SUCCESS after FAILED
        assertThrows(IllegalArgumentException::class.java) {
            failedInquiry.markSuccess("Budi", 100000L, 2500L, null, null, 1050L)
        }

        val successInquiry = validInquiry.markSuccess("Budi", 100000L, 2500L, null, null, 1020L)
        val expiredInquiry = successInquiry.markExpired(1100L)

        // Attempting to transition from EXPIRED to SUCCESS
        assertThrows(IllegalArgumentException::class.java) {
            expiredInquiry.markSuccess("Budi", 100000L, 2500L, null, null, 1200L)
        }
    }

    @Test
    fun `invalid direct transitions are strictly rejected`() {
        // Cannot jump directly from INITIATED to EXPIRED
        assertThrows(IllegalArgumentException::class.java) {
            validInquiry.markExpired(1100L)
        }
    }
}
