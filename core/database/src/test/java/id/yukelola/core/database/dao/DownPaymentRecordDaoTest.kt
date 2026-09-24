package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.DownPaymentRecordEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.payment.PaymentMethod
import id.yukelola.core.domain.model.serviceorder.DownPaymentRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DownPaymentRecordDaoTest {

    @Test
    fun `DownPaymentRecordDao interface defines required persistence operations`() {
        val upsertMethod = DownPaymentRecordDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("DownPaymentRecordDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(DownPaymentRecordEntity::class.java, upsertMethod.parameterTypes[0])

        val upsertAllMethod = DownPaymentRecordDao::class.java.methods.firstOrNull { it.name == "upsertAll" }
        assertNotNull("DownPaymentRecordDao must declare upsertAll method", upsertAllMethod)
        assertEquals(1, upsertAllMethod!!.parameterTypes.size)
        assertEquals(List::class.java, upsertAllMethod.parameterTypes[0])

        val findByIdMethod = DownPaymentRecordDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("DownPaymentRecordDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(DownPaymentRecordEntity::class.java, findByIdMethod.returnType)

        val findByOrderIdMethod = DownPaymentRecordDao::class.java.methods.firstOrNull { it.name == "findByOrderId" }
        assertNotNull("DownPaymentRecordDao must declare findByOrderId method", findByOrderIdMethod)
        assertEquals(1, findByOrderIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByOrderIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByOrderIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            DownPaymentRecordDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull(
            "DownPaymentRecordDao must declare findByBusinessIdAndBranchId method",
            findByBusinessIdAndBranchIdMethod
        )
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates DownPaymentRecordDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.DownPaymentRecordDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement DownPaymentRecordDao",
            DownPaymentRecordDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : DownPaymentRecordDao {
            private val storage = mutableMapOf<String, DownPaymentRecordEntity>()

            override fun upsert(record: DownPaymentRecordEntity) {
                storage[record.id] = record
            }

            override fun upsertAll(records: List<DownPaymentRecordEntity>) {
                records.forEach { storage[it.id] = it }
            }

            override fun findById(id: String): DownPaymentRecordEntity? = storage[id]

            override fun findByOrderId(orderId: String): List<DownPaymentRecordEntity> =
                storage.values.filter { it.orderId == orderId }.sortedBy { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<DownPaymentRecordEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }
        }

        val dp1 = DownPaymentRecord(
            id = "dp-01",
            businessId = "biz-01",
            branchId = "branch-01",
            orderId = "so-100",
            amount = 25000L,
            paymentMethod = PaymentMethod.CASH,
            notes = "First deposit",
            createdAt = 1700000000001L
        )

        val dp2 = DownPaymentRecord(
            id = "dp-02",
            businessId = "biz-01",
            branchId = "branch-01",
            orderId = "so-100",
            amount = 15000L,
            paymentMethod = PaymentMethod.QRIS,
            notes = "Second deposit",
            createdAt = 1700000000002L
        )

        val dpOther = DownPaymentRecord(
            id = "dp-03",
            businessId = "biz-01",
            branchId = "branch-02",
            orderId = "so-200",
            amount = 50000L,
            paymentMethod = PaymentMethod.TRANSFER,
            notes = "Other order",
            createdAt = 1700000000003L
        )

        fakeDao.upsertAll(listOf(dp1.toEntity(), dp2.toEntity(), dpOther.toEntity()))

        val so100Dps = fakeDao.findByOrderId("so-100")
        assertEquals(2, so100Dps.size)
        assertEquals(listOf("dp-01", "dp-02"), so100Dps.map { it.id })

        val domainDps = so100Dps.map { it.toDomain() }
        assertEquals(listOf(dp1, dp2), domainDps)

        val branch1Dps = fakeDao.findByBusinessIdAndBranchId("biz-01", "branch-01")
        assertEquals(2, branch1Dps.size)
        assertEquals(listOf("dp-02", "dp-01"), branch1Dps.map { it.id })
    }
}
