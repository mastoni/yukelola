package id.yukelola.core.domain.model.actor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CashierSessionScopeTest {

    @Test
    fun `CashierSession correctly binds User, Device, and Branch in open state`() {
        val session = CashierSession(
            id = "sess-100",
            branchId = "br-01",
            userId = "user-kasir-1",
            deviceId = "dev-pos-1",
            openingBalance = 100000L,
            openedAt = 1711234000000L,
            status = SessionStatus.OPEN
        )

        assertTrue(session.isOpen)
        assertEquals("br-01", session.branchId)
        assertEquals("user-kasir-1", session.userId)
        assertEquals("dev-pos-1", session.deviceId)
        assertEquals(100000L, session.openingBalance)
    }

    @Test
    fun `CashierSession close transition correctly records closing balance and timestamp`() {
        val session = CashierSession(
            id = "sess-100",
            branchId = "br-01",
            userId = "user-kasir-1",
            deviceId = "dev-pos-1",
            openingBalance = 100000L,
            openedAt = 1711234000000L,
            status = SessionStatus.OPEN
        )

        val closedSession = session.close(
            closingBalance = 450000L,
            closedAt = 1711262800000L
        )

        assertFalse(closedSession.isOpen)
        assertEquals(SessionStatus.CLOSED, closedSession.status)
        assertEquals(450000L, closedSession.closingBalance)
        assertEquals(1711262800000L, closedSession.closedAt)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Closing already closed CashierSession throws exception`() {
        val closedSession = CashierSession(
            id = "sess-100",
            branchId = "br-01",
            userId = "user-kasir-1",
            deviceId = "dev-pos-1",
            openingBalance = 100000L,
            closingBalance = 450000L,
            openedAt = 1711234000000L,
            closedAt = 1711262800000L,
            status = SessionStatus.CLOSED
        )

        closedSession.close(closingBalance = 500000L, closedAt = 1711263000000L)
    }
}
