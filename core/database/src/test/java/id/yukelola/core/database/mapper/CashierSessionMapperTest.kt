package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.CashierSessionEntity
import id.yukelola.core.domain.model.actor.CashierSession
import id.yukelola.core.domain.model.actor.SessionStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CashierSessionMapperTest {

    @Test
    fun `toEntity maps open CashierSession domain fields losslessly`() {
        val domain = CashierSession(
            id = "sess-001",
            branchId = "branch-001",
            userId = "user-kasir-1",
            deviceId = "dev-pos-1",
            openingBalance = 100000L,
            closingBalance = null,
            openedAt = 1711234000000L,
            closedAt = null,
            status = SessionStatus.OPEN
        )

        val entity = CashierSessionMapper.toEntity(domain)

        assertEquals("sess-001", entity.id)
        assertEquals("branch-001", entity.branchId)
        assertEquals("user-kasir-1", entity.userId)
        assertEquals("dev-pos-1", entity.deviceId)
        assertEquals(100000L, entity.openingBalance)
        assertNull(entity.closingBalance)
        assertEquals(1711234000000L, entity.openedAt)
        assertNull(entity.closedAt)
        assertEquals("OPEN", entity.status)
    }

    @Test
    fun `toEntity maps closed CashierSession domain fields losslessly`() {
        val domain = CashierSession(
            id = "sess-002",
            branchId = "branch-001",
            userId = "user-kasir-2",
            deviceId = "dev-pos-2",
            openingBalance = 200000L,
            closingBalance = 750000L,
            openedAt = 1711234000000L,
            closedAt = 1711262800000L,
            status = SessionStatus.CLOSED
        )

        val entity = CashierSessionMapper.toEntity(domain)

        assertEquals("sess-002", entity.id)
        assertEquals("branch-001", entity.branchId)
        assertEquals("user-kasir-2", entity.userId)
        assertEquals("dev-pos-2", entity.deviceId)
        assertEquals(200000L, entity.openingBalance)
        assertEquals(750000L, entity.closingBalance)
        assertEquals(1711234000000L, entity.openedAt)
        assertEquals(1711262800000L, entity.closedAt)
        assertEquals("CLOSED", entity.status)
    }

    @Test
    fun `toDomain maps open CashierSessionEntity fields losslessly`() {
        val entity = CashierSessionEntity(
            id = "sess-003",
            branchId = "branch-002",
            userId = "user-kasir-3",
            deviceId = "dev-pos-3",
            openingBalance = 50000L,
            closingBalance = null,
            openedAt = 1711235000000L,
            closedAt = null,
            status = "OPEN"
        )

        val domain = CashierSessionMapper.toDomain(entity)

        assertEquals("sess-003", domain.id)
        assertEquals("branch-002", domain.branchId)
        assertEquals("user-kasir-3", domain.userId)
        assertEquals("dev-pos-3", domain.deviceId)
        assertEquals(50000L, domain.openingBalance)
        assertNull(domain.closingBalance)
        assertEquals(1711235000000L, domain.openedAt)
        assertNull(domain.closedAt)
        assertEquals(SessionStatus.OPEN, domain.status)
    }

    @Test
    fun `toDomain maps closed CashierSessionEntity fields losslessly`() {
        val entity = CashierSessionEntity(
            id = "sess-004",
            branchId = "branch-002",
            userId = "user-kasir-4",
            deviceId = "dev-pos-4",
            openingBalance = 50000L,
            closingBalance = 320000L,
            openedAt = 1711235000000L,
            closedAt = 1711263800000L,
            status = "CLOSED"
        )

        val domain = CashierSessionMapper.toDomain(entity)

        assertEquals("sess-004", domain.id)
        assertEquals("branch-002", domain.branchId)
        assertEquals("user-kasir-4", domain.userId)
        assertEquals("dev-pos-4", domain.deviceId)
        assertEquals(50000L, domain.openingBalance)
        assertEquals(320000L, domain.closingBalance)
        assertEquals(1711235000000L, domain.openedAt)
        assertEquals(1711263800000L, domain.closedAt)
        assertEquals(SessionStatus.CLOSED, domain.status)
    }

    @Test
    fun `preserves all canonical SessionStatus enum values`() {
        for (status in SessionStatus.values()) {
            val domain = CashierSession(
                id = "sess-status-${status.name}",
                branchId = "branch-001",
                userId = "user-1",
                deviceId = "dev-1",
                openingBalance = 100000L,
                closingBalance = if (status == SessionStatus.CLOSED) 500000L else null,
                openedAt = 1711234000000L,
                closedAt = if (status == SessionStatus.CLOSED) 1711262800000L else null,
                status = status
            )

            val entity = CashierSessionMapper.toEntity(domain)
            assertEquals(status.name, entity.status)

            val roundTrip = CashierSessionMapper.toDomain(entity)
            assertEquals(status, roundTrip.status)
        }
    }
}
