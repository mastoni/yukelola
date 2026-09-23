package id.yukelola.core.designsystem

import java.text.NumberFormat
import java.util.Locale

/**
 * Standard Indonesian Rupiah Currency Formatter for Yukelola UI.
 * Formats numbers into clear, accessible formats like "Rp 150.000".
 */
object YukelolaCurrencyFormatter {
    private val indonesianLocale = Locale("id", "ID")

    fun format(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(indonesianLocale)
        return "Rp ${formatter.format(amount)}"
    }

    fun format(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(indonesianLocale)
        formatter.maximumFractionDigits = 0
        return "Rp ${formatter.format(amount)}"
    }

    fun formatCompact(amount: Long): String {
        return when {
            amount >= 1_000_000_000 -> String.format(indonesianLocale, "Rp %.1fM", amount / 1_000_000_000.0)
            amount >= 1_000_000 -> String.format(indonesianLocale, "Rp %.1fJt", amount / 1_000_000.0)
            amount >= 1_000 -> String.format(indonesianLocale, "Rp %.0fRb", amount / 1_000.0)
            else -> format(amount)
        }
    }
}
