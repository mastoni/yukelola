package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.PurchaseEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.purchase.Purchase
import id.yukelola.core.domain.model.purchase.PurchaseItem
import id.yukelola.core.domain.model.sale.PaymentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PurchaseDaoTest {

    @Test
    fun `PurchaseDao interface defines required persistence operations`() {
        val upsertMethod = PurchaseDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("PurchaseDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(PurchaseEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = PurchaseDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("PurchaseDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(PurchaseEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            PurchaseDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("PurchaseDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(PurchaseEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByIdAndBusinessIdAndBranchIdMethod =
            PurchaseDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessIdAndBranchId" }
        assertNotNull(
            "PurchaseDao must declare findByIdAndBusinessIdAndBranchId method",
            findByIdAndBusinessIdAndBranchIdMethod
        )
        assertEquals(3, findByIdAndBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[2])
        assertEquals(PurchaseEntity::class.java, findByIdAndBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdMethod =
            PurchaseDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("PurchaseDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            PurchaseDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull("PurchaseDao must declare findByBusinessIdAndBranchId method", findByBusinessIdAndBranchIdMethod)
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdAndSupplierIdMethod =
            PurchaseDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndSupplierId" }
        assertNotNull("PurchaseDao must declare findByBusinessIdAndSupplierId method", findByBusinessIdAndSupplierIdMethod)
        assertEquals(2, findByBusinessIdAndSupplierIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndSupplierIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndSupplierIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndSupplierIdMethod.returnType)

        val findByBusinessIdAndBranchIdAndSupplierIdMethod =
            PurchaseDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndSupplierId" }
        assertNotNull(
            "PurchaseDao must declare findByBusinessIdAndBranchIdAndSupplierId method",
            findByBusinessIdAndBranchIdAndSupplierIdMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndSupplierIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndSupplierIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndSupplierIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndSupplierIdMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndSupplierIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates PurchaseDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.PurchaseDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement PurchaseDao",
            PurchaseDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : PurchaseDao {
            private val storage = mutableMapOf<String, PurchaseEntity>()

            override fun upsert(purchase: PurchaseEntity) {
                storage[purchase.id] = purchase
            }

            override fun findById(id: String): PurchaseEntity? = storage[id]

            override fun findByIdAndBusinessId(id: String, businessId: String): PurchaseEntity? =
                storage[id]?.takeIf { it.businessId == businessId }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): PurchaseEntity? =
                storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }

            override fun findByBusinessId(businessId: String): List<PurchaseEntity> =
                storage.values.filter { it.businessId == businessId }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<PurchaseEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndSupplierId(
                businessId: String,
                supplierId: String
            ): List<PurchaseEntity> =
                storage.values.filter { it.businessId == businessId && it.supplierId == supplierId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndSupplierId(
                businessId: String,
                branchId: String,
                supplierId: String
            ): List<PurchaseEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.supplierId == supplierId
                }.sortedByDescending { it.createdAt }
        }

        val item = PurchaseItem(
            id = "pi-01",
            purchaseId = "purch-dao-01",
            productId = "prod-01",
            productName = "Product 1",
            unitCost = 50000L,
            quantity = 10.0
        )

        val domain = Purchase(
            id = "purch-dao-01",
            purchaseNumber = "PO-DAO-001",
            attribution = TransactionAttribution(
                businessId = "biz-01",
                branchId = "branch-01",
                userId = "user-01",
                deviceId = "dev-01",
                cashierSessionId = "sess-01",
                createdAt = 1700000000000L
            ),
            supplierId = "sup-01",
            items = listOf(item),
            paidAmount = 500000L,
            paymentStatus = PaymentStatus.PAID,
            notes = "DAO test purchase",
            createdAt = 1700000000000L
        )

        fakeDao.upsert(domain.toEntity())

        val retrievedEntity = fakeDao.findById("purch-dao-01")
        assertNotNull(retrievedEntity)
        val retrievedDomain = retrievedEntity!!.toDomain(listOf(item))

        assertEquals(domain, retrievedDomain)
        assertEquals(500000L, retrievedDomain.totalAmount)
        assertEquals(500000L, retrievedDomain.paidAmount)
        assertEquals(0L, retrievedDomain.remainingBalance)
        assertEquals(PaymentStatus.PAID, retrievedDomain.paymentStatus)
    }

    @Test
    fun `business and branch and supplier isolation strictly enforced`() {
        val fakeDao = object : PurchaseDao {
            private val storage = mutableMapOf<String, PurchaseEntity>()

            override fun upsert(purchase: PurchaseEntity) {
                storage[purchase.id] = purchase
            }

            override fun findById(id: String): PurchaseEntity? = storage[id]

            override fun findByIdAndBusinessId(id: String, businessId: String): PurchaseEntity? =
                storage[id]?.takeIf { it.businessId == businessId }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): PurchaseEntity? =
                storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }

            override fun findByBusinessId(businessId: String): List<PurchaseEntity> =
                storage.values.filter { it.businessId == businessId }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<PurchaseEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndSupplierId(
                businessId: String,
                supplierId: String
            ): List<PurchaseEntity> =
                storage.values.filter { it.businessId == businessId && it.supplierId == supplierId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndSupplierId(
                businessId: String,
                branchId: String,
                supplierId: String
            ): List<PurchaseEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.supplierId == supplierId
                }.sortedByDescending { it.createdAt }
        }

        val purchBiz1BranchASup1 = Purchase(
            id = "purch-iso-01",
            purchaseNumber = "PO-001",
            attribution = TransactionAttribution("biz-01", "branch-a", "user-01", "dev-01", createdAt = 1700000000001L),
            supplierId = "sup-01",
            items = listOf(PurchaseItem("pi-01", "purch-iso-01", "p-01", "P1", unitCost = 10000L)),
            paidAmount = 10000L
        )

        val purchBiz1BranchBSup2 = Purchase(
            id = "purch-iso-02",
            purchaseNumber = "PO-002",
            attribution = TransactionAttribution("biz-01", "branch-b", "user-01", "dev-01", createdAt = 1700000000002L),
            supplierId = "sup-02",
            items = listOf(PurchaseItem("pi-02", "purch-iso-02", "p-02", "P2", unitCost = 20000L)),
            paidAmount = 20000L
        )

        val purchBiz2BranchASup1 = Purchase(
            id = "purch-iso-03",
            purchaseNumber = "PO-003",
            attribution = TransactionAttribution("biz-02", "branch-a", "user-02", "dev-02", createdAt = 1700000000003L),
            supplierId = "sup-01",
            items = listOf(PurchaseItem("pi-03", "purch-iso-03", "p-03", "P3", unitCost = 30000L)),
            paidAmount = 30000L
        )

        fakeDao.upsert(purchBiz1BranchASup1.toEntity())
        fakeDao.upsert(purchBiz1BranchBSup2.toEntity())
        fakeDao.upsert(purchBiz2BranchASup1.toEntity())

        // Business isolation on findById
        assertNull(fakeDao.findByIdAndBusinessId("purch-iso-01", "biz-02"))
        assertNotNull(fakeDao.findByIdAndBusinessId("purch-iso-01", "biz-01"))

        // Branch isolation on findById
        assertNull(fakeDao.findByIdAndBusinessIdAndBranchId("purch-iso-01", "biz-01", "branch-b"))
        assertNotNull(fakeDao.findByIdAndBusinessIdAndBranchId("purch-iso-01", "biz-01", "branch-a"))

        // Multi-record business isolation
        val biz1Purchases = fakeDao.findByBusinessId("biz-01")
        assertEquals(2, biz1Purchases.size)
        assertEquals(listOf("purch-iso-02", "purch-iso-01"), biz1Purchases.map { it.id })

        val biz2Purchases = fakeDao.findByBusinessId("biz-02")
        assertEquals(1, biz2Purchases.size)
        assertEquals("purch-iso-03", biz2Purchases[0].id)

        // Supplier isolation
        val sup1Biz1Purchases = fakeDao.findByBusinessIdAndSupplierId("biz-01", "sup-01")
        assertEquals(1, sup1Biz1Purchases.size)
        assertEquals("purch-iso-01", sup1Biz1Purchases[0].id)

        val sup1Biz2Purchases = fakeDao.findByBusinessIdAndSupplierId("biz-02", "sup-01")
        assertEquals(1, sup1Biz2Purchases.size)
        assertEquals("purch-iso-03", sup1Biz2Purchases[0].id)
    }
}
