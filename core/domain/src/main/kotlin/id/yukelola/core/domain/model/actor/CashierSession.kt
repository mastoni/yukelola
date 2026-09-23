package id.yukelola.core.domain.model.actor

/**
 * Active cash drawer shift session binding an operator (User), a terminal (Device), and a Branch.
 */
data class CashierSession(
    val id: String,
    val branchId: String,
    val userId: String,
    val deviceId: String,
    val openingBalance: Long,
    val closingBalance: Long? = null,
    val openedAt: Long,
    val closedAt: Long? = null,
    val status: SessionStatus = SessionStatus.OPEN
) {
    init {
        require(id.isNotBlank()) { "CashierSession id must not be blank" }
        require(branchId.isNotBlank()) { "CashierSession branchId must not be blank" }
        require(userId.isNotBlank()) { "CashierSession userId must not be blank" }
        require(deviceId.isNotBlank()) { "CashierSession deviceId must not be blank" }
        require(openingBalance >= 0L) { "openingBalance must not be negative" }
        require(openedAt > 0L) { "openedAt timestamp must be positive" }
        closingBalance?.let { require(it >= 0L) { "closingBalance must not be negative" } }
        closedAt?.let { require(it >= openedAt) { "closedAt must not be earlier than openedAt" } }
    }

    /**
     * Whether the shift session is currently active and open for transactions.
     */
    val isOpen: Boolean
        get() = status == SessionStatus.OPEN
}
