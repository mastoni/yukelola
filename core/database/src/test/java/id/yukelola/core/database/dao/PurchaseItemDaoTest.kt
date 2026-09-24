package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.PurchaseItemEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.purchase.PurchaseItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PurchaseItemDaoTest {

    @Test
    fun `PurchaseItemDao interface defines required persistence operations`() {
        val upsertMethod = PurchaseItemDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("PurchaseItemDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(PurchaseItemEntity::class.java, upsertMethod.parameterTypes[0])

        val upsertAllMethod = PurchaseItemDao::class.java.methods.firstOrNull { it.name == "upsertAll" }
        assertNotNull("PurchaseItemDao must declare upsertAll method", upsertAllMethod)
        assertEquals(1, upsertAllMethod!!.parameterTypes.size)
        assertEquals(List::class.java, upsertAllMethod.parameterTypes[0])

        val findByIdMethod = PurchaseItemDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("PurchaseItemDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(PurchaseItemEntity::class.java, findByIdMethod.returnType)

        val findByPurchaseIdMethod = PurchaseItemDao::class.java.methods.firstOrNull { it.name == "findByPurchaseId" }
        assertNotNull("PurchaseItemDao must declare findByPurchaseId method", findByPurchaseIdMethod)
        assertEquals(1, findByPurchaseIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByPurchaseIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findByPurchaseIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates PurchaseItemDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.PurchaseItemDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement PurchaseItemDao",
            PurchaseItemDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : PurchaseItemDao {
            private val storage = mutableMapOf<String, PurchaseItemEntity>()

            override fun upsert(item: PurchaseItemEntity) {
                storage[item.id] = item
            }

            override fun upsertAll(items: List<PurchaseItemEntity>) {
                items.forEach { storage[it.id] = it }
            }

            override fun findById(id: String): PurchaseItemEntity? = storage[id]

            override fun findByPurchaseId(purchaseId: String): List<PurchaseItemEntity> =
                storage.values.filter { it.purchaseId == purchaseId }
        }

        val item1 = PurchaseItem(
            id = "pi-01",
            purchaseId = "purch-100",
            productId = "prod-01",
            productName = "Product 1",
            unit = "PCS",
            unitCost = 25000L,
            quantity = 10.0,
            subtotal = 250000L
        )

        val item2 = PurchaseItem(
            id = "pi-02",
            purchaseId = "purch-100",
            productId = "prod-02",
            productName = "Product 2",
            unit = "KG",
            unitCost = 50000L,
            quantity = 5.0,
            subtotal = 250000L
        )

        val otherPurchaseItem = PurchaseItem(
            id = "pi-03",
            purchaseId = "purch-200",
            productId = "prod-03",
            productName = "Product 3",
            unit = "PCS",
            unitCost = 15000L
        )

        fakeDao.upsertAll(listOf(item1.toEntity(), item2.toEntity(), otherPurchaseItem.toEntity()))

        val purch100Items = fakeDao.findByPurchaseId("purch-100")
        assertEquals(2, purch100Items.size)
        assertEquals(listOf("pi-01", "pi-02"), purch100Items.map { it.id })

        val domainItems = purch100Items.map { it.toDomain() }
        assertEquals(listOf(item1, item2), domainItems)

        val singleItem = fakeDao.findById("pi-01")
        assertNotNull(singleItem)
        assertEquals(item1, singleItem!!.toDomain())
    }
}
