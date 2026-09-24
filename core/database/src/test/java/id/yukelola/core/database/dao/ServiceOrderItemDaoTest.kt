package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.ServiceOrderItemEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.serviceorder.ServiceOrderItem
import id.yukelola.core.domain.model.serviceorder.ServiceOrderItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceOrderItemDaoTest {

    @Test
    fun `ServiceOrderItemDao interface defines required persistence operations`() {
        val upsertMethod = ServiceOrderItemDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("ServiceOrderItemDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(ServiceOrderItemEntity::class.java, upsertMethod.parameterTypes[0])

        val upsertAllMethod = ServiceOrderItemDao::class.java.methods.firstOrNull { it.name == "upsertAll" }
        assertNotNull("ServiceOrderItemDao must declare upsertAll method", upsertAllMethod)
        assertEquals(1, upsertAllMethod!!.parameterTypes.size)
        assertEquals(List::class.java, upsertAllMethod.parameterTypes[0])

        val findByIdMethod = ServiceOrderItemDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("ServiceOrderItemDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(ServiceOrderItemEntity::class.java, findByIdMethod.returnType)

        val findByOrderIdMethod = ServiceOrderItemDao::class.java.methods.firstOrNull { it.name == "findByOrderId" }
        assertNotNull("ServiceOrderItemDao must declare findByOrderId method", findByOrderIdMethod)
        assertEquals(1, findByOrderIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByOrderIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByOrderIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates ServiceOrderItemDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.ServiceOrderItemDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement ServiceOrderItemDao",
            ServiceOrderItemDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : ServiceOrderItemDao {
            private val storage = mutableMapOf<String, ServiceOrderItemEntity>()

            override fun upsert(item: ServiceOrderItemEntity) {
                storage[item.id] = item
            }

            override fun upsertAll(items: List<ServiceOrderItemEntity>) {
                items.forEach { storage[it.id] = it }
            }

            override fun findById(id: String): ServiceOrderItemEntity? = storage[id]

            override fun findByOrderId(orderId: String): List<ServiceOrderItemEntity> =
                storage.values.filter { it.orderId == orderId }
        }

        val item1 = ServiceOrderItem(
            id = "soi-01",
            orderId = "so-100",
            productId = "prod-01",
            productName = "Cuci Bedcover",
            itemType = ServiceOrderItemType.SERVICE_LABOR,
            unit = "PCS",
            unitPrice = 30000L,
            costPrice = 8000L,
            quantity = 2.0,
            discountAmount = 5000L,
            subtotal = 55000L
        )

        val item2 = ServiceOrderItem(
            id = "soi-02",
            orderId = "so-100",
            productId = "prod-02",
            productName = "Parfum Laundry Tambahan",
            itemType = ServiceOrderItemType.PHYSICAL_PART,
            unit = "BTL",
            unitPrice = 15000L,
            costPrice = 10000L,
            quantity = 1.0,
            discountAmount = 0L,
            subtotal = 15000L
        )

        val otherOrderItem = ServiceOrderItem(
            id = "soi-03",
            orderId = "so-200",
            productId = "prod-03",
            productName = "Cuci Karpet",
            itemType = ServiceOrderItemType.SERVICE_LABOR,
            unit = "M2",
            unitPrice = 20000L
        )

        fakeDao.upsertAll(listOf(item1.toEntity(), item2.toEntity(), otherOrderItem.toEntity()))

        val so100Items = fakeDao.findByOrderId("so-100")
        assertEquals(2, so100Items.size)
        assertEquals(listOf("soi-01", "soi-02"), so100Items.map { it.id })

        val domainItems = so100Items.map { it.toDomain() }
        assertEquals(listOf(item1, item2), domainItems)

        val singleItem = fakeDao.findById("soi-01")
        assertNotNull(singleItem)
        assertEquals(item1, singleItem!!.toDomain())
    }
}
