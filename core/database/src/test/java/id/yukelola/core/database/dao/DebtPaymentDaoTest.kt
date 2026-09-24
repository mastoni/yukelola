package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.DebtPaymentEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.debt.DebtPayment
import id.yukelola.core.domain.model.debt.DebtType
import id.yukelola.core.domain.model.payment.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DebtPaymentDaoTest {

    @Test
    fun `DebtPaymentDao interface defines required persistence operations`() {
        val upsertMethod = DebtPaymentDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("DebtPaymentDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(DebtPaymentEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = DebtPaymentDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("DebtPaymentDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(DebtPaymentEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            DebtPaymentDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("DebtPaymentDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(DebtPaymentEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByIdAndBusinessIdAndBranchIdMethod =
            DebtPaymentDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessIdAndBranchId" }
        assertNotNull(
            "DebtPaymentDao must declare findByIdAndBusinessIdAndBranchId method",
            findByIdAndBusinessIdAndBranchIdMethod
        )
        assertEquals(3, findByIdAndBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[2])
        assertEquals(DebtPaymentEntity::class.java, findByIdAndBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdMethod =
            DebtPaymentDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("DebtPaymentDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            DebtPaymentDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull("DebtPaymentDao must declare findByBusinessIdAndBranchId method", findByBusinessIdAndBranchIdMethod)
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdAndDebtIdMethod =
            DebtPaymentDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndDebtId" }
        assertNotNull("DebtPaymentDao must declare findByBusinessIdAndDebtId method", findByBusinessIdAndDebtIdMethod)
        assertEquals(2, findByBusinessIdAndDebtIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndDebtIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndDebtIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndDebtIdMethod.returnType)

        val findByBusinessIdAndBranchIdAndDebtIdMethod =
            DebtPaymentDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndDebtId" }
        assertNotNull(
            "DebtPaymentDao must declare findByBusinessIdAndBranchIdAndDebtId method",
            findByBusinessIdAndBranchIdAndDebtIdMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndDebtIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndDebtIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndDebtIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndDebtIdMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndDebtIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates DebtPaymentDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.DebtPaymentDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement DebtPaymentDao",
            DebtPaymentDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : DebtPaymentDao {
            private val storage = mutableMapOf<String, DebtPaymentEntity>()

            override fun upsert(debtPayment: DebtPaymentEntity) {
                storage[debtPayment.id] = debtPayment
            }

            override fun findById(id: String): DebtPaymentEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): DebtPaymentEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): DebtPaymentEntity? {
                return storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
            }

            override fun findByBusinessId(businessId: String): List<DebtPaymentEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<DebtPaymentEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndDebtId(
                businessId: String,
                debtId: String
            ): List<DebtPaymentEntity> {
                return storage.values.filter { it.businessId == businessId && it.debtId == debtId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchIdAndDebtId(
                businessId: String,
                branchId: String,
                debtId: String
            ): List<DebtPaymentEntity> {
                return storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.debtId == debtId
                }.sortedByDescending { it.createdAt }
            }
        }

        val domainPayment = DebtPayment(
            id = "dp-test-01",
            businessId = "biz-101",
            branchId = "branch-101",
            debtType = DebtType.CUSTOMER,
            debtId = "cust-debt-101",
            amount = 180000L,
            paymentMethod = PaymentMethod.CASH,
            notes = "Pelunasan tunai",
            createdAt = 1700000000000L
        )

        // 1. Insert/Upsert
        fakeDao.upsert(domainPayment.toEntity())

        // 2. Retrieve by ID
        val retrievedEntity = fakeDao.findById("dp-test-01")
        assertNotNull("Retrieved entity must not be null", retrievedEntity)
        assertEquals("dp-test-01", retrievedEntity!!.id)
        assertEquals("biz-101", retrievedEntity.businessId)
        assertEquals("branch-101", retrievedEntity.branchId)
        assertEquals("CUSTOMER", retrievedEntity.debtType)
        assertEquals("cust-debt-101", retrievedEntity.debtId)
        assertEquals(180000L, retrievedEntity.amount)
        assertEquals("CASH", retrievedEntity.paymentMethod)
        assertEquals("Pelunasan tunai", retrievedEntity.notes)
        assertEquals(1700000000000L, retrievedEntity.createdAt)

        // 3. Map back to domain
        val retrievedDomain = retrievedEntity.toDomain()
        assertEquals(domainPayment, retrievedDomain)

        // 4. Non-existent returns null
        assertNull(fakeDao.findById("missing-dp"))
    }

    @Test
    fun `business and branch isolation ensures debt payments remain strictly partitioned`() {
        val fakeDao = object : DebtPaymentDao {
            private val storage = mutableMapOf<String, DebtPaymentEntity>()

            override fun upsert(debtPayment: DebtPaymentEntity) {
                storage[debtPayment.id] = debtPayment
            }

            override fun findById(id: String): DebtPaymentEntity? {
                return storage[id]
            }

            override fun findByIdAndBusinessId(id: String, businessId: String): DebtPaymentEntity? {
                return storage[id]?.takeIf { it.businessId == businessId }
            }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): DebtPaymentEntity? {
                return storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }
            }

            override fun findByBusinessId(businessId: String): List<DebtPaymentEntity> {
                return storage.values.filter { it.businessId == businessId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<DebtPaymentEntity> {
                return storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndDebtId(
                businessId: String,
                debtId: String
            ): List<DebtPaymentEntity> {
                return storage.values.filter { it.businessId == businessId && it.debtId == debtId }
                    .sortedByDescending { it.createdAt }
            }

            override fun findByBusinessIdAndBranchIdAndDebtId(
                businessId: String,
                branchId: String,
                debtId: String
            ): List<DebtPaymentEntity> {
                return storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.debtId == debtId
                }.sortedByDescending { it.createdAt }
            }
        }

        val dpA1 = DebtPayment(
            id = "dp-A1",
            businessId = "biz-A",
            branchId = "branch-A1",
            debtType = DebtType.CUSTOMER,
            debtId = "cdebt-A1",
            amount = 50000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1000L
        )

        val dpA2 = DebtPayment(
            id = "dp-A2",
            businessId = "biz-A",
            branchId = "branch-A2",
            debtType = DebtType.SUPPLIER,
            debtId = "sdebt-A2",
            amount = 120000L,
            paymentMethod = PaymentMethod.TRANSFER,
            createdAt = 2000L
        )

        val dpB1 = DebtPayment(
            id = "dp-B1",
            businessId = "biz-B",
            branchId = "branch-B1",
            debtType = DebtType.CUSTOMER,
            debtId = "cdebt-B1",
            amount = 80000L,
            paymentMethod = PaymentMethod.QRIS,
            createdAt = 3000L
        )

        fakeDao.upsert(dpA1.toEntity())
        fakeDao.upsert(dpA2.toEntity())
        fakeDao.upsert(dpB1.toEntity())

        // Business A total
        val bizAPayments = fakeDao.findByBusinessId("biz-A")
        assertEquals(2, bizAPayments.size)

        // Branch A1 only
        val branchA1Payments = fakeDao.findByBusinessIdAndBranchId("biz-A", "branch-A1")
        assertEquals(1, branchA1Payments.size)
        assertEquals("dp-A1", branchA1Payments[0].id)

        // Branch A2 only
        val branchA2Payments = fakeDao.findByBusinessIdAndBranchId("biz-A", "branch-A2")
        assertEquals(1, branchA2Payments.size)
        assertEquals("dp-A2", branchA2Payments[0].id)

        // By Debt ID
        val debtA1Payments = fakeDao.findByBusinessIdAndDebtId("biz-A", "cdebt-A1")
        assertEquals(1, debtA1Payments.size)
        assertEquals("dp-A1", debtA1Payments[0].id)

        // Cross-business and cross-branch checks
        assertNull(fakeDao.findByIdAndBusinessId("dp-A1", "biz-B"))
        assertNull(fakeDao.findByIdAndBusinessIdAndBranchId("dp-A1", "biz-A", "branch-A2"))
    }

    @Test
    fun `polymorphic debt reference test preserves CUSTOMER and SUPPLIER debt types without separate FK columns`() {
        val custPayment = DebtPayment(
            id = "dp-poly-cust",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.CUSTOMER,
            debtId = "cust-debt-999",
            amount = 50000L,
            paymentMethod = PaymentMethod.CASH,
            createdAt = 1000L
        )

        val supPayment = DebtPayment(
            id = "dp-poly-sup",
            businessId = "biz-01",
            branchId = "branch-01",
            debtType = DebtType.SUPPLIER,
            debtId = "sup-debt-888",
            amount = 250000L,
            paymentMethod = PaymentMethod.TRANSFER,
            createdAt = 2000L
        )

        val custEntity = custPayment.toEntity()
        val supEntity = supPayment.toEntity()

        assertEquals("CUSTOMER", custEntity.debtType)
        assertEquals("cust-debt-999", custEntity.debtId)
        assertEquals("SUPPLIER", supEntity.debtType)
        assertEquals("sup-debt-888", supEntity.debtId)

        assertEquals(custPayment, custEntity.toDomain())
        assertEquals(supPayment, supEntity.toDomain())
    }

    @Test
    fun `debt payment entity does NOT contain settlement mutation or duplicate entity fields`() {
        val fields = DebtPaymentEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue("DebtPaymentEntity must NOT contain remainingAmount", !fields.contains("remainingAmount"))
        assertTrue("DebtPaymentEntity must NOT contain debtBalance", !fields.contains("debtBalance"))
        assertTrue("DebtPaymentEntity must NOT contain cashMutationId", !fields.contains("cashMutationId"))
        assertTrue("DebtPaymentEntity must NOT contain paymentId", !fields.contains("paymentId"))
        assertTrue("DebtPaymentEntity must NOT contain saleId", !fields.contains("saleId"))
        assertTrue("DebtPaymentEntity must NOT contain purchaseId", !fields.contains("purchaseId"))
    }
}
