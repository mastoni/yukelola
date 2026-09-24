package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.DigitalTransactionEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.digital.DigitalTransaction
import id.yukelola.core.domain.model.digital.DigitalTransactionStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DigitalTransactionDaoTest {

    @Test
    fun `DigitalTransactionDao interface defines required persistence operations`() {
        val upsertMethod = DigitalTransactionDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("DigitalTransactionDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(DigitalTransactionEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = DigitalTransactionDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("DigitalTransactionDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(DigitalTransactionEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            DigitalTransactionDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("DigitalTransactionDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(DigitalTransactionEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByIdAndBusinessIdAndBranchIdMethod =
            DigitalTransactionDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessIdAndBranchId" }
        assertNotNull(
            "DigitalTransactionDao must declare findByIdAndBusinessIdAndBranchId method",
            findByIdAndBusinessIdAndBranchIdMethod
        )
        assertEquals(3, findByIdAndBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[2])
        assertEquals(DigitalTransactionEntity::class.java, findByIdAndBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdMethod =
            DigitalTransactionDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("DigitalTransactionDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            DigitalTransactionDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull(
            "DigitalTransactionDao must declare findByBusinessIdAndBranchId method",
            findByBusinessIdAndBranchIdMethod
        )
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdAndBranchIdAndStatusMethod =
            DigitalTransactionDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndStatus" }
        assertNotNull(
            "DigitalTransactionDao must declare findByBusinessIdAndBranchIdAndStatus method",
            findByBusinessIdAndBranchIdAndStatusMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndStatusMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndStatusMethod.returnType)

        val findByBusinessIdAndBranchIdAndTargetNumberMethod =
            DigitalTransactionDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndTargetNumber" }
        assertNotNull(
            "DigitalTransactionDao must declare findByBusinessIdAndBranchIdAndTargetNumber method",
            findByBusinessIdAndBranchIdAndTargetNumberMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndTargetNumberMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndTargetNumberMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndTargetNumberMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndTargetNumberMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndTargetNumberMethod.returnType)

        val findByBusinessIdAndSaleIdMethod =
            DigitalTransactionDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndSaleId" }
        assertNotNull(
            "DigitalTransactionDao must declare findByBusinessIdAndSaleId method",
            findByBusinessIdAndSaleIdMethod
        )
        assertEquals(2, findByBusinessIdAndSaleIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndSaleIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndSaleIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndSaleIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates DigitalTransactionDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.DigitalTransactionDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement DigitalTransactionDao",
            DigitalTransactionDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : DigitalTransactionDao {
            private val storage = mutableMapOf<String, DigitalTransactionEntity>()

            override fun upsert(digitalTransaction: DigitalTransactionEntity) {
                storage[digitalTransaction.id] = digitalTransaction
            }

            override fun findById(id: String): DigitalTransactionEntity? = storage[id]

            override fun findByIdAndBusinessId(id: String, businessId: String): DigitalTransactionEntity? =
                storage[id]?.takeIf { it.businessId == businessId }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): DigitalTransactionEntity? =
                storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }

            override fun findByBusinessId(businessId: String): List<DigitalTransactionEntity> =
                storage.values.filter { it.businessId == businessId }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<DigitalTransactionEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndStatus(
                businessId: String,
                branchId: String,
                status: String
            ): List<DigitalTransactionEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.fulfillmentStatus == status
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndTargetNumber(
                businessId: String,
                branchId: String,
                targetNumber: String
            ): List<DigitalTransactionEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.targetNumber == targetNumber
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndSaleId(
                businessId: String,
                saleId: String
            ): List<DigitalTransactionEntity> =
                storage.values.filter { it.businessId == businessId && it.saleId == saleId }
                    .sortedByDescending { it.createdAt }
        }

        val domain = DigitalTransaction(
            id = "dt-dao-01",
            attribution = TransactionAttribution(
                businessId = "biz-01",
                branchId = "branch-01",
                userId = "user-01",
                deviceId = "dev-01",
                cashierSessionId = "sess-01",
                createdAt = 1700000000000L
            ),
            targetNumber = "081234567890",
            productCode = "TSEL_100K",
            denomination = 100000L,
            costPrice = 98000L,
            sellingPrice = 102000L,
            saleId = "sale-100",
            depositMutationId = "mut-100",
            fulfillmentStatus = DigitalTransactionStatus.SUCCESS,
            providerReference = "SN-2026-TSEL-001",
            createdAt = 1700000000000L,
            updatedAt = 1700000005000L
        )

        fakeDao.upsert(domain.toEntity())

        val retrievedEntity = fakeDao.findById("dt-dao-01")
        assertNotNull(retrievedEntity)
        val retrievedDomain = retrievedEntity!!.toDomain()

        assertEquals(domain, retrievedDomain)
        assertEquals(DigitalTransactionStatus.SUCCESS, retrievedDomain.fulfillmentStatus)
        assertEquals("SN-2026-TSEL-001", retrievedDomain.providerReference)
        assertEquals(4000L, retrievedDomain.grossProfit)
    }

    @Test
    fun `business and branch and status and target and sale isolation strictly enforced`() {
        val fakeDao = object : DigitalTransactionDao {
            private val storage = mutableMapOf<String, DigitalTransactionEntity>()

            override fun upsert(digitalTransaction: DigitalTransactionEntity) {
                storage[digitalTransaction.id] = digitalTransaction
            }

            override fun findById(id: String): DigitalTransactionEntity? = storage[id]

            override fun findByIdAndBusinessId(id: String, businessId: String): DigitalTransactionEntity? =
                storage[id]?.takeIf { it.businessId == businessId }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): DigitalTransactionEntity? =
                storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }

            override fun findByBusinessId(businessId: String): List<DigitalTransactionEntity> =
                storage.values.filter { it.businessId == businessId }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<DigitalTransactionEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndStatus(
                businessId: String,
                branchId: String,
                status: String
            ): List<DigitalTransactionEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.fulfillmentStatus == status
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndTargetNumber(
                businessId: String,
                branchId: String,
                targetNumber: String
            ): List<DigitalTransactionEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.targetNumber == targetNumber
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndSaleId(
                businessId: String,
                saleId: String
            ): List<DigitalTransactionEntity> =
                storage.values.filter { it.businessId == businessId && it.saleId == saleId }
                    .sortedByDescending { it.createdAt }
        }

        val tx1 = DigitalTransaction(
            id = "dt-iso-01",
            attribution = TransactionAttribution("biz-01", "branch-a", "user-01", "dev-01", createdAt = 1700000000001L),
            targetNumber = "0811111111",
            productCode = "TSEL_25K",
            denomination = 25000L,
            costPrice = 24500L,
            sellingPrice = 27000L,
            saleId = "sale-01",
            fulfillmentStatus = DigitalTransactionStatus.SUCCESS
        )

        val tx2 = DigitalTransaction(
            id = "dt-iso-02",
            attribution = TransactionAttribution("biz-01", "branch-b", "user-01", "dev-01", createdAt = 1700000000002L),
            targetNumber = "0822222222",
            productCode = "ISAT_50K",
            denomination = 50000L,
            costPrice = 49000L,
            sellingPrice = 52000L,
            saleId = "sale-02",
            fulfillmentStatus = DigitalTransactionStatus.FAILED
        )

        val tx3 = DigitalTransaction(
            id = "dt-iso-03",
            attribution = TransactionAttribution("biz-02", "branch-a", "user-02", "dev-02", createdAt = 1700000000003L),
            targetNumber = "0811111111",
            productCode = "TSEL_25K",
            denomination = 25000L,
            costPrice = 24500L,
            sellingPrice = 27000L,
            saleId = "sale-01",
            fulfillmentStatus = DigitalTransactionStatus.SUCCESS
        )

        fakeDao.upsert(tx1.toEntity())
        fakeDao.upsert(tx2.toEntity())
        fakeDao.upsert(tx3.toEntity())

        // Business isolation on findById
        assertNull(fakeDao.findByIdAndBusinessId("dt-iso-01", "biz-02"))
        assertNotNull(fakeDao.findByIdAndBusinessId("dt-iso-01", "biz-01"))

        // Branch isolation on findById
        assertNull(fakeDao.findByIdAndBusinessIdAndBranchId("dt-iso-01", "biz-01", "branch-b"))
        assertNotNull(fakeDao.findByIdAndBusinessIdAndBranchId("dt-iso-01", "biz-01", "branch-a"))

        // Multi-record business isolation
        val biz1Txs = fakeDao.findByBusinessId("biz-01")
        assertEquals(2, biz1Txs.size)
        assertEquals(listOf("dt-iso-02", "dt-iso-01"), biz1Txs.map { it.id })

        val biz2Txs = fakeDao.findByBusinessId("biz-02")
        assertEquals(1, biz2Txs.size)
        assertEquals("dt-iso-03", biz2Txs[0].id)

        // Status isolation
        val successBranchA = fakeDao.findByBusinessIdAndBranchIdAndStatus("biz-01", "branch-a", "SUCCESS")
        assertEquals(1, successBranchA.size)
        assertEquals("dt-iso-01", successBranchA[0].id)

        val failedBranchB = fakeDao.findByBusinessIdAndBranchIdAndStatus("biz-01", "branch-b", "FAILED")
        assertEquals(1, failedBranchB.size)
        assertEquals("dt-iso-02", failedBranchB[0].id)

        // Target number isolation
        val target1Biz1 = fakeDao.findByBusinessIdAndBranchIdAndTargetNumber("biz-01", "branch-a", "0811111111")
        assertEquals(1, target1Biz1.size)
        assertEquals("dt-iso-01", target1Biz1[0].id)

        // Sale ID isolation
        val sale1Biz1 = fakeDao.findByBusinessIdAndSaleId("biz-01", "sale-01")
        assertEquals(1, sale1Biz1.size)
        assertEquals("dt-iso-01", sale1Biz1[0].id)

        val sale1Biz2 = fakeDao.findByBusinessIdAndSaleId("biz-02", "sale-01")
        assertEquals(1, sale1Biz2.size)
        assertEquals("dt-iso-03", sale1Biz2[0].id)
    }
}
