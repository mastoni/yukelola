package id.yukelola.core.domain.model.actor

/**
 * Authority tier of a system user.
 */
enum class Role {
    /**
     * Business-wide authority across all branches, global catalog, and subscription management.
     */
    OWNER,

    /**
     * Branch-scoped authority for local stock adjustments, supplier purchasing, and shift reviews.
     */
    MANAGER,

    /**
     * Operational shift authority for POS checkout, receipt issuing, and cash drawer management.
     */
    CASHIER
}
