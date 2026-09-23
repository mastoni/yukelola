package id.yukelola.core.domain.model.catalog

/**
 * Measurement unit definition for physical goods (e.g., PCS, KG, STRIP, BOX, LITER).
 */
data class ProductUnit(
    val id: String,
    val businessId: String,
    val name: String,
    val symbol: String,
    val conversionFactor: Double = 1.0
) {
    init {
        require(id.isNotBlank()) { "ProductUnit id must not be blank" }
        require(businessId.isNotBlank()) { "ProductUnit businessId must not be blank" }
        require(name.isNotBlank()) { "ProductUnit name must not be blank" }
        require(symbol.isNotBlank()) { "ProductUnit symbol must not be blank" }
        require(conversionFactor > 0.0) { "conversionFactor must be positive" }
    }
}
