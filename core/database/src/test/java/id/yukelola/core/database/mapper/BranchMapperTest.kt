package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.BranchEntity
import id.yukelola.core.domain.model.business.Branch
import id.yukelola.core.domain.model.business.BusinessModel
import id.yukelola.core.domain.model.business.BusinessProfile
import id.yukelola.core.domain.model.business.Capability
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BranchMapperTest {

    @Test
    fun `domain to entity preserves all canonical fields`() {
        val domain = Branch(
            id = "branch-001",
            businessId = "biz-001",
            code = "PST",
            name = "Pusat Jakarta",
            businessProfile = BusinessProfile(
                name = "Yukelola Store Pusat",
                phone = "08123456789",
                address = "Jl. Sudirman No. 1",
                receiptHeader = "Selamat Datang di Yukelola Pusat",
                receiptFooter = "Terima kasih atas kunjungan Anda",
                currency = "IDR",
                timezone = "Asia/Jakarta",
                allowNegativeStock = false
            ),
            businessModel = BusinessModel.RETAIL_WARUNG,
            enabledCapabilities = setOf(Capability.RETAIL, Capability.INVENTORY, Capability.CASH),
            isActive = true
        )

        val entity = domain.toEntity()

        assertEquals("branch-001", entity.id)
        assertEquals("biz-001", entity.businessId)
        assertEquals("PST", entity.code)
        assertEquals("Pusat Jakarta", entity.name)
        assertEquals("Yukelola Store Pusat", entity.profileName)
        assertEquals("08123456789", entity.profilePhone)
        assertEquals("Jl. Sudirman No. 1", entity.profileAddress)
        assertEquals("Selamat Datang di Yukelola Pusat", entity.receiptHeader)
        assertEquals("Terima kasih atas kunjungan Anda", entity.receiptFooter)
        assertEquals("IDR", entity.currency)
        assertEquals("Asia/Jakarta", entity.timezone)
        assertFalse(entity.allowNegativeStock)
        assertEquals("RETAIL_WARUNG", entity.businessModel)
        assertTrue(entity.enabledCapabilities.contains("RETAIL"))
        assertTrue(entity.enabledCapabilities.contains("INVENTORY"))
        assertTrue(entity.enabledCapabilities.contains("CASH"))
        assertTrue(entity.isActive)
    }

    @Test
    fun `entity to domain preserves all canonical fields`() {
        val entity = BranchEntity(
            id = "branch-002",
            businessId = "biz-002",
            code = "BDG",
            name = "Cabang Bandung",
            profileName = "Yukelola Kiosk Bandung",
            profilePhone = null,
            profileAddress = null,
            receiptHeader = null,
            receiptFooter = null,
            currency = "IDR",
            timezone = "Asia/Jakarta",
            allowNegativeStock = true,
            businessModel = "DIGITAL_KIOSK",
            enabledCapabilities = "DIGITAL_SERVICE,DIGITAL_DEPOSIT,PPOB",
            isActive = false
        )

        val domain = entity.toDomain()

        assertEquals("branch-002", domain.id)
        assertEquals("biz-002", domain.businessId)
        assertEquals("BDG", domain.code)
        assertEquals("Cabang Bandung", domain.name)
        assertEquals("Yukelola Kiosk Bandung", domain.businessProfile.name)
        assertEquals(null, domain.businessProfile.phone)
        assertEquals(null, domain.businessProfile.address)
        assertEquals(null, domain.businessProfile.receiptHeader)
        assertEquals(null, domain.businessProfile.receiptFooter)
        assertEquals("IDR", domain.businessProfile.currency)
        assertEquals("Asia/Jakarta", domain.businessProfile.timezone)
        assertTrue(domain.businessProfile.allowNegativeStock)
        assertEquals(BusinessModel.DIGITAL_KIOSK, domain.businessModel)
        assertEquals(
            setOf(Capability.DIGITAL_SERVICE, Capability.DIGITAL_DEPOSIT, Capability.PPOB),
            domain.enabledCapabilities
        )
        assertFalse(domain.isActive)
    }

    @Test
    fun `roundtrip domain to entity to domain produces identical aggregate`() {
        val original = Branch(
            id = "branch-003",
            businessId = "biz-003",
            code = "SBY",
            name = "Cabang Surabaya",
            businessProfile = BusinessProfile(
                name = "Yukelola Workshop Surabaya",
                phone = "08198765432",
                address = "Jl. Pemuda No. 10",
                receiptHeader = "Bengkel Resmi Yukelola",
                receiptFooter = "Garansi Service 14 Hari",
                currency = "IDR",
                timezone = "Asia/Jakarta",
                allowNegativeStock = false
            ),
            businessModel = BusinessModel.SERVICE_WORKSHOP,
            enabledCapabilities = setOf(Capability.SERVICE, Capability.INVENTORY, Capability.CUSTOMER_DEBT),
            isActive = true
        )

        val roundtrip = original.toEntity().toDomain()

        assertEquals(original, roundtrip)
    }

    @Test
    fun `entity with empty capabilities maps to empty set`() {
        val entity = BranchEntity(
            id = "branch-004",
            businessId = "biz-004",
            code = "MLG",
            name = "Cabang Malang",
            profileName = "Yukelola Malang",
            businessModel = "GENERAL_STORE",
            enabledCapabilities = "",
            isActive = true
        )

        val domain = entity.toDomain()

        assertTrue(domain.enabledCapabilities.isEmpty())
    }
}
