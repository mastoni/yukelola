package id.yukelola.core.domain.model.digital

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class DigitalTransactionLifecycleTest {

    private val attribution = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-01",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    private val validTx = DigitalTransaction(
        id = "dt-01",
        attribution = attribution,
        targetNumber = "081234567890",
        productCode = "TELKOMSEL-50K",
        denomination = 50000L,
        costPrice = 49000L,
        sellingPrice = 52000L,
        createdAt = 1000L
    )

    @Test
    fun `digital transaction executes happy path INITIATED to PENDING to SUCCESS to REVERSED`() {
        assertEquals(DigitalTransactionStatus.INITIATED, validTx.fulfillmentStatus)
        assertFalse(validTx.isTerminal)

        // Step 1: INITIATED -> PENDING
        val pendingTx = validTx.markPending(1050L)
        assertEquals(DigitalTransactionStatus.PENDING, pendingTx.fulfillmentStatus)
        assertEquals(1050L, pendingTx.updatedAt)

        // Step 2: PENDING -> SUCCESS
        val successTx = pendingTx.markSuccess(
            providerReference = "SN-987654321",
            depositMutationId = "mut-dep-01",
            timestamp = 1100L
        )
        assertEquals(DigitalTransactionStatus.SUCCESS, successTx.fulfillmentStatus)
        assertEquals("SN-987654321", successTx.providerReference)
        assertEquals("mut-dep-01", successTx.depositMutationId)
        assertFalse(successTx.isTerminal)

        // Step 3: SUCCESS -> REVERSED (Traceable compensating reversal)
        val reversedTx = successTx.markReversed(
            reversalReason = "Provider failed downstream delivery",
            timestamp = 1200L
        )
        assertEquals(DigitalTransactionStatus.REVERSED, reversedTx.fulfillmentStatus)
        assertEquals("Provider failed downstream delivery", reversedTx.failureReason)
        assertTrue(reversedTx.isTerminal)
        assertEquals(1200L, reversedTx.updatedAt)
    }

    @Test
    fun `digital transaction executes failure path PENDING to FAILED`() {
        val pendingTx = validTx.markPending(1050L)

        val failedTx = pendingTx.markFailed(
            failureReason = "Target number blocked or invalid",
            timestamp = 1100L
        )

        assertEquals(DigitalTransactionStatus.FAILED, failedTx.fulfillmentStatus)
        assertEquals("Target number blocked or invalid", failedTx.failureReason)
        assertTrue(failedTx.isTerminal)
    }

    @Test
    fun `terminal state protects against any further status transition`() {
        val pendingTx = validTx.markPending(1050L)
        val failedTx = pendingTx.markFailed("Timeout", 1100L)

        // Attempting to transition from FAILED to SUCCESS
        assertThrows(IllegalArgumentException::class.java) {
            failedTx.markSuccess("SN-123", null, 1200L)
        }

        // Attempting to transition from FAILED to PENDING
        assertThrows(IllegalArgumentException::class.java) {
            failedTx.markPending(1200L)
        }

        val successTx = pendingTx.markSuccess("SN-123", null, 1100L)
        val reversedTx = successTx.markReversed("Error", 1200L)

        // Attempting to transition from REVERSED to PENDING
        assertThrows(IllegalArgumentException::class.java) {
            reversedTx.markPending(1300L)
        }
    }

    @Test
    fun `invalid direct transitions are strictly rejected`() {
        // Cannot jump directly from INITIATED to SUCCESS without PENDING
        assertThrows(IllegalArgumentException::class.java) {
            validTx.markSuccess("SN-123", null, 1100L)
        }

        // Cannot jump directly from INITIATED to FAILED without PENDING
        assertThrows(IllegalArgumentException::class.java) {
            validTx.markFailed("Error", 1100L)
        }

        // Cannot jump directly from INITIATED to REVERSED
        assertThrows(IllegalArgumentException::class.java) {
            validTx.markReversed("Error", 1100L)
        }
    }
}
