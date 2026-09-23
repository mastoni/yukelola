package id.yukelola.core.domain.model.business

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BusinessModelCapabilityScopeTest {

    @Test
    fun `BusinessModel is branch-scoped and Business can operate heterogeneous branches`() {
        val bizId = "biz-multi-tenant"

        val warungBranch = Branch(
            id = "br-warung",
            businessId = bizId,
            code = "WR01",
            name = "Warung Kelontong",
            businessProfile = BusinessProfile(name = "Warung Berkah"),
            businessModel = BusinessModel.RETAIL_WARUNG,
            enabledCapabilities = setOf(Capability.RETAIL, Capability.INVENTORY, Capability.FUEL)
        )

        val konterBranch = Branch(
            id = "br-konter",
            businessId = bizId,
            code = "KT01",
            name = "Konter Pulsa",
            businessProfile = BusinessProfile(name = "Konter Berkah"),
            businessModel = BusinessModel.DIGITAL_KIOSK,
            enabledCapabilities = setOf(Capability.DIGITAL_SERVICE, Capability.DIGITAL_DEPOSIT, Capability.RETAIL)
        )

        val bengkelBranch = Branch(
            id = "br-bengkel",
            businessId = bizId,
            code = "BK01",
            name = "Bengkel Motor",
            businessProfile = BusinessProfile(name = "Bengkel Berkah"),
            businessModel = BusinessModel.SERVICE_WORKSHOP,
            enabledCapabilities = setOf(Capability.SERVICE, Capability.RETAIL, Capability.INVENTORY)
        )

        assertEquals(BusinessModel.RETAIL_WARUNG, warungBranch.businessModel)
        assertEquals(BusinessModel.DIGITAL_KIOSK, konterBranch.businessModel)
        assertEquals(BusinessModel.SERVICE_WORKSHOP, bengkelBranch.businessModel)

        assertTrue(warungBranch.hasCapability(Capability.FUEL))
        assertFalse(konterBranch.hasCapability(Capability.FUEL))
        assertTrue(konterBranch.hasCapability(Capability.DIGITAL_DEPOSIT))
        assertFalse(bengkelBranch.hasCapability(Capability.DIGITAL_DEPOSIT))
    }

    @Test
    fun `Capabilities can be dynamically extended without altering BusinessModel`() {
        val baseBranch = Branch(
            id = "br-01",
            businessId = "biz-01",
            code = "WR01",
            name = "Warung Biasa",
            businessProfile = BusinessProfile(name = "Warung Biasa"),
            businessModel = BusinessModel.RETAIL_WARUNG,
            enabledCapabilities = setOf(Capability.RETAIL, Capability.INVENTORY)
        )

        // Adding PPOB capability does NOT transform BusinessModel to DIGITAL_KIOSK
        val updatedBranch = baseBranch.withCapability(Capability.DIGITAL_SERVICE)

        assertEquals(BusinessModel.RETAIL_WARUNG, updatedBranch.businessModel)
        assertTrue(updatedBranch.hasCapability(Capability.DIGITAL_SERVICE))
        assertTrue(updatedBranch.hasCapability(Capability.RETAIL))
    }
}
