package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.StockAdjustmentEntity
import id.yukelola.core.domain.model.stock.StockAdjustment
import id.yukelola.core.domain.model.stock.StockAdjustmentReason

/**
 * Lossless mapper between [StockAdjustment] domain aggregate and [StockAdjustmentEntity].
 * Storage only: contains no business logic, stock mutations, or inventory orchestration.
 */
object StockAdjustmentMapper {

    fun toEntity(domain: StockAdjustment): StockAdjustmentEntity {
        return StockAdjustmentEntity(
            id = domain.id,
            businessId = domain.businessId,
            branchId = domain.branchId,
            productId = domain.productId,
            previousStock = domain.previousStock,
            adjustedStock = domain.adjustedStock,
            reason = domain.reason.name,
            userId = domain.userId,
            deviceId = domain.deviceId,
            notes = domain.notes,
            createdAt = domain.createdAt
        )
    }

    fun toDomain(entity: StockAdjustmentEntity): StockAdjustment {
        return StockAdjustment(
            id = entity.id,
            businessId = entity.businessId,
            branchId = entity.branchId,
            productId = entity.productId,
            previousStock = entity.previousStock,
            adjustedStock = entity.adjustedStock,
            reason = StockAdjustmentReason.valueOf(entity.reason),
            userId = entity.userId,
            deviceId = entity.deviceId,
            notes = entity.notes,
            createdAt = entity.createdAt
        )
    }
}
