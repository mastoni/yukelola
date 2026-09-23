package id.yukelola.core.domain.model.business

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BusinessProfileScopeTest {

    @Test
    fun `BusinessProfile is scoped to Branch and distinct per location`() {
        val profileUrban = BusinessProfile(
            name = "Apotek Sehat - Cabang Mall",
            phone = "021-5551234",
            address = "Mall Grand Indonesia Lt. 2",
            receiptHeader = "Apotek Sehat Mall\nNPWP: 01.234.567.8-012.000",
            receiptFooter = "Semoga Lekas Sembuh"
        )

        val profileSuburban = BusinessProfile(
            name = "Apotek Sehat - Cabang Depok",
            phone = "021-7775678",
            address = "Jl. Margonda Raya No. 45",
            receiptHeader = "Apotek Sehat Depok",
            receiptFooter = "Layanan 24 Jam"
        )

        val branchUrban = Branch(
            id = "br-mall",
            businessId = "biz-apotek",
            code = "AP01",
            name = "Cabang Mall",
            businessProfile = profileUrban,
            businessModel = BusinessModel.RETAIL_HEALTH
        )

        val branchSuburban = Branch(
            id = "br-depok",
            businessId = "biz-apotek",
            code = "AP02",
            name = "Cabang Depok",
            businessProfile = profileSuburban,
            businessModel = BusinessModel.RETAIL_HEALTH
        )

        assertEquals("Apotek Sehat - Cabang Mall", branchUrban.businessProfile.name)
        assertEquals("Apotek Sehat - Cabang Depok", branchSuburban.businessProfile.name)
        assertNotEquals(branchUrban.businessProfile.receiptHeader, branchSuburban.businessProfile.receiptHeader)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `BusinessProfile with blank name throws exception`() {
        BusinessProfile(name = "   ")
    }
}
