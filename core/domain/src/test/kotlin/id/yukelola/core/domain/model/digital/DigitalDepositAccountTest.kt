package id.yukelola.core.domain.model.digital

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class DigitalDepositAccountTest {

    @Test
    fun `digital deposit account applies top up and digital sale mutations deterministically`() {
        val account = DigitalDepositAccount(
            id = "dep-01",
            businessId = "biz-01",
            branchId = "branch-01",
            currentBalance = 1000000L,
            updatedAt = 1000L
        )

        // Top up 500.000
        val topUpMutation = DigitalDepositMutation(
            id = "mut-01",
            businessId = "biz-01",
            branchId = "branch-01",
            accountId = "dep-01",
            mutationType = DigitalDepositMutationType.TOP_UP,
            amount = 500000L,
            balanceBefore = 1000000L,
            balanceAfter = 1500000L,
            referenceId = "TOPUP-REF-123",
            createdAt = 1050L
        )

        val toppedUpAccount = account.applyMutation(topUpMutation)
        assertEquals(1500000L, toppedUpAccount.currentBalance)
        assertEquals(1050L, toppedUpAccount.updatedAt)

        // Digital sale cost debit: 49.000
        val saleMutation = DigitalDepositMutation(
            id = "mut-02",
            businessId = "biz-01",
            branchId = "branch-01",
            accountId = "dep-01",
            mutationType = DigitalDepositMutationType.DIGITAL_SALE,
            amount = 49000L,
            balanceBefore = 1500000L,
            balanceAfter = 1451000L,
            referenceId = "DT-001",
            createdAt = 1100L
        )

        val soldAccount = toppedUpAccount.applyMutation(saleMutation)
        assertEquals(1451000L, soldAccount.currentBalance)
        assertEquals(1100L, soldAccount.updatedAt)
    }

    @Test
    fun `reversal mutation compensates and restores deposit balance`() {
        val account = DigitalDepositAccount(
            id = "dep-01",
            businessId = "biz-01",
            branchId = "branch-01",
            currentBalance = 1451000L,
            updatedAt = 1100L
        )

        // Reversal of previously failed 49.000 dispatch
        val reversalMutation = DigitalDepositMutation(
            id = "mut-03",
            businessId = "biz-01",
            branchId = "branch-01",
            accountId = "dep-01",
            mutationType = DigitalDepositMutationType.REVERSAL,
            amount = 49000L,
            balanceBefore = 1451000L,
            balanceAfter = 1500000L,
            referenceId = "DT-001",
            notes = "Compensating reversal for failed downstream dispatch",
            createdAt = 1150L
        )

        val restoredAccount = account.applyMutation(reversalMutation)
        assertEquals(1500000L, restoredAccount.currentBalance)
        assertEquals(1150L, restoredAccount.updatedAt)
    }

    @Test
    fun `mutation with mathematical inconsistency is strictly rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            DigitalDepositMutation(
                id = "mut-bad",
                businessId = "biz-01",
                branchId = "branch-01",
                accountId = "dep-01",
                mutationType = DigitalDepositMutationType.TOP_UP,
                amount = 100000L,
                balanceBefore = 500000L,
                balanceAfter = 550000L, // Incorrect, should be 600000
                createdAt = 1000L
            )
        }
    }

    @Test
    fun `account rejects mutation when balanceBefore does not match current account balance`() {
        val account = DigitalDepositAccount(
            id = "dep-01",
            businessId = "biz-01",
            branchId = "branch-01",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        val staleMutation = DigitalDepositMutation(
            id = "mut-stale",
            businessId = "biz-01",
            branchId = "branch-01",
            accountId = "dep-01",
            mutationType = DigitalDepositMutationType.TOP_UP,
            amount = 100000L,
            balanceBefore = 400000L, // Stale!
            balanceAfter = 500000L,
            createdAt = 1050L
        )

        assertThrows(IllegalArgumentException::class.java) {
            account.applyMutation(staleMutation)
        }
    }
}
