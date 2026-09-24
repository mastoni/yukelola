package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.ServiceOrderEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.payment.PaymentMethod
import id.yukelola.core.domain.model.sale.TransactionMode
import id.yukelola.core.domain.model.serviceorder.DownPaymentRecord
import id.yukelola.core.domain.model.serviceorder.ServiceOrder
import id.yukelola.core.domain.model.serviceorder.ServiceOrderItem
import id.yukelola.core.domain.model.serviceorder.ServiceOrderItemType
import id.yukelola.core.domain.model.serviceorder.ServiceOrderStatus
import id.yukelola.core.domain.model.serviceorder.ServiceOrderType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceOrderDaoTest {

    @Test
    fun `ServiceOrderDao interface defines required persistence operations`() {
        val upsertMethod = ServiceOrderDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("ServiceOrderDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(ServiceOrderEntity::class.java, upsertMethod.parameterTypes[0])

        val findByIdMethod = ServiceOrderDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("ServiceOrderDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(ServiceOrderEntity::class.java, findByIdMethod.returnType)

        val findByIdAndBusinessIdMethod =
            ServiceOrderDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessId" }
        assertNotNull("ServiceOrderDao must declare findByIdAndBusinessId method", findByIdAndBusinessIdMethod)
        assertEquals(2, findByIdAndBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdMethod.parameterTypes[1])
        assertEquals(ServiceOrderEntity::class.java, findByIdAndBusinessIdMethod.returnType)

        val findByIdAndBusinessIdAndBranchIdMethod =
            ServiceOrderDao::class.java.methods.firstOrNull { it.name == "findByIdAndBusinessIdAndBranchId" }
        assertNotNull(
            "ServiceOrderDao must declare findByIdAndBusinessIdAndBranchId method",
            findByIdAndBusinessIdAndBranchIdMethod
        )
        assertEquals(3, findByIdAndBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByIdAndBusinessIdAndBranchIdMethod.parameterTypes[2])
        assertEquals(ServiceOrderEntity::class.java, findByIdAndBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdMethod =
            ServiceOrderDao::class.java.methods.firstOrNull { it.name == "findByBusinessId" }
        assertNotNull("ServiceOrderDao must declare findByBusinessId method", findByBusinessIdMethod)
        assertEquals(1, findByBusinessIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByBusinessIdMethod.returnType)

        val findByBusinessIdAndBranchIdMethod =
            ServiceOrderDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchId" }
        assertNotNull("ServiceOrderDao must declare findByBusinessIdAndBranchId method", findByBusinessIdAndBranchIdMethod)
        assertEquals(2, findByBusinessIdAndBranchIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdMethod.returnType)

        val findByBusinessIdAndCustomerIdMethod =
            ServiceOrderDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndCustomerId" }
        assertNotNull("ServiceOrderDao must declare findByBusinessIdAndCustomerId method", findByBusinessIdAndCustomerIdMethod)
        assertEquals(2, findByBusinessIdAndCustomerIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndCustomerIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndCustomerIdMethod.parameterTypes[1])
        assertEquals(List::class.java, findByBusinessIdAndCustomerIdMethod.returnType)

        val findByBusinessIdAndBranchIdAndCustomerIdMethod =
            ServiceOrderDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndCustomerId" }
        assertNotNull(
            "ServiceOrderDao must declare findByBusinessIdAndBranchIdAndCustomerId method",
            findByBusinessIdAndBranchIdAndCustomerIdMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndCustomerIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndCustomerIdMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndCustomerIdMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndCustomerIdMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndCustomerIdMethod.returnType)

        val findByBusinessIdAndBranchIdAndStatusMethod =
            ServiceOrderDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndStatus" }
        assertNotNull(
            "ServiceOrderDao must declare findByBusinessIdAndBranchIdAndStatus method",
            findByBusinessIdAndBranchIdAndStatusMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndStatusMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndStatusMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndStatusMethod.returnType)

        val findByBusinessIdAndBranchIdAndOrderTypeMethod =
            ServiceOrderDao::class.java.methods.firstOrNull { it.name == "findByBusinessIdAndBranchIdAndOrderType" }
        assertNotNull(
            "ServiceOrderDao must declare findByBusinessIdAndBranchIdAndOrderType method",
            findByBusinessIdAndBranchIdAndOrderTypeMethod
        )
        assertEquals(3, findByBusinessIdAndBranchIdAndOrderTypeMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndOrderTypeMethod.parameterTypes[0])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndOrderTypeMethod.parameterTypes[1])
        assertEquals(String::class.java, findByBusinessIdAndBranchIdAndOrderTypeMethod.parameterTypes[2])
        assertEquals(List::class.java, findByBusinessIdAndBranchIdAndOrderTypeMethod.returnType)
    }

    @Test
    fun `Room KSP generates ServiceOrderDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.ServiceOrderDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement ServiceOrderDao",
            ServiceOrderDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : ServiceOrderDao {
            private val storage = mutableMapOf<String, ServiceOrderEntity>()

            override fun upsert(serviceOrder: ServiceOrderEntity) {
                storage[serviceOrder.id] = serviceOrder
            }

            override fun findById(id: String): ServiceOrderEntity? = storage[id]

            override fun findByIdAndBusinessId(id: String, businessId: String): ServiceOrderEntity? =
                storage[id]?.takeIf { it.businessId == businessId }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): ServiceOrderEntity? =
                storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }

            override fun findByBusinessId(businessId: String): List<ServiceOrderEntity> =
                storage.values.filter { it.businessId == businessId }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<ServiceOrderEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndCustomerId(
                businessId: String,
                customerId: String
            ): List<ServiceOrderEntity> =
                storage.values.filter { it.businessId == businessId && it.customerId == customerId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndCustomerId(
                businessId: String,
                branchId: String,
                customerId: String
            ): List<ServiceOrderEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.customerId == customerId
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndStatus(
                businessId: String,
                branchId: String,
                status: String
            ): List<ServiceOrderEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.status == status
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndOrderType(
                businessId: String,
                branchId: String,
                orderType: String
            ): List<ServiceOrderEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.orderType == orderType
                }.sortedByDescending { it.createdAt }
        }

        val item = ServiceOrderItem(
            id = "soi-dao-01",
            orderId = "so-dao-01",
            productId = "prod-01",
            productName = "Cuci Sepatu",
            itemType = ServiceOrderItemType.SERVICE_LABOR,
            unit = "PASANG",
            unitPrice = 35000L,
            costPrice = 5000L,
            quantity = 2.0,
            discountAmount = 0L,
            subtotal = 70000L
        )

        val dp = DownPaymentRecord(
            id = "dp-dao-01",
            businessId = "biz-01",
            branchId = "branch-01",
            orderId = "so-dao-01",
            amount = 30000L,
            paymentMethod = PaymentMethod.CASH,
            notes = "Uang muka",
            createdAt = 1700000000000L
        )

        val domain = ServiceOrder(
            id = "so-dao-01",
            orderNumber = "SO-DAO-001",
            orderType = ServiceOrderType.LAUNDRY,
            transactionMode = TransactionMode.SERVICE_ORDER_TRANSACTION,
            attribution = TransactionAttribution(
                businessId = "biz-01",
                branchId = "branch-01",
                userId = "user-01",
                deviceId = "dev-01",
                cashierSessionId = "sess-01",
                createdAt = 1700000000000L
            ),
            customerId = "cust-01",
            items = listOf(item),
            downPayments = listOf(dp),
            discountAmount = 0L,
            taxAmount = 0L,
            status = ServiceOrderStatus.IN_PROGRESS,
            notes = "Deep cleaning",
            createdAt = 1700000000000L,
            updatedAt = 1700000000000L
        )

        fakeDao.upsert(domain.toEntity())

        val retrievedEntity = fakeDao.findById("so-dao-01")
        assertNotNull(retrievedEntity)
        val retrievedDomain = retrievedEntity!!.toDomain(listOf(item), listOf(dp))

        assertEquals(domain, retrievedDomain)
        assertEquals(ServiceOrderStatus.IN_PROGRESS, retrievedDomain.status)
        assertEquals(70000L, retrievedDomain.totalAmount)
        assertEquals(30000L, retrievedDomain.downPaymentAmount)
        assertEquals(40000L, retrievedDomain.remainingBalance)
    }

    @Test
    fun `business and branch and customer and status and orderType isolation strictly enforced`() {
        val fakeDao = object : ServiceOrderDao {
            private val storage = mutableMapOf<String, ServiceOrderEntity>()

            override fun upsert(serviceOrder: ServiceOrderEntity) {
                storage[serviceOrder.id] = serviceOrder
            }

            override fun findById(id: String): ServiceOrderEntity? = storage[id]

            override fun findByIdAndBusinessId(id: String, businessId: String): ServiceOrderEntity? =
                storage[id]?.takeIf { it.businessId == businessId }

            override fun findByIdAndBusinessIdAndBranchId(
                id: String,
                businessId: String,
                branchId: String
            ): ServiceOrderEntity? =
                storage[id]?.takeIf { it.businessId == businessId && it.branchId == branchId }

            override fun findByBusinessId(businessId: String): List<ServiceOrderEntity> =
                storage.values.filter { it.businessId == businessId }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchId(
                businessId: String,
                branchId: String
            ): List<ServiceOrderEntity> =
                storage.values.filter { it.businessId == businessId && it.branchId == branchId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndCustomerId(
                businessId: String,
                customerId: String
            ): List<ServiceOrderEntity> =
                storage.values.filter { it.businessId == businessId && it.customerId == customerId }
                    .sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndCustomerId(
                businessId: String,
                branchId: String,
                customerId: String
            ): List<ServiceOrderEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.customerId == customerId
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndStatus(
                businessId: String,
                branchId: String,
                status: String
            ): List<ServiceOrderEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.status == status
                }.sortedByDescending { it.createdAt }

            override fun findByBusinessIdAndBranchIdAndOrderType(
                businessId: String,
                branchId: String,
                orderType: String
            ): List<ServiceOrderEntity> =
                storage.values.filter {
                    it.businessId == businessId && it.branchId == branchId && it.orderType == orderType
                }.sortedByDescending { it.createdAt }
        }

        val order1 = ServiceOrder(
            id = "so-iso-01",
            orderNumber = "SO-001",
            orderType = ServiceOrderType.LAUNDRY,
            attribution = TransactionAttribution("biz-01", "branch-a", "user-01", "dev-01", createdAt = 1700000000001L),
            customerId = "cust-01",
            items = listOf(ServiceOrderItem("item-01", "so-iso-01", "p-01", "P1", unitPrice = 10000L)),
            status = ServiceOrderStatus.RECEIVED
        )

        val order2 = ServiceOrder(
            id = "so-iso-02",
            orderNumber = "SO-002",
            orderType = ServiceOrderType.WORKSHOP,
            attribution = TransactionAttribution("biz-01", "branch-b", "user-01", "dev-01", createdAt = 1700000000002L),
            customerId = "cust-02",
            items = listOf(ServiceOrderItem("item-02", "so-iso-02", "p-02", "P2", unitPrice = 20000L)),
            status = ServiceOrderStatus.IN_PROGRESS
        )

        val order3 = ServiceOrder(
            id = "so-iso-03",
            orderNumber = "SO-003",
            orderType = ServiceOrderType.LAUNDRY,
            attribution = TransactionAttribution("biz-02", "branch-a", "user-02", "dev-02", createdAt = 1700000000003L),
            customerId = "cust-01",
            items = listOf(ServiceOrderItem("item-03", "so-iso-03", "p-03", "P3", unitPrice = 30000L)),
            status = ServiceOrderStatus.RECEIVED
        )

        fakeDao.upsert(order1.toEntity())
        fakeDao.upsert(order2.toEntity())
        fakeDao.upsert(order3.toEntity())

        // Business isolation on findById
        assertNull(fakeDao.findByIdAndBusinessId("so-iso-01", "biz-02"))
        assertNotNull(fakeDao.findByIdAndBusinessId("so-iso-01", "biz-01"))

        // Branch isolation on findById
        assertNull(fakeDao.findByIdAndBusinessIdAndBranchId("so-iso-01", "biz-01", "branch-b"))
        assertNotNull(fakeDao.findByIdAndBusinessIdAndBranchId("so-iso-01", "biz-01", "branch-a"))

        // Multi-record business isolation
        val biz1Orders = fakeDao.findByBusinessId("biz-01")
        assertEquals(2, biz1Orders.size)
        assertEquals(listOf("so-iso-02", "so-iso-01"), biz1Orders.map { it.id })

        val biz2Orders = fakeDao.findByBusinessId("biz-02")
        assertEquals(1, biz2Orders.size)
        assertEquals("so-iso-03", biz2Orders[0].id)

        // Customer isolation
        val cust1Biz1Orders = fakeDao.findByBusinessIdAndCustomerId("biz-01", "cust-01")
        assertEquals(1, cust1Biz1Orders.size)
        assertEquals("so-iso-01", cust1Biz1Orders[0].id)

        val cust1Biz2Orders = fakeDao.findByBusinessIdAndCustomerId("biz-02", "cust-01")
        assertEquals(1, cust1Biz2Orders.size)
        assertEquals("so-iso-03", cust1Biz2Orders[0].id)

        // Status isolation
        val inProgressBranchB = fakeDao.findByBusinessIdAndBranchIdAndStatus("biz-01", "branch-b", "IN_PROGRESS")
        assertEquals(1, inProgressBranchB.size)
        assertEquals("so-iso-02", inProgressBranchB[0].id)

        // OrderType isolation
        val laundryBranchA = fakeDao.findByBusinessIdAndBranchIdAndOrderType("biz-01", "branch-a", "LAUNDRY")
        assertEquals(1, laundryBranchA.size)
        assertEquals("so-iso-01", laundryBranchA[0].id)
    }
}
