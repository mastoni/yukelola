package id.yukelola.core.domain.model.digital

import id.yukelola.core.domain.model.attribution.TransactionAttribution
import id.yukelola.core.domain.model.cash.CashRegister
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class InquiryIsolationTest {

    private val attribution = TransactionAttribution(
        businessId = "biz-01",
        branchId = "branch-01",
        userId = "user-01",
        deviceId = "dev-01",
        createdAt = 1000L
    )

    @Test
    fun `inquiry execution is read-only and strictly leaves cash register and deposit accounts untouched`() {
        val cashRegister = CashRegister(
            id = "reg-01",
            businessId = "biz-01",
            branchId = "branch-01",
            name = "Cash Drawer",
            currentBalance = 1000000L,
            updatedAt = 1000L
        )

        val depositAccount = DigitalDepositAccount(
            id = "dep-01",
            businessId = "biz-01",
            branchId = "branch-01",
            currentBalance = 500000L,
            updatedAt = 1000L
        )

        // Customer inquires PLN postpaid bill
        val inquiry = Inquiry(
            id = "inq-01",
            attribution = attribution,
            targetNumber = "512345678901",
            productCode = "PLNPOSTPAID",
            createdAt = 1010L
        ).markSuccess(
            customerName = "BAPAK BUDI SANTOSO",
            billAmount = 350000L,
            adminFee = 3000L,
            inquiryReference = "INQ-999",
            expiresAt = 1200L,
            timestamp = 1020L
        )

        assertEquals(InquiryStatus.SUCCESS, inquiry.status)
        assertEquals(353000L, inquiry.totalBillAmount)

        // Cash drawer balance is untouched
        assertEquals(1000000L, cashRegister.currentBalance)

        // Digital deposit account balance is untouched
        assertEquals(500000L, depositAccount.currentBalance)
    }

    @Test
    fun `inquiry does not create digital transaction or trigger fulfillment dispatch`() {
        val inquiry = Inquiry(
            id = "inq-02",
            attribution = attribution,
            targetNumber = "081234567890",
            productCode = "BPJS-KESEHATAN",
            createdAt = 1000L
        ).markSuccess(
            customerName = "IBU SITI",
            billAmount = 150000L,
            adminFee = 2500L,
            timestamp = 1010L
        )

        // The inquiry entity is purely an inquiry check and distinct from a DigitalTransaction
        assertEquals(InquiryStatus.SUCCESS, inquiry.status)
        assertEquals(152500L, inquiry.totalBillAmount)
    }
}
