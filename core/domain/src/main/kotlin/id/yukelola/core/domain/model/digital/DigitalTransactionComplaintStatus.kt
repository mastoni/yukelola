package id.yukelola.core.domain.model.digital

/**
 * Operational lifecycle status for a digital transaction issue report / complaint.
 */
enum class DigitalTransactionComplaintStatus {
    OPEN,
    INVESTIGATING,
    RESOLVED,
    REJECTED
}
