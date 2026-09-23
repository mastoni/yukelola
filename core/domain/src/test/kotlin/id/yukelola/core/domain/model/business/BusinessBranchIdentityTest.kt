package id.yukelola.core.domain.model.business

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BusinessBranchIdentityTest {

    @Test
    fun `Business is the top-level owner of multiple operational branches`() {
        val business = Business(
            id = "biz-corp-01",
            legalName = "PT Berkah Jaya Mandiri",
            ownerUserId = "user-owner-01",
            createdAt = 1711234000000L
        )

        val profileA = BusinessProfile(name = "Berkah Jaya - Cabang Pusat")
        val profileB = BusinessProfile(name = "Berkah Jaya - Cabang Stasiun")

        val branchA = Branch(
            id = "branch-01",
            businessId = business.id,
            code = "CAB01",
            name = "Cabang Pusat",
            businessProfile = profileA,
            businessModel = BusinessModel.RETAIL_WARUNG
        )

        val branchB = Branch(
            id = "branch-02",
            businessId = business.id,
            code = "CAB02",
            name = "Cabang Stasiun",
            businessProfile = profileB,
            businessModel = BusinessModel.DIGITAL_KIOSK
        )

        assertEquals("biz-corp-01", branchA.businessId)
        assertEquals("biz-corp-01", branchB.businessId)
        assertNotEquals(branchA.id, branchB.id)
        assertNotEquals(branchA.code, branchB.code)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Branch cannot be created without valid businessId reference`() {
        Branch(
            id = "branch-01",
            businessId = "   ",
            code = "CAB01",
            name = "Branch Tanpa Bisnis",
            businessProfile = BusinessProfile(name = "Profil"),
            businessModel = BusinessModel.RETAIL_WARUNG
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Branch code must not be blank`() {
        Branch(
            id = "branch-01",
            businessId = "biz-01",
            code = "",
            name = "Branch Code Kosong",
            businessProfile = BusinessProfile(name = "Profil"),
            businessModel = BusinessModel.RETAIL_WARUNG
        )
    }
}
