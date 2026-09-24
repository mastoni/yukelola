package id.yukelola.core.database

import androidx.room.RoomDatabase
import id.yukelola.core.database.entity.BusinessEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class YukelolaDatabaseTest {

    @Test
    fun `YukelolaDatabase extends RoomDatabase`() {
        assertTrue(RoomDatabase::class.java.isAssignableFrom(YukelolaDatabase::class.java))
    }

    @Test
    fun `Room KSP generates YukelolaDatabase_Impl`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        assertNotNull("Generated Room database implementation must exist", implClass)
        assertTrue(
            "Generated implementation must extend YukelolaDatabase",
            YukelolaDatabase::class.java.isAssignableFrom(implClass)
        )
    }

    @Test
    fun `BusinessEntity fields match canonical contract`() {
        val entity = BusinessEntity(
            id = "biz-01",
            legalName = "Yukelola Store",
            ownerUserId = "user-01",
            createdAt = 1700000000000L,
            isActive = true
        )
        assertEquals("biz-01", entity.id)
        assertEquals("Yukelola Store", entity.legalName)
        assertEquals("user-01", entity.ownerUserId)
        assertEquals(1700000000000L, entity.createdAt)
        assertTrue(entity.isActive)
    }

    @Test
    fun `YukelolaDatabase declares businessDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "businessDao" }
        assertNotNull("YukelolaDatabase must declare abstract businessDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.BusinessDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements businessDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "businessDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement businessDao method", method)
        assertEquals(id.yukelola.core.database.dao.BusinessDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares branchDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "branchDao" }
        assertNotNull("YukelolaDatabase must declare abstract branchDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.BranchDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements branchDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "branchDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement branchDao method", method)
        assertEquals(id.yukelola.core.database.dao.BranchDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares productDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "productDao" }
        assertNotNull("YukelolaDatabase must declare abstract productDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.ProductDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements productDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "productDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement productDao method", method)
        assertEquals(id.yukelola.core.database.dao.ProductDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares branchProductOverrideDao getter`() {
        val method =
            YukelolaDatabase::class.java.methods.firstOrNull { it.name == "branchProductOverrideDao" }
        assertNotNull("YukelolaDatabase must declare abstract branchProductOverrideDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(
            id.yukelola.core.database.dao.BranchProductOverrideDao::class.java,
            method.returnType
        )
    }

    @Test
    fun `YukelolaDatabase_Impl implements branchProductOverrideDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "branchProductOverrideDao" }
        assertNotNull(
            "Generated YukelolaDatabase_Impl must implement branchProductOverrideDao method",
            method
        )
        assertEquals(
            id.yukelola.core.database.dao.BranchProductOverrideDao::class.java,
            method!!.returnType
        )
    }

    @Test
    fun `YukelolaDatabase declares categoryDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "categoryDao" }
        assertNotNull("YukelolaDatabase must declare abstract categoryDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.CategoryDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements categoryDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "categoryDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement categoryDao method", method)
        assertEquals(id.yukelola.core.database.dao.CategoryDao::class.java, method!!.returnType)
    }
}
