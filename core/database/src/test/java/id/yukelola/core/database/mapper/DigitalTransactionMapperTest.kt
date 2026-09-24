package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.DigitalTransactionEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.digital.DigitalTransaction
import id.yukelola.core.domain.model.digital.DigitalTransactionStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DigitalTransactionMapperTest {

    @Test
    fun `domain to entity maps all fields losslessly with active initiated transaction`() {
        val attribution = TransactionAttribution(
            businessId = "biz-01",
            branchId = "branch-01",
            userId = "user-01",
            deviceId = "dev-01",
            cashierSessionId = "sess-01",
            createdAt = 1700000000000L
        )

        val domain = DigitalTransaction(
            id = "dt-01",
            attribution = attribution,
            targetNumber = "081234567890",
            productCode = "TELKOMSEL_50K",
            denomination = 50000L,
            costPrice = 48500L,
            sellingPrice = 52000L,
            saleId = "sale-01",
            depositMutationId = "mut-01",
            fulfillmentStatus = DigitalTransactionStatus.INITIATED,
            providerReference = null,
            failureReason = null,
            createdAt = 1700000000000L,
            updatedAt = 1700000000000L
        )

        val entity = domain.toEntity()

        assertEquals("dt-01", entity.id)
        assertEquals("biz-01", entity.businessId)
        assertEquals("branch-01", entity.branchId)
        assertEquals("user-01", entity.userId)
        assertEquals("dev-01", entity.deviceId)
        assertEquals("sess-01", entity.cashierSessionId)
        assertEquals("081234567890", entity.targetNumber)
        assertEquals("TELKOMSEL_50K", entity.productCode)
        assertEquals(50000L, entity.denomination)
        assertEquals(48500L, entity.costPrice)
        assertEquals(52000L, entity.sellingPrice)
        assertEquals("sale-01", entity.saleId)
        assertEquals("mut-01", entity.depositMutationId)
        assertEquals("INITIATED", entity.fulfillmentStatus)
        assertNull(entity.providerReference)
        assertNull(entity.failureReason)
        assertEquals(1700000000000L, entity.createdAt)
        assertEquals(1700000000000L, entity.updatedAt)
    }

    @Test
    fun `entity to domain maps all fields losslessly with nullable fields null`() {
        val entity = DigitalTransactionEntity(
            id = "dt-02",
            businessId = "biz-02",
            branchId = "branch-02",
            userId = "user-02",
            deviceId = "dev-02",
            cashierSessionId = null,
            targetNumber = "PLN12345678",
            productCode = "PLN_TOKEN_100K",
            denomination = 100000L,
            costPrice = 100000L,
            sellingPrice = 102500L,
            saleId = null,
            depositMutationId = null,
            fulfillmentStatus = "PENDING",
            providerReference = null,
            failureReason = null,
            createdAt = 1700000000000L,
            updatedAt = 1700000005000L
        )

        val domain = entity.toDomain()

        assertEquals("dt-02", domain.id)
        assertEquals("biz-02", domain.businessId)
        assertEquals("branch-02", domain.branchId)
        assertEquals("user-02", domain.attribution.userId)
        assertEquals("dev-02", domain.attribution.deviceId)
        assertNull(domain.attribution.cashierSessionId)
        assertEquals("PLN12345678", domain.targetNumber)
        assertEquals("PLN_TOKEN_100K", domain.productCode)
        assertEquals(100000L, domain.denomination)
        assertEquals(100000L, domain.costPrice)
        assertEquals(102500L, domain.sellingPrice)
        assertNull(domain.saleId)
        assertNull(domain.depositMutationId)
        assertEquals(DigitalTransactionStatus.PENDING, domain.fulfillmentStatus)
        assertNull(domain.providerReference)
        assertNull(domain.failureReason)
        assertEquals(1700000000000L, domain.createdAt)
        assertEquals(1700000005000L, domain.updatedAt)
    }

    @Test
    fun `full round trip preserves exact domain identity and precision across lifecycle statuses`() {
        val attribution = TransactionAttribution(
            businessId = "biz-03",
            branchId = "branch-03",
            userId = "user-03",
            deviceId = "dev-03",
            cashierSessionId = "sess-03",
            createdAt = 1700000000000L
        )

        val statuses = listOf(
            DigitalTransactionStatus.INITIATED to Pair(null, null),
            DigitalTransactionStatus.PENDING to Pair(null, null),
            DigitalTransactionStatus.SUCCESS to Pair("PRV-SN-998877", null),
            DigitalTransactionStatus.FAILED to Pair(null, "INSUFFICIENT_PROVIDER_BALANCE"),
            DigitalTransactionStatus.REVERSED to Pair("PRV-SN-998877", "CUSTOMER_DISPUTE_REFUND")
        )

        statuses.forEachIndexed { index, (status, refAndReason) ->
            val (ref, reason) = refAndReason
            val original = DigitalTransaction(
                id = "dt-round-$index",
                attribution = attribution,
                targetNumber = "0855123456$index",
                productCode = "ISAT_25K",
                denomination = 25000L,
                costPrice = 24000L,
                sellingPrice = 26000L,
                saleId = "sale-round-$index",
                depositMutationId = "mut-round-$index",
                fulfillmentStatus = status,
                providerReference = ref,
                failureReason = reason,
                createdAt = 1700000000000L,
                updatedAt = 1700000010000L + index
            )

            val entity = original.toEntity()
            val restored = entity.toDomain()

            assertEquals(original, restored)
            assertEquals(2000L, restored.grossProfit)
            assertEquals(status, restored.fulfillmentStatus)
        }
    }
}
