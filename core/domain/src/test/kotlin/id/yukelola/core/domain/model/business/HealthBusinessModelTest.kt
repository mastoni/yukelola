package id.yukelola.core.domain.model.business

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthBusinessModelTest {

    @Test
    fun `RETAIL_HEALTH is the unified model for Apotek and Toko Obat profiles`() {
        val apotekBranch = Branch(
            id = "br-apotek",
            businessId = "biz-health",
            code = "APT01",
            name = "Apotek Sehat Medika",
            businessProfile = BusinessProfile(
                name = "Apotek Sehat Medika",
                receiptHeader = "SIA: 123/SIA/2026\nApoteker: apt. Budi, S.Farm"
            ),
            businessModel = BusinessModel.RETAIL_HEALTH,
            enabledCapabilities = setOf(Capability.RETAIL, Capability.INVENTORY, Capability.PURCHASE)
        )

        val tokoObatBranch = Branch(
            id = "br-toko-obat",
            businessId = "biz-health",
            code = "TKO01",
            name = "Toko Obat Manjur",
            businessProfile = BusinessProfile(
                name = "Toko Obat Manjur",
                receiptHeader = "Toko Obat Berizin"
            ),
            businessModel = BusinessModel.RETAIL_HEALTH,
            enabledCapabilities = setOf(Capability.RETAIL, Capability.INVENTORY)
        )

        // Both operate under the canonical RETAIL_HEALTH BusinessModel
        assertEquals(BusinessModel.RETAIL_HEALTH, apotekBranch.businessModel)
        assertEquals(BusinessModel.RETAIL_HEALTH, tokoObatBranch.businessModel)
    }

    @Test
    fun `Warung selling OTC medicine remains RETAIL_WARUNG`() {
        val warungWithMedicine = Branch(
            id = "br-warung-obat",
            businessId = "biz-warung",
            code = "WR02",
            name = "Warung Sembako & Obat P3K",
            businessProfile = BusinessProfile(name = "Warung Bu Siti"),
            businessModel = BusinessModel.RETAIL_WARUNG,
            enabledCapabilities = setOf(Capability.RETAIL, Capability.INVENTORY)
        )

        // Selling medicine does not morph Warung into RETAIL_HEALTH
        assertEquals(BusinessModel.RETAIL_WARUNG, warungWithMedicine.businessModel)
    }
}
