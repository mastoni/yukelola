package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.CashierSessionEntity
import id.yukelola.core.domain.model.actor.CashierSession
import id.yukelola.core.domain.model.actor.SessionStatus

/**
 * Lossless mapper between [CashierSession] domain aggregate and [CashierSessionEntity].
 * Storage only: contains no authentication logic, authorization, or cash ledger mutations.
 */
object CashierSessionMapper {

    fun toEntity(domain: CashierSession): CashierSessionEntity {
        return CashierSessionEntity(
            id = domain.id,
            branchId = domain.branchId,
            userId = domain.userId,
            deviceId = domain.deviceId,
            openingBalance = domain.openingBalance,
            closingBalance = domain.closingBalance,
            openedAt = domain.openedAt,
            closedAt = domain.closedAt,
            status = domain.status.name
        )
    }

    fun toDomain(entity: CashierSessionEntity): CashierSession {
        return CashierSession(
            id = entity.id,
            branchId = entity.branchId,
            userId = entity.userId,
            deviceId = entity.deviceId,
            openingBalance = entity.openingBalance,
            closingBalance = entity.closingBalance,
            openedAt = entity.openedAt,
            closedAt = entity.closedAt,
            status = SessionStatus.valueOf(entity.status)
        )
    }
}
