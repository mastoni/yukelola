package id.yukelola.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import id.yukelola.core.database.dao.BranchDao
import id.yukelola.core.database.dao.BranchProductOverrideDao
import id.yukelola.core.database.dao.BusinessDao
import id.yukelola.core.database.dao.CashMutationDao
import id.yukelola.core.database.dao.CashRegisterDao
import id.yukelola.core.database.dao.CategoryDao
import id.yukelola.core.database.dao.CustomerDao
import id.yukelola.core.database.dao.CustomerDebtDao
import id.yukelola.core.database.dao.DebtPaymentDao
import id.yukelola.core.database.dao.DigitalDepositAccountDao
import id.yukelola.core.database.dao.DigitalDepositMutationDao
import id.yukelola.core.database.dao.DigitalTransactionComplaintDao
import id.yukelola.core.database.dao.DigitalTransactionDao
import id.yukelola.core.database.dao.DownPaymentRecordDao
import id.yukelola.core.database.dao.InquiryDao
import id.yukelola.core.database.dao.PaymentDao
import id.yukelola.core.database.dao.ProductDao
import id.yukelola.core.database.dao.ProductUnitDao
import id.yukelola.core.database.dao.PurchaseDao
import id.yukelola.core.database.dao.PurchaseItemDao
import id.yukelola.core.database.dao.SaleDao
import id.yukelola.core.database.dao.SaleItemDao
import id.yukelola.core.database.dao.ServiceOrderDao
import id.yukelola.core.database.dao.ServiceOrderItemDao
import id.yukelola.core.database.dao.StockAdjustmentDao
import id.yukelola.core.database.dao.SupplierDao
import id.yukelola.core.database.dao.SupplierDebtDao
import id.yukelola.core.database.entity.BranchEntity
import id.yukelola.core.database.entity.BranchProductOverrideEntity
import id.yukelola.core.database.entity.BusinessEntity
import id.yukelola.core.database.entity.CashMutationEntity
import id.yukelola.core.database.entity.CashRegisterEntity
import id.yukelola.core.database.entity.CategoryEntity
import id.yukelola.core.database.entity.CustomerDebtEntity
import id.yukelola.core.database.entity.CustomerEntity
import id.yukelola.core.database.entity.DebtPaymentEntity
import id.yukelola.core.database.entity.DigitalDepositAccountEntity
import id.yukelola.core.database.entity.DigitalDepositMutationEntity
import id.yukelola.core.database.entity.DigitalTransactionComplaintEntity
import id.yukelola.core.database.entity.DigitalTransactionEntity
import id.yukelola.core.database.entity.DownPaymentRecordEntity
import id.yukelola.core.database.entity.InquiryEntity
import id.yukelola.core.database.entity.PaymentEntity
import id.yukelola.core.database.entity.ProductEntity
import id.yukelola.core.database.entity.ProductUnitEntity
import id.yukelola.core.database.entity.PurchaseEntity
import id.yukelola.core.database.entity.PurchaseItemEntity
import id.yukelola.core.database.entity.SaleEntity
import id.yukelola.core.database.entity.SaleItemEntity
import id.yukelola.core.database.entity.ServiceOrderEntity
import id.yukelola.core.database.entity.ServiceOrderItemEntity
import id.yukelola.core.database.entity.StockAdjustmentEntity
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
        CustomerDebtEntity::class,
        DebtPaymentEntity::class,
        CashRegisterEntity::class,
        CashMutationEntity::class,
        DigitalDepositAccountEntity::class,
        DigitalDepositMutationEntity::class,
        PaymentEntity::class,
        SaleEntity::class,
        SaleItemEntity::class,
        PurchaseEntity::class,
        PurchaseItemEntity::class,
        ServiceOrderEntity::class,
        ServiceOrderItemEntity::class,
        DownPaymentRecordEntity::class,
        DigitalTransactionEntity::class,
        InquiryEntity::class,
        DigitalTransactionComplaintEntity::class,
        StockAdjustmentEntity::class
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
    abstract fun debtPaymentDao(): DebtPaymentDao
    abstract fun cashRegisterDao(): CashRegisterDao
    abstract fun cashMutationDao(): CashMutationDao
    abstract fun digitalDepositAccountDao(): DigitalDepositAccountDao
    abstract fun digitalDepositMutationDao(): DigitalDepositMutationDao
    abstract fun digitalTransactionDao(): DigitalTransactionDao
    abstract fun digitalTransactionComplaintDao(): DigitalTransactionComplaintDao
    abstract fun inquiryDao(): InquiryDao
    abstract fun paymentDao(): PaymentDao
    abstract fun saleDao(): SaleDao
    abstract fun saleItemDao(): SaleItemDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun purchaseItemDao(): PurchaseItemDao
    abstract fun serviceOrderDao(): ServiceOrderDao
    abstract fun serviceOrderItemDao(): ServiceOrderItemDao
    abstract fun downPaymentRecordDao(): DownPaymentRecordDao
    abstract fun stockAdjustmentDao(): StockAdjustmentDao
}
