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
}
