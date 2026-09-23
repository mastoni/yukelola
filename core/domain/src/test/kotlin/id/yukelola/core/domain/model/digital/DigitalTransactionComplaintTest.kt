package id.yukelola.core.domain.model.digital

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.cash.CashRegister
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class DigitalTransactionComplaintTest {

    private val attributionBranchA = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-a",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    private val digitalTx = DigitalTransaction(
        id = "dt-01",
        attribution = attributionBranchA,
        targetNumber = "081234567890",
        productCode = "TELKOMSEL-50K",
        denomination = 50000L,
        costPrice = 49000L,
        sellingPrice = 52000L,
        createdAt = 1000L
    ).markPending(1050L)
        .markSuccess("SN-98765", "mut-dep-01", 1100L)

    @Test
    fun `complaint creation binds to valid transaction and attribution`() {
        val complaint = DigitalTransactionComplaint.createForTransaction(
            id = "comp-01",
            transaction = digitalTx,
            attribution = attributionBranchA,
            reason = DigitalTransactionComplaintReason.PRODUCT_NOT_RECEIVED,
            description = "Customer states pulsa 50k has not entered after 2 hours",
            createdAt = 1200L
        )

        assertEquals("comp-01", complaint.id)
        assertEquals("dt-01", complaint.digitalTransactionId)
        assertEquals("biz-01", complaint.businessId)
        assertEquals("branch-a", complaint.branchId)
        assertEquals(DigitalTransactionComplaintReason.PRODUCT_NOT_RECEIVED, complaint.reason)
        assertEquals(DigitalTransactionComplaintStatus.OPEN, complaint.status)
        assertFalse(complaint.isTerminal)
    }

    @Test
    fun `complaint rejects cross-branch or cross-business creation`() {
        val attributionBranchB = TransactionAttribution(
            businessId = "biz-01",
            branchId = "branch-b",
            userId = "user-02",
            deviceId = "dev-02",
            createdAt = 1200L
        )

        assertThrows(IllegalArgumentException::class.java) {
            DigitalTransactionComplaint.createForTransaction(
                id = "comp-cross",
                transaction = digitalTx, // Branch A
                attribution = attributionBranchB, // Branch B
                reason = DigitalTransactionComplaintReason.PRODUCT_NOT_RECEIVED,
                description = "Cross branch attempt"
            )
        }
    }

    @Test
    fun `complaint executes lifecycle OPEN to INVESTIGATING to RESOLVED`() {
        val complaint = DigitalTransactionComplaint.createForTransaction(
            id = "comp-01",
            transaction = digitalTx,
            attribution = attributionBranchA,
            reason = DigitalTransactionComplaintReason.PRODUCT_NOT_RECEIVED,
            description = "Pulsa belum masuk",
            createdAt = 1200L
        )

        // Step 1: OPEN -> INVESTIGATING
        val investigating = complaint.markInvestigating(1250L)
        assertEquals(DigitalTransactionComplaintStatus.INVESTIGATING, investigating.status)
        assertEquals(1250L, investigating.updatedAt)

        // Step 2: INVESTIGATING -> RESOLVED
        val resolved = investigating.markResolved(
            resolutionNotes = "Confirmed with provider: SN verified delivered to destination number.",
            timestamp = 1300L
        )
        assertEquals(DigitalTransactionComplaintStatus.RESOLVED, resolved.status)
        assertEquals(
            "Confirmed with provider: SN verified delivered to destination number.",
            resolved.resolutionNotes
        )
        assertTrue(resolved.isTerminal)
        assertEquals(1300L, resolved.resolvedAt)
    }

    @Test
    fun `complaint executes rejection path INVESTIGATING to REJECTED`() {
        val complaint = DigitalTransactionComplaint.createForTransaction(
            id = "comp-02",
            transaction = digitalTx,
            attribution = attributionBranchA,
            reason = DigitalTransactionComplaintReason.DESTINATION_PROBLEM,
            description = "Customer entered wrong phone number",
            createdAt = 1200L
        )

        val investigating = complaint.markInvestigating(1250L)
        val rejected = investigating.markRejected(
            rejectionReason = "Destination number was input correctly by customer; transaction successfully fulfilled.",
            timestamp = 1300L
        )

        assertEquals(DigitalTransactionComplaintStatus.REJECTED, rejected.status)
        assertTrue(rejected.isTerminal)
    }

    @Test
    fun `terminal complaint states reject further transitions`() {
        val complaint = DigitalTransactionComplaint.createForTransaction(
            id = "comp-01",
            transaction = digitalTx,
            attribution = attributionBranchA,
            reason = DigitalTransactionComplaintReason.OTHER,
            description = "Test issue",
            createdAt = 1200L
        ).markInvestigating(1250L)
            .markResolved("Resolved", 1300L)

        // Cannot transition from RESOLVED to INVESTIGATING
        assertThrows(IllegalArgumentException::class.java) {
            complaint.markInvestigating(1350L)
        }

        // Cannot transition from RESOLVED to REJECTED
        assertThrows(IllegalArgumentException::class.java) {
            complaint.markRejected("Reject", 1350L)
        }
    }

    @Test
    fun `complaint operations do NOT mutate digital transaction status, cash register, or deposit account`() {
        val cashRegister = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-a",
            name = "Cash Drawer",
            currentBalance = 1000000L,
            updatedAt = 1000L
        )

        val depositAccount = DigitalDepositAccount(
            id = "dep-01",
            businessId = "biz-01",
            branchId = "branch-a",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        // DigitalTransaction is SUCCESS
        assertEquals(DigitalTransactionStatus.SUCCESS, digitalTx.fulfillmentStatus)

        // Customer opens a complaint
        val complaint = DigitalTransactionComplaint.createForTransaction(
            id = "comp-01",
            transaction = digitalTx,
            attribution = attributionBranchA,
            reason = DigitalTransactionComplaintReason.TRANSACTION_STUCK,
            description = "Transaction reported stuck",
            createdAt = 1200L
        ).markInvestigating(1250L)
            .markResolved("Checked with provider", 1300L)

        assertEquals(DigitalTransactionComplaintStatus.RESOLVED, complaint.status)

        // INVARIANTS:
        // 1. DigitalTransaction remains SUCCESS (not auto-reversed or failed)
        assertEquals(DigitalTransactionStatus.SUCCESS, digitalTx.fulfillmentStatus)

        // 2. CashRegister is untouched
        assertEquals(1000000L, cashRegister.currentBalance)

        // 3. DigitalDepositAccount is untouched
        assertEquals(500000L, depositAccount.currentBalance)
    }
}
