package id.yukelola.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import id.yukelola.core.database.dao.BranchDao
import id.yukelola.core.database.dao.BranchProductOverrideDao
import id.yukelola.core.database.dao.BusinessDao
import id.yukelola.core.database.dao.CategoryDao
import id.yukelola.core.database.dao.CustomerDao
import id.yukelola.core.database.dao.CustomerDebtDao
import id.yukelola.core.database.dao.ProductDao
import id.yukelola.core.database.dao.ProductUnitDao
import id.yukelola.core.database.dao.SupplierDao
import id.yukelola.core.database.dao.SupplierDebtDao
import id.yukelola.core.database.entity.BranchEntity
import id.yukelola.core.database.entity.BranchProductOverrideEntity
import id.yukelola.core.database.entity.BusinessEntity
import id.yukelola.core.database.entity.CategoryEntity
import id.yukelola.core.database.entity.CustomerDebtEntity
import id.yukelola.core.database.entity.CustomerEntity
import id.yukelola.core.database.entity.ProductEntity
import id.yukelola.core.database.entity.ProductUnitEntity
import id.yukelola.core.database.entity.SupplierDebtEntity
import id.yukelola.core.database.entity.SupplierEntity

@Database(
    entities = [
        BusinessEntity::class,
        BranchEntity::class,
        ProductEntity::class,
        BranchProductOverrideEntity::class,
        CategoryEntity::class,
        ProductUnitEntity::class,
        CustomerEntity::class,
        SupplierEntity::class,
        SupplierDebtEntity::class,
        CustomerDebtEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class YukelolaDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao
    abstract fun branchDao(): BranchDao
    abstract fun productDao(): ProductDao
    abstract fun branchProductOverrideDao(): BranchProductOverrideDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productUnitDao(): ProductUnitDao
    abstract fun customerDao(): CustomerDao
    abstract fun supplierDao(): SupplierDao
    abstract fun supplierDebtDao(): SupplierDebtDao
    abstract fun customerDebtDao(): CustomerDebtDao
}
