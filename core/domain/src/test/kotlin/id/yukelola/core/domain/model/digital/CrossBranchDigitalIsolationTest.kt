package id.yukelola.core.domain.model.digital

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CrossBranchDigitalIsolationTest {

    private val branchAAttribution = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-a",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    @Test
    fun `digital transaction is strictly isolated to attribution branch and business`() {
        val tx = DigitalTransaction(
            id = "dt-a-01",
            attribution = branchAAttribution,
            targetNumber = "081234567890",
            productCode = "TSEL50",
            denomination = 50000L,
            costPrice = 49000L,
            sellingPrice = 52000L,
            createdAt = 1000L
        )

        assertEquals("biz-01", tx.businessId)
        assertEquals("branch-a", tx.branchId)
    }

    @Test
    fun `digital deposit account rejects mutation from another branch or business`() {
        val accountBranchA = DigitalDepositAccount(
            id = "dep-branch-a",
            businessId = "biz-01",
            branchId = "branch-a",
            currentBalance = 1000000L,
            updatedAt = 1000L
        )

        val branchBMutation = DigitalDepositMutation(
            id = "mut-b-01",
            businessId = "biz-01",
            branchId = "branch-b",
            accountId = "dep-branch-a",
            mutationType = DigitalDepositMutationType.TOP_UP,
            amount = 100000L,
            balanceBefore = 1000000L,
            balanceAfter = 1100000L,
            createdAt = 1050L
        )

        assertThrows(IllegalArgumentException::class.java) {
            accountBranchA.applyMutation(branchBMutation)
        }

        val otherBizMutation = DigitalDepositMutation(
            id = "mut-other-01",
            businessId = "biz-other",
            branchId = "branch-a",
            accountId = "dep-branch-a",
            mutationType = DigitalDepositMutationType.TOP_UP,
            amount = 100000L,
            balanceBefore = 1000000L,
            balanceAfter = 1100000L,
            createdAt = 1050L
        )

        assertThrows(IllegalArgumentException::class.java) {
            accountBranchA.applyMutation(otherBizMutation)
        }
    }
}
