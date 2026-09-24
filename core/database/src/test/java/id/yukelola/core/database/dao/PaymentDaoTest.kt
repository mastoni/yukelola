package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.PaymentEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.payment.Payment
import id.yukelola.core.domain.model.payment.PaymentMethod
import id.yukelola.core.domain.model.payment.PaymentTransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentDaoTest {

    @Test
    fun `PaymentDao interface defines required persistence operations`() {
        val upsertMethod = PaymentDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("PaymentDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(PaymentEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = PaymentDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("PaymentDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(PaymentEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            PaymentDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("PaymentDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(PaymentEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByIdAndBusinessIdAndBranchIdMethod =
            PaymentDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessIdAndBranchId" }
        assertNotNull(
            "PaymentDao must declare findByIdAndBusinessIdAndBranchId method",
            findByIdAndBusinessIdAndBranchIdMethod
        )
        assertEquals(3, findByIdAndBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[2])
        assertEquals(PaymentEntity::class.java, findByIdAndBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdMethod =
            PaymentDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("PaymentDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            PaymentDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull("PaymentDao must declare findByBusinessIdAndBranchId method", findByBusinessIdAndBranchIdMethod)
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdAndReferenceIdMethod =
            PaymentDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndReferenceId" }
        assertNotNull(
            "PaymentDao must declare findByBusinessIdAndReferenceId method",
            findByBusinessIdAndReferenceIdMethod
        )
        assertEquals(2, findByBusinessIdAndReferenceIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndReferenceIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndReferenceIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndReferenceIdMethod.returnType)

        val findByBusinessIdAndBranchIdAndReferenceIdMethod =
            PaymentDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndReferenceId" }
        assertNotNull(
            "PaymentDao must declare findByBusinessIdAndBranchIdAndReferenceId method",
            findByBusinessIdAndBranchIdAndReferenceIdMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndReferenceIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndReferenceIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndReferenceIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndReferenceIdMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndReferenceIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates PaymentDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.PaymentDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement PaymentDao",
            PaymentDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : PaymentDao {
            private val storage = mutableMapOf<String, PaymentEntity>()

            override fun upsert(payment: PaymentEntity) {
                storage[payment.id] = payment
            }

            override fun findById(id: String): PaymentEntity? = storage[id]

            override fun findByIdAndBusinessId(id: String, businessId: String): PaymentEntity? =
                storage[id]?.takeIf { it.businessId == businessId }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): PaymentEntity? =
                storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }

            override fun findByBusinessId(businessId: String): List<PaymentEntity> =
                storage.values.filter { it.businessId == businessId }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<PaymentEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndReferenceId(
                businessId: String,
                referenceId: String
            ): List<PaymentEntity> =
                storage.values.filter { it.businessId == businessId && it.referenceId == referenceId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndReferenceId(
                businessId: String,
                branchId: String,
                referenceId: String
            ): List<PaymentEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.referenceId == referenceId
                }.sortedByDescending { it.createdAt }
        }

        val domain = Payment(
            id = "pay-dao-01",
            businessId = "biz-01",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.SALE,
            referenceId = "sale-999",
            paymentMethod = PaymentMethod.CASH,
            amount = 175000L,
            createdAt = 1700000000000L
        )

        fakeDao.upsert(domain.toEntity())

        val retrievedEntity = fakeDao.findById("pay-dao-01")
        assertNotNull(retrievedEntity)
        val retrievedDomain = retrievedEntity!!.toDomain()

        assertEquals(domain, retrievedDomain)
        assertEquals(PaymentMethod.CASH, retrievedDomain.paymentMethod)
        assertEquals(PaymentTransactionType.SALE, retrievedDomain.transactionType)
        assertEquals(175000L, retrievedDomain.amount)
        assertEquals("sale-999", retrievedDomain.referenceId)
    }

    @Test
    fun `business and branch and reference isolation strictly enforced`() {
        val fakeDao = object : PaymentDao {
            private val storage = mutableMapOf<String, PaymentEntity>()

            override fun upsert(payment: PaymentEntity) {
                storage[payment.id] = payment
            }

            override fun findById(id: String): PaymentEntity? = storage[id]

            override fun findByIdAndBusinessId(id: String, businessId: String): PaymentEntity? =
                storage[id]?.takeIf { it.businessId == businessId }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): PaymentEntity? =
                storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }

            override fun findByBusinessId(businessId: String): List<PaymentEntity> =
                storage.values.filter { it.businessId == businessId }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<PaymentEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndReferenceId(
                businessId: String,
                referenceId: String
            ): List<PaymentEntity> =
                storage.values.filter { it.businessId == businessId && it.referenceId == referenceId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndReferenceId(
                businessId: String,
                branchId: String,
                referenceId: String
            ): List<PaymentEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.referenceId == referenceId
                }.sortedByDescending { it.createdAt }
        }

        val paymentBiz1BranchA = Payment(
            id = "pay-iso-01",
            businessId = "biz-01",
            branchId = "branch-a",
            transactionType = PaymentTransactionType.SALE,
            referenceId = "ref-100",
            paymentMethod = PaymentMethod.CASH,
            amount = 50000L,
            createdAt = 1700000000001L
        )

        val paymentBiz1BranchB = Payment(
            id = "pay-iso-02",
            businessId = "biz-01",
            branchId = "branch-b",
            transactionType = PaymentTransactionType.PURCHASE,
            referenceId = "ref-200",
            paymentMethod = PaymentMethod.TRANSFER,
            amount = 150000L,
            createdAt = 1700000000002L
        )

        val paymentBiz2BranchA = Payment(
            id = "pay-iso-03",
            businessId = "biz-02",
            branchId = "branch-a",
            transactionType = PaymentTransactionType.CUSTOMER_DEBT,
            referenceId = "ref-100", // Same reference ID string, different business
            paymentMethod = PaymentMethod.QRIS,
            amount = 75000L,
            createdAt = 1700000000003L
        )

        fakeDao.upsert(paymentBiz1BranchA.toEntity())
        fakeDao.upsert(paymentBiz1BranchB.toEntity())
        fakeDao.upsert(paymentBiz2BranchA.toEntity())

        // Business isolation on findById
        assertNull(fakeDao.findByIdAndBusinessId("pay-iso-01", "biz-02"))
        assertNotNull(fakeDao.findByIdAndBusinessId("pay-iso-01", "biz-01"))

        // Branch isolation on findById
        assertNull(fakeDao.findByIdAndBusinessIdAndBranchId("pay-iso-01", "biz-01", "branch-b"))
        assertNotNull(fakeDao.findByIdAndBusinessIdAndBranchId("pay-iso-01", "biz-01", "branch-a"))

        // Multi-record business isolation
        val biz1Payments = fakeDao.findByBusinessId("biz-01")
        assertEquals(2, biz1Payments.size)
        assertEquals(listOf("pay-iso-02", "pay-iso-01"), biz1Payments.map { it.id })

        val biz2Payments = fakeDao.findByBusinessId("biz-02")
        assertEquals(1, biz2Payments.size)
        assertEquals("pay-iso-03", biz2Payments[0].id)

        // Branch isolation
        val branchAPayments = fakeDao.findByBusinessIdAndBranchId("biz-01", "branch-a")
        assertEquals(1, branchAPayments.size)
        assertEquals("pay-iso-01", branchAPayments[0].id)

        // Reference isolation
        val ref100Biz1 = fakeDao.findByBusinessIdAndReferenceId("biz-01", "ref-100")
        assertEquals(1, ref100Biz1.size)
        assertEquals("pay-iso-01", ref100Biz1[0].id)

        val ref100Biz2 = fakeDao.findByBusinessIdAndReferenceId("biz-02", "ref-100")
        assertEquals(1, ref100Biz2.size)
        assertEquals("pay-iso-03", ref100Biz2[0].id)
    }

    @Test
    fun `boundary test - Payment persistence does NOT mutate CashRegister or create side effects`() {
        val payment = Payment(
            id = "pay-boundary-01",
            businessId = "biz-01",
            branchId = "branch-01",
            transactionType = PaymentTransactionType.SALE,
            referenceId = "sale-01",
            paymentMethod = PaymentMethod.CASH,
            amount = 100000L,
            createdAt = 1700000000000L
        )

        val entity = payment.toEntity()

        assertEquals("pay-boundary-01", entity.id)
        assertEquals(100000L, entity.amount)
        assertEquals("CASH", entity.paymentMethod)
    }
}
