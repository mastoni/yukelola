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

    @Test
    fun `YukelolaDatabase declares productUnitDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "productUnitDao" }
        assertNotNull("YukelolaDatabase must declare abstract productUnitDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.ProductUnitDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements productUnitDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "productUnitDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement productUnitDao method", method)
        assertEquals(id.yukelola.core.database.dao.ProductUnitDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares customerDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "customerDao" }
        assertNotNull("YukelolaDatabase must declare abstract customerDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.CustomerDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements customerDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "customerDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement customerDao method", method)
        assertEquals(id.yukelola.core.database.dao.CustomerDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares supplierDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "supplierDao" }
        assertNotNull("YukelolaDatabase must declare abstract supplierDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.SupplierDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements supplierDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "supplierDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement supplierDao method", method)
        assertEquals(id.yukelola.core.database.dao.SupplierDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares supplierDebtDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "supplierDebtDao" }
        assertNotNull("YukelolaDatabase must declare abstract supplierDebtDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.SupplierDebtDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements supplierDebtDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "supplierDebtDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement supplierDebtDao method", method)
        assertEquals(id.yukelola.core.database.dao.SupplierDebtDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares customerDebtDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "customerDebtDao" }
        assertNotNull("YukelolaDatabase must declare abstract customerDebtDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.CustomerDebtDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements customerDebtDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "customerDebtDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement customerDebtDao method", method)
        assertEquals(id.yukelola.core.database.dao.CustomerDebtDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares debtPaymentDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "debtPaymentDao" }
        assertNotNull("YukelolaDatabase must declare abstract debtPaymentDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.DebtPaymentDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements debtPaymentDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "debtPaymentDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement debtPaymentDao method", method)
        assertEquals(id.yukelola.core.database.dao.DebtPaymentDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares cashRegisterDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "cashRegisterDao" }
        assertNotNull("YukelolaDatabase must declare abstract cashRegisterDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.CashRegisterDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements cashRegisterDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "cashRegisterDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement cashRegisterDao method", method)
        assertEquals(id.yukelola.core.database.dao.CashRegisterDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares cashMutationDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "cashMutationDao" }
        assertNotNull("YukelolaDatabase must declare abstract cashMutationDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.CashMutationDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements cashMutationDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "cashMutationDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement cashMutationDao method", method)
        assertEquals(id.yukelola.core.database.dao.CashMutationDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares digitalDepositAccountDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "digitalDepositAccountDao" }
        assertNotNull("YukelolaDatabase must declare abstract digitalDepositAccountDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.DigitalDepositAccountDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements digitalDepositAccountDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "digitalDepositAccountDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement digitalDepositAccountDao method", method)
        assertEquals(id.yukelola.core.database.dao.DigitalDepositAccountDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares digitalDepositMutationDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "digitalDepositMutationDao" }
        assertNotNull("YukelolaDatabase must declare abstract digitalDepositMutationDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.DigitalDepositMutationDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements digitalDepositMutationDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "digitalDepositMutationDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement digitalDepositMutationDao method", method)
        assertEquals(id.yukelola.core.database.dao.DigitalDepositMutationDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares paymentDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "paymentDao" }
        assertNotNull("YukelolaDatabase must declare abstract paymentDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.PaymentDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements paymentDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "paymentDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement paymentDao method", method)
        assertEquals(id.yukelola.core.database.dao.PaymentDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares saleDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "saleDao" }
        assertNotNull("YukelolaDatabase must declare abstract saleDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.SaleDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements saleDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "saleDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement saleDao method", method)
        assertEquals(id.yukelola.core.database.dao.SaleDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares saleItemDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "saleItemDao" }
        assertNotNull("YukelolaDatabase must declare abstract saleItemDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.SaleItemDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements saleItemDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "saleItemDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement saleItemDao method", method)
        assertEquals(id.yukelola.core.database.dao.SaleItemDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares purchaseDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "purchaseDao" }
        assertNotNull("YukelolaDatabase must declare abstract purchaseDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.PurchaseDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements purchaseDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "purchaseDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement purchaseDao method", method)
        assertEquals(id.yukelola.core.database.dao.PurchaseDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares purchaseItemDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "purchaseItemDao" }
        assertNotNull("YukelolaDatabase must declare abstract purchaseItemDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.PurchaseItemDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements purchaseItemDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "purchaseItemDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement purchaseItemDao method", method)
        assertEquals(id.yukelola.core.database.dao.PurchaseItemDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares serviceOrderDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "serviceOrderDao" }
        assertNotNull("YukelolaDatabase must declare abstract serviceOrderDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.ServiceOrderDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements serviceOrderDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "serviceOrderDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement serviceOrderDao method", method)
        assertEquals(id.yukelola.core.database.dao.ServiceOrderDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares serviceOrderItemDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "serviceOrderItemDao" }
        assertNotNull("YukelolaDatabase must declare abstract serviceOrderItemDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.ServiceOrderItemDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements serviceOrderItemDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "serviceOrderItemDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement serviceOrderItemDao method", method)
        assertEquals(id.yukelola.core.database.dao.ServiceOrderItemDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares downPaymentRecordDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "downPaymentRecordDao" }
        assertNotNull("YukelolaDatabase must declare abstract downPaymentRecordDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.DownPaymentRecordDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements downPaymentRecordDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "downPaymentRecordDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement downPaymentRecordDao method", method)
        assertEquals(id.yukelola.core.database.dao.DownPaymentRecordDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares digitalTransactionDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "digitalTransactionDao" }
        assertNotNull("YukelolaDatabase must declare abstract digitalTransactionDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.DigitalTransactionDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements digitalTransactionDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "digitalTransactionDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement digitalTransactionDao method", method)
        assertEquals(id.yukelola.core.database.dao.DigitalTransactionDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares inquiryDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "inquiryDao" }
        assertNotNull("YukelolaDatabase must declare abstract inquiryDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.InquiryDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements inquiryDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "inquiryDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement inquiryDao method", method)
        assertEquals(id.yukelola.core.database.dao.InquiryDao::class.java, method!!.returnType)
    }

    @Test
    fun `YukelolaDatabase declares digitalTransactionComplaintDao getter`() {
        val method = YukelolaDatabase::class.java.methods.firstOrNull { it.name == "digitalTransactionComplaintDao" }
        assertNotNull("YukelolaDatabase must declare abstract digitalTransactionComplaintDao method", method)
        assertEquals(0, method!!.parameterTypes.size)
        assertEquals(id.yukelola.core.database.dao.DigitalTransactionComplaintDao::class.java, method.returnType)
    }

    @Test
    fun `YukelolaDatabase_Impl implements digitalTransactionComplaintDao`() {
        val implClass = Class.forName("id.yukelola.core.database.YukelolaDatabase_Impl")
        val method = implClass.methods.firstOrNull { it.name == "digitalTransactionComplaintDao" }
        assertNotNull("Generated YukelolaDatabase_Impl must implement digitalTransactionComplaintDao method", method)
        assertEquals(id.yukelola.core.database.dao.DigitalTransactionComplaintDao::class.java, method!!.returnType)
    }
}
