package id.yukelola.core.database.dao

import id.yukelola.core.database.entity.SaleItemEntity
import id.yukelola.core.database.mapper.toDomain
import id.yukelola.core.database.mapper.toEntity
import id.yukelola.core.domain.model.sale.SaleItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SaleItemDaoTest {

    @Test
    fun `SaleItemDao interface defines required persistence operations`() {
        val upsertMethod = SaleItemDao::class.java.methods.firstOrNull { it.name == "upsert" }
        assertNotNull("SaleItemDao must declare upsert method", upsertMethod)
        assertEquals(1, upsertMethod!!.parameterTypes.size)
        assertEquals(SaleItemEntity::class.java, upsertMethod.parameterTypes[0])

        val upsertAllMethod = SaleItemDao::class.java.methods.firstOrNull { it.name == "upsertAll" }
        assertNotNull("SaleItemDao must declare upsertAll method", upsertAllMethod)
        assertEquals(1, upsertAllMethod!!.parameterTypes.size)
        assertEquals(List::class.java, upsertAllMethod.parameterTypes[0])

        val findByIdMethod = SaleItemDao::class.java.methods.firstOrNull { it.name == "findById" }
        assertNotNull("SaleItemDao must declare findById method", findByIdMethod)
        assertEquals(1, findByIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findByIdMethod.parameterTypes[0])
        assertEquals(SaleItemEntity::class.java, findByIdMethod.returnType)

        val findBySaleIdMethod = SaleItemDao::class.java.methods.firstOrNull { it.name == "findBySaleId" }
        assertNotNull("SaleItemDao must declare findBySaleId method", findBySaleIdMethod)
        assertEquals(1, findBySaleIdMethod!!.parameterTypes.size)
        assertEquals(String::class.java, findBySaleIdMethod.parameterTypes[0])
        assertEquals(List::class.java, findBySaleIdMethod.returnType)
    }

    @Test
    fun `Room KSP generates SaleItemDao_Impl`() {
        val daoImplClass = Class.forName("id.yukelola.core.database.dao.SaleItemDao_Impl")
        assertNotNull("Generated Room DAO implementation must exist", daoImplClass)
        assertTrue(
            "Generated implementation must implement SaleItemDao",
            SaleItemDao::class.java.isAssignableFrom(daoImplClass)
        )
    }

    @Test
    fun `persistence slice supports simulated DAO insert and retrieval with domain mapper`() {
        val fakeDao = object : SaleItemDao {
            private val storage = mutableMapOf<String, SaleItemEntity>()

            override fun upsert(item: SaleItemEntity) {
                storage[item.id] = item
            }

            override fun upsertAll(items: List<SaleItemEntity>) {
                items.forEach { storage[it.id] = it }
            }

            override fun findById(id: String): SaleItemEntity? = storage[id]

            override fun findBySaleId(saleId: String): List<SaleItemEntity> =
                storage.values.filter { it.saleId == saleId }
        }

        val item1 = SaleItem(
            id = "item-01",
            saleId = "sale-100",
            productId = "prod-01",
            productName = "Product 1",
            unit = "PCS",
            unitPrice = 15000L,
            costPrice = 10000L,
            quantity = 2.0,
            discountAmount = 1000L,
            subtotal = 29000L
        )

        val item2 = SaleItem(
            id = "item-02",
            saleId = "sale-100",
            productId = "prod-02",
            productName = "Product 2",
            unit = "BTL",
            unitPrice = 25000L,
            costPrice = 20000L,
            quantity = 1.0,
            discountAmount = 0L,
            subtotal = 25000L
        )

        val otherSaleItem = SaleItem(
            id = "item-03",
            saleId = "sale-200",
            productId = "prod-03",
            productName = "Product 3",
            unit = "PCS",
            unitPrice = 5000L
        )

        fakeDao.upsertAll(listOf(item1.toEntity(), item2.toEntity(), otherSaleItem.toEntity()))

        val sale100Items = fakeDao.findBySaleId("sale-100")
        assertEquals(2, sale100Items.size)
        assertEquals(listOf("item-01", "item-02"), sale100Items.map { it.id })

        val domainItems = sale100Items.map { it.toDomain() }
        assertEquals(listOf(item1, item2), domainItems)

        val singleItem = fakeDao.findById("item-01")
        assertNotNull(singleItem)
        assertEquals(item1, singleItem!!.toDomain())
    }
}
