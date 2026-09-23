package id.yukelola.core.domain.model.business

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BusinessBranchModelTest {

    @Test
    fun `Business initializes with valid properties`() {
        val business = Business(
            id = "biz-123",
            legalName = "PT Sukses Mandiri",
            ownerUserId = "user-owner",
            createdAt = 1711234567000L,
            isActive = true
        )

        assertEquals("biz-123", business.id)
        assertEquals("PT Sukses Mandiri", business.legalName)
        assertEquals("user-owner", business.ownerUserId)
        assertTrue(business.isActive)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Business with blank legalName throws exception`() {
        Business(
            id = "biz-123",
            legalName = "   ",
            ownerUserId = "user-owner",
            createdAt = 1711234567000L
        )
    }

    @Test
    fun `Branch initializes with branch-scoped BusinessProfile and capabilities`() {
        val profile = BusinessProfile(
            name = "Cabang Pasar Baru",
            phone = "081234567890",
            address = "Jl. Pasar No. 12",
            receiptHeader = "Selamat Datang di Cabang Pasar Baru",
            receiptFooter = "Terima Kasih atas Kunjungan Anda"
        )

        val branch = Branch(
            id = "branch-01",
            businessId = "biz-123",
            code = "CAB01",
            name = "Cabang Pasar Baru",
            businessProfile = profile,
            businessModel = BusinessModel.RETAIL_WARUNG,
            enabledCapabilities = setOf(Capability.RETAIL, Capability.INVENTORY, Capability.DIGITAL_SERVICE)
        )

        assertEquals("branch-01", branch.id)
        assertEquals("biz-123", branch.businessId)
        assertEquals(BusinessModel.RETAIL_WARUNG, branch.businessModel)
        assertTrue(branch.hasCapability(Capability.RETAIL))
        assertTrue(branch.hasCapability(Capability.DIGITAL_SERVICE))
        assertFalse(branch.hasCapability(Capability.FUEL))
    }

    @Test
    fun `BusinessModel contains exactly 10 canonical values`() {
        val expectedModels = setOf(
            BusinessModel.RETAIL_WARUNG,
            BusinessModel.DIGITAL_KIOSK,
            BusinessModel.FOOD_BEVERAGE_CAFE,
            BusinessModel.SERVICE_WORKSHOP,
            BusinessModel.LAUNDRY,
            BusinessModel.RETAIL_HEALTH,
            BusinessModel.PERCETAKAN,
            BusinessModel.FOTOCOPY,
            BusinessModel.ATK,
            BusinessModel.GENERAL_STORE
        )

        assertEquals(10, BusinessModel.values().size)
        assertEquals(expectedModels, BusinessModel.values().toSet())
    }

    @Test
    fun `Capability contains exactly 13 canonical values`() {
        val expectedCapabilities = setOf(
            Capability.RETAIL,
            Capability.INVENTORY,
            Capability.PURCHASE,
            Capability.DIGITAL_SERVICE,
            Capability.DIGITAL_DEPOSIT,
            Capability.PPOB,
            Capability.FUEL,
            Capability.FOOD_BEVERAGE,
            Capability.SERVICE,
            Capability.CUSTOMER_DEBT,
            Capability.SUPPLIER_DEBT,
            Capability.CASH,
            Capability.REPORTING
        )

        assertEquals(13, Capability.values().size)
        assertEquals(expectedCapabilities, Capability.values().toSet())
    }
}
