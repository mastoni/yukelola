package id.yukelola.core.domain.model.digital

/**
 * Controlled categorical reasons for opening a digital transaction complaint.
 */
enum class DigitalTransactionComplaintReason {
    PRODUCT_NOT_RECEIVED,
    WRONG_RESULT,
    TRANSACTION_STUCK,
    DESTINATION_PROBLEM,
    PROVIDER_RESULT_MISMATCH,
    CUSTOMER_DISPUTE,
    OTHER
}
