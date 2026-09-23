package id.yukelola.core.domain.model.digital

/**
 * Operational status for a read-only digital inquiry check.
 */
enum class InquiryStatus {
    INITIATED,
    SUCCESS,
    FAILED,
    EXPIRED
}
