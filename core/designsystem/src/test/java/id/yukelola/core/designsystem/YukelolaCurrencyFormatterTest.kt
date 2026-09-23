package id.yukelola.core.designsystem

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class YukelolaCurrencyFormatterTest {

    @Test
    fun testStandardFormat() {
        val formatted = YukelolaCurrencyFormatter.format(150000L)
        assertTrue(formatted.startsWith("Rp"))
        assertTrue(formatted.contains("150"))
    }

    @Test
    fun testZeroFormat() {
        val formatted = YukelolaCurrencyFormatter.format(0L)
        assertTrue(formatted.startsWith("Rp"))
        assertTrue(formatted.contains("0"))
    }

    @Test
    fun testCompactFormat() {
        val thousand = YukelolaCurrencyFormatter.formatCompact(50000L)
        assertTrue(thousand.contains("50") || thousand.contains("Rb"))

        val million = YukelolaCurrencyFormatter.formatCompact(1500000L)
        assertTrue(million.contains("1") || million.contains("Jt"))
    }
}
