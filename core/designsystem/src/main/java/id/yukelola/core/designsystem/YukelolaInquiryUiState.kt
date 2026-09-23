package id.yukelola.core.designsystem

/**
 * Representation of Inquiry Visual Contract for UI rendering.
 * Strictly encapsulates the read-only states prior to payment confirmation.
 */
sealed interface YukelolaInquiryUiState {
    /** Input state awaiting user number or scan */
    object Idle : YukelolaInquiryUiState

    /** Checking provider/network with progress indicator */
    object Checking : YukelolaInquiryUiState

    /**
     * Successful Inquiry result displaying validated account info.
     * Note: This is read-only validation and does NOT represent a completed sale.
     */
    data class Success(
        val targetNumber: String,
        val subscriberName: String,
        val segmentOrPower: String? = null,
        val billPeriod: String? = null,
        val billAmount: Long? = null,
        val adminFee: Long = 0L,
        val totalAmount: Long = (billAmount ?: 0L) + adminFee
    ) : YukelolaInquiryUiState

    /** Inquiry failed or validation rejected */
    data class Error(
        val errorMessage: String,
        val retryable: Boolean = true
    ) : YukelolaInquiryUiState
}
