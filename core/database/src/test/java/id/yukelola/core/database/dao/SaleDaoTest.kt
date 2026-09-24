package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.SaleEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.sale.PaymentStatus
import id.yukelola.core.domain.model.sale.Sale
import id.yukelola.core.domain.model.sale.SaleItem
import id.yukelola.core.domain.model.sale.SaleStatus
import id.yukelola.core.domain.model.sale.TransactionMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SaleDaoTest {

    @Test
    fun `SaleDao interface defines required persistence operations`() {
        val upsertMethod = SaleDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("SaleDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(SaleEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = SaleDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("SaleDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(SaleEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            SaleDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("SaleDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(SaleEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByIdAndBusinessIdAndBranchIdMethod =
            SaleDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessIdAndBranchId" }
        assertNotNull(
            "SaleDao must declare findByIdAndBusinessIdAndBranchId method",
            findByIdAndBusinessIdAndBranchIdMethod
        )
        assertEquals(3, findByIdAndBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[2])
        assertEquals(SaleEntity::class.java, findByIdAndBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdMethod =
            SaleDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("SaleDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            SaleDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull("SaleDao must declare findByBusinessIdAndBranchId method", findByBusinessIdAndBranchIdMethod)
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdAndCustomerIdMethod =
            SaleDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndCustomerId" }
        assertNotNull("SaleDao must declare findByBusinessIdAndCustomerId method", findByBusinessIdAndCustomerIdMethod)
        assertEquals(2, findByBusinessIdAndCustomerIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndCustomerIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndCustomerIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndCustomerIdMethod.returnType)

        val findByBusinessIdAndBranchIdAndCustomerIdMethod =
            SaleDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndCustomerId" }
        assertNotNull(
            "SaleDao must declare findByBusinessIdAndBranchIdAndCustomerId method",
            findByBusinessIdAndBranchIdAndCustomerIdMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndCustomerIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndCustomerIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndCustomerIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndCustomerIdMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndCustomerIdMethod.returnType)

        val findByBusinessIdAndBranchIdAndStatusMethod =
            SaleDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndStatus" }
        assertNotNull(
            "SaleDao must declare findByBusinessIdAndBranchIdAndStatus method",
            findByBusinessIdAndBranchIdAndStatusMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndStatusMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndStatusMethod.returnType)
    }

    @Test
    fun `Room KSP generates SaleDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.SaleDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement SaleDao",
            SaleDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : SaleDao {
            private val storage = mutableMapOf<String, SaleEntity>()

            override fun upsert(sale: SaleEntity) {
                storage[sale.id] = sale
            }

            override fun findById(id: String): SaleEntity? = storage[id]

            override fun findByIdAndBusinessId(id: String, businessId: String): SaleEntity? =
                storage[id]?.takeIf { it.businessId == businessId }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): SaleEntity? =
                storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }

            override fun findByBusinessId(businessId: String): List<SaleEntity> =
                storage.values.filter { it.businessId == businessId }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<SaleEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndCustomerId(
                businessId: String,
                customerId: String
            ): List<SaleEntity> =
                storage.values.filter { it.businessId == businessId && it.customerId == customerId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndCustomerId(
                businessId: String,
                branchId: String,
                customerId: String
            ): List<SaleEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.customerId == customerId
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndStatus(
                businessId: String,
                branchId: String,
                status: String
            ): List<SaleEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.status == status
                }.sortedByDescending { it.createdAt }
        }

        val item = SaleItem(
            id = "item-01",
            saleId = "sale-dao-01",
            productId = "prod-01",
            productName = "Product 1",
            unitPrice = 10000L,
            quantity = 2.0
        )

        val domain = Sale(
            id = "sale-dao-01",
            saleNumber = "TRX-DAO-001",
            attribution = TransactionAttribution(
                businessId = "biz-01",
                branchId = "branch-01",
                userId = "user-01",
                deviceId = "dev-01",
                cashierSessionId = "sess-01",
                createdAt = 1700000000000L
            ),
            items = listOf(item),
            paidAmount = 20000L,
            paymentStatus = PaymentStatus.PAID,
            status = SaleStatus.COMPLETED,
            completedAt = 1700000050000L
        )

        fakeDao.upsert(domain.toEntity())

        val retrievedEntity = fakeDao.findById("sale-dao-01")
        assertNotNull(retrievedEntity)
        val retrievedDomain = retrievedEntity!!.toDomain(listOf(item))

        assertEquals(domain, retrievedDomain)
        assertEquals(SaleStatus.COMPLETED, retrievedDomain.status)
        assertEquals(PaymentStatus.PAID, retrievedDomain.paymentStatus)
        assertEquals(20000L, retrievedDomain.paidAmount)
        assertEquals(20000L, retrievedDomain.totalAmount)
    }

    @Test
    fun `business and branch and customer isolation strictly enforced`() {
        val fakeDao = object : SaleDao {
            private val storage = mutableMapOf<String, SaleEntity>()

            override fun upsert(sale: SaleEntity) {
                storage[sale.id] = sale
            }

            override fun findById(id: String): SaleEntity? = storage[id]

            override fun findByIdAndBusinessId(id: String, businessId: String): SaleEntity? =
                storage[id]?.takeIf { it.businessId == businessId }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): SaleEntity? =
                storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }

            override fun findByBusinessId(businessId: String): List<SaleEntity> =
                storage.values.filter { it.businessId == businessId }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<SaleEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndCustomerId(
                businessId: String,
                customerId: String
            ): List<SaleEntity> =
                storage.values.filter { it.businessId == businessId && it.customerId == customerId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndCustomerId(
                businessId: String,
                branchId: String,
                customerId: String
            ): List<SaleEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.customerId == customerId
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndStatus(
                businessId: String,
                branchId: String,
                status: String
            ): List<SaleEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.status == status
                }.sortedByDescending { it.createdAt }
        }

        val saleBiz1BranchACust1 = Sale(
            id = "sale-iso-01",
            saleNumber = "TRX-001",
            attribution = TransactionAttribution("biz-01", "branch-a", "user-01", "dev-01", createdAt = 1700000000001L),
            customerId = "cust-01",
            items = listOf(SaleItem("item-01", "sale-iso-01", "p-01", "P1", unitPrice = 10000L)),
            paidAmount = 10000L,
            paymentStatus = PaymentStatus.PAID,
            status = SaleStatus.COMPLETED
        )

        val saleBiz1BranchBCust2 = Sale(
            id = "sale-iso-02",
            saleNumber = "TRX-002",
            attribution = TransactionAttribution("biz-01", "branch-b", "user-01", "dev-01", createdAt = 1700000000002L),
            customerId = "cust-02",
            items = listOf(SaleItem("item-02", "sale-iso-02", "p-02", "P2", unitPrice = 20000L)),
            paidAmount = 20000L,
            paymentStatus = PaymentStatus.PAID,
            status = SaleStatus.COMPLETED
        )

        val saleBiz2BranchACust1 = Sale(
            id = "sale-iso-03",
            saleNumber = "TRX-003",
            attribution = TransactionAttribution("biz-02", "branch-a", "user-02", "dev-02", createdAt = 1700000000003L),
            customerId = "cust-01",
            items = listOf(SaleItem("item-03", "sale-iso-03", "p-03", "P3", unitPrice = 30000L)),
            paidAmount = 30000L,
            paymentStatus = PaymentStatus.PAID,
            status = SaleStatus.COMPLETED
        )

        fakeDao.upsert(saleBiz1BranchACust1.toEntity())
        fakeDao.upsert(saleBiz1BranchBCust2.toEntity())
        fakeDao.upsert(saleBiz2BranchACust1.toEntity())

        // Business isolation on findById
        assertNull(fakeDao.findByIdAndBusinessId("sale-iso-01", "biz-02"))
        assertNotNull(fakeDao.findByIdAndBusinessId("sale-iso-01", "biz-01"))

        // Branch isolation on findById
        assertNull(fakeDao.findByIdAndBusinessIdAndBranchId("sale-iso-01", "biz-01", "branch-b"))
        assertNotNull(fakeDao.findByIdAndBusinessIdAndBranchId("sale-iso-01", "biz-01", "branch-a"))

        // Multi-record business isolation
        val biz1Sales = fakeDao.findByBusinessId("biz-01")
        assertEquals(2, biz1Sales.size)
        assertEquals(listOf("sale-iso-02", "sale-iso-01"), biz1Sales.map { it.id })

        val biz2Sales = fakeDao.findByBusinessId("biz-02")
        assertEquals(1, biz2Sales.size)
        assertEquals("sale-iso-03", biz2Sales[0].id)

        // Customer isolation
        val cust1Biz1Sales = fakeDao.findByBusinessIdAndCustomerId("biz-01", "cust-01")
        assertEquals(1, cust1Biz1Sales.size)
        assertEquals("sale-iso-01", cust1Biz1Sales[0].id)

        val cust1Biz2Sales = fakeDao.findByBusinessIdAndCustomerId("biz-02", "cust-01")
        assertEquals(1, cust1Biz2Sales.size)
        assertEquals("sale-iso-03", cust1Biz2Sales[0].id)
    }
}
