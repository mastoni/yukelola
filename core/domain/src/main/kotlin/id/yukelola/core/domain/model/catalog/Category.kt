package id.yukelola.core.domain.model.catalog

/**
 * Organizational taxonomy grouping for products within a Business.
 */
data class Category(
    val id: String,
    val businessId: String,
    val name: String,
    val color: String? = null,
    val icon: String? = null,
    val sortOrder: Int = 0
) {
    init {
        require(id.isNotBlank()) { "Category id must not be blank" }
        require(businessId.isNotBlank()) { "Category businessId must not be blank" }
        require(name.isNotBlank()) { "Category name must not be blank" }
    }
}
