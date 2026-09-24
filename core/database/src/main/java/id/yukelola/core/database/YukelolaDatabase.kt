package id.yukelola.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import id.yukelola.core.database.dao.BranchDao
import id.yukelola.core.database.dao.BranchProductOverrideDao
import id.yukelola.core.database.dao.BusinessDao
import id.yukelola.core.database.dao.ProductDao
import id.yukelola.core.database.entity.BranchEntity
import id.yukelola.core.database.entity.BranchProductOverrideEntity
import id.yukelola.core.database.entity.BusinessEntity
import id.yukelola.core.database.entity.ProductEntity

@Database(
    entities = [
        BusinessEntity::class,
        BranchEntity::class,
        ProductEntity::class,
        BranchProductOverrideEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class YukelolaDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao
    abstract fun branchDao(): BranchDao
    abstract fun productDao(): ProductDao
    abstract fun branchProductOverrideDao(): BranchProductOverrideDao
}
