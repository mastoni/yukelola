package id.yukelola.core.domain.model.stock

/**
 * Controlled reasons for manual physical stock corrections.
 * Strictly compliant with Contract v1.3.0.
 */
enum class StockAdjustmentReason {
    /**
     * Periodic inventory count reconciliation (opname fisik).
     */
    STOCK_OPNAME,

    /**
     * Physical damage / broken item write-off (barang rusak).
     */
    DAMAGED,

    /**
     * Expired inventory write-off (barang kedaluwarsa).
     */
    EXPIRED,

    /**
     * Missing or stolen inventory write-off (barang hilang).
     */
    LOST,

    /**
     * Item consumed for branch internal / store operational use (keperluan operasional/toko).
     */
    INTERNAL_USE
}
