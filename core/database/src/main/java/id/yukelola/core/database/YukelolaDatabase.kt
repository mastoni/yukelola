package id.yukelola.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import id.yukelola.core.database.dao.BusinessDao
import id.yukelola.core.database.entity.BusinessEntity

@Database(
    entities = [
        BusinessEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class YukelolaDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao
}
