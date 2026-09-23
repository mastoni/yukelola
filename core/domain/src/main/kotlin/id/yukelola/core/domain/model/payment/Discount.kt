package id.yukelola.core.domain.model.payment

/**
 * Monetary or percentage reduction applied to an item or entire transaction.
 */
data class Discount(
    val type: DiscountType,
    val value: Long
) {
    init {
        require(value >= 0L) { "Discount value must not be negative" }
        if (type == DiscountType.PERCENTAGE) {
            require(value <= 100L) { "Percentage discount must not exceed 100%" }
        }
    }

    /**
     * Computes the absolute monetary deduction for a given gross amount.
     */
    fun calculateDeduction(grossAmount: Long): Long {
        return when (type) {
            DiscountType.FIXED_AMOUNT -> minOf(value, grossAmount)
            DiscountType.PERCENTAGE -> (grossAmount * value) / 100L
        }
    }
}
