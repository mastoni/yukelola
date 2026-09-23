package id.yukelola.core.domain.model.digital

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.catalog.Product
import id.yukelola.core.domain.model.catalog.ProductType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class DigitalTransactionSemanticsTest {

    private val attribution = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-01",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    @Test
    fun `digital transaction separates customer selling price and deposit cost price with deterministic gross profit`() {
        val tx = DigitalTransaction(
            id = "dt-01",
            attribution = attribution,
            targetNumber = "085712345678",
            productCode = "INDOSAT-100K",
            denomination = 100000L,
            costPrice = 98500L,
            sellingPrice = 102000L,
            createdAt = 1000L
        )

        // Customer pays selling price: 102.000
        assertEquals(102000L, tx.sellingPrice)

        // Digital deposit is debited by distributor cost price: 98.500
        assertEquals(98500L, tx.costPrice)

        // Gross profit realized is the spread: 102.000 - 98.500 = 3.500
        assertEquals(3500L, tx.grossProfit)
    }

    @Test
    fun `digital transaction enforces pure digital semantics without physical inventory`() {
        val digitalProduct = Product(
            id = "prod-pln-50k",
            businessId = "biz-01",
            categoryId = "cat-digital",
            name = "Token Listrik PLN 50.000",
            sku = "PLN50",
            productType = ProductType.DIGITAL,
            baseUnit = "VOUCHER",
            defaultCostPrice = 49500L,
            defaultSellingPrice = 52000L
        )

        assertEquals(ProductType.DIGITAL, digitalProduct.productType)

        val tx = DigitalTransaction(
            id = "dt-pln-01",
            attribution = attribution,
            targetNumber = "14123456789",
            productCode = digitalProduct.sku ?: "PLN50",
            denomination = 50000L,
            costPrice = digitalProduct.defaultCostPrice,
            sellingPrice = digitalProduct.defaultSellingPrice,
            createdAt = 1000L
        )

        assertEquals("PLN50", tx.productCode)
        assertEquals(50000L, tx.denomination)
        assertEquals(2500L, tx.grossProfit)
    }

    @Test
    fun `digital transaction rejects invalid monetary amounts and blank identifiers`() {
        assertThrows(IllegalArgumentException::class.java) {
            DigitalTransaction(
                id = "",
                attribution = attribution,
                targetNumber = "081234567890",
                productCode = "TELKOMSEL-50K",
                denomination = 50000L,
                costPrice = 49000L,
                sellingPrice = 52000L,
                createdAt = 1000L
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            DigitalTransaction(
                id = "dt-01",
                attribution = attribution,
                targetNumber = "081234567890",
                productCode = "TELKOMSEL-50K",
                denomination = 0L,
                costPrice = 49000L,
                sellingPrice = 52000L,
                createdAt = 1000L
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            DigitalTransaction(
                id = "dt-01",
                attribution = attribution,
                targetNumber = "081234567890",
                productCode = "TELKOMSEL-50K",
                denomination = 50000L,
                costPrice = -100L,
                sellingPrice = 52000L,
                createdAt = 1000L
            )
        }
    }

    @Test
    fun `historical snapshot remains invariant if distributor pricing changes later`() {
        var distributorCost = 49000L

        val tx = DigitalTransaction(
            id = "dt-01",
            attribution = attribution,
            targetNumber = "081234567890",
            productCode = "TSEL-50K",
            denomination = 50000L,
            costPrice = distributorCost,
            sellingPrice = 52000L,
            createdAt = 1000L
        ).markPending(1050L)
            .markSuccess("SN-12345", "dep-01", 1100L)

        // Next day, provider raises price
        distributorCost = 50500L

        // Historical completed transaction retains original cost price of 49.000 and profit 3.000
        assertEquals(49000L, tx.costPrice)
        assertEquals(3000L, tx.grossProfit)
    }
}
