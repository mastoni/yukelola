package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.BranchEntity
import id.yukelola.core.domain.model.business.Branch
import id.yukelola.core.domain.model.business.BusinessModel
import id.yukelola.core.domain.model.business.BusinessProfile
import id.yukelola.core.domain.model.business.Capability

/**
 * Persistence mapper converting between [Branch] domain aggregate and [BranchEntity] Room entity.
 * Backed by canonical Contract v1.3.0 Section 3.
 */
fun Branch.toEntity(): BranchEntity = BranchEntity(
    id = id,
    businessId = businessId,
    code = code,
    name = name,
    profileName = businessProfile.name,
    profilePhone = businessProfile.phone,
    profileAddress = businessProfile.address,
    receiptHeader = businessProfile.receiptHeader,
    receiptFooter = businessProfile.receiptFooter,
    currency = businessProfile.currency,
    timezone = businessProfile.timezone,
    allowNegativeStock = businessProfile.allowNegativeStock,
    businessModel = businessModel.name,
    enabledCapabilities = enabledCapabilities.joinToString(",") { it.name },
    isActive = isActive
)

fun BranchEntity.toDomain(): Branch {
    val capabilities = if (enabledCapabilities.isBlank()) {
        emptySet()
    } else {
        enabledCapabilities.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { Capability.valueOf(it) }
            .toSet()
    }

    return Branch(
        id = id,
        businessId = businessId,
        code = code,
        name = name,
        businessProfile = BusinessProfile(
            name = profileName,
            phone = profilePhone,
            address = profileAddress,
            receiptHeader = receiptHeader,
            receiptFooter = receiptFooter,
            currency = currency,
            timezone = timezone,
            allowNegativeStock = allowNegativeStock
        ),
        businessModel = BusinessModel.valueOf(businessModel),
        enabledCapabilities = capabilities,
        isActive = isActive
    )
}
