package id.yukelola.core.domain.model.attribution

import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionAttributionTest {

    @Test
    fun `TransactionAttribution captures full operational audit context`() {
        val attribution = TransactionAttribution(
            businessId = "biz-corp-01",
            branchId = "branch-01",
            userId = "user-kasir-01",
            deviceId = "device-tablet-01",
            cashierSessionId = "session-shift-01",
            createdAt = 1711234567000L
        )

        assertEquals("biz-corp-01", attribution.businessId)
        assertEquals("branch-01", attribution.branchId)
        assertEquals("user-kasir-01", attribution.userId)
        assertEquals("device-tablet-01", attribution.deviceId)
        assertEquals("session-shift-01", attribution.cashierSessionId)
        assertEquals(1711234567000L, attribution.createdAt)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `TransactionAttribution with blank branchId throws exception`() {
        TransactionAttribution(
            businessId = "biz-corp-01",
            branchId = "   ",
            userId = "user-kasir-01",
            deviceId = "device-tablet-01",
            createdAt = 1711234567000L
        )
    }
}
