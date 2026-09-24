package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.UserEntity
import id.yukelola.core.domain.model.actor.Role
import id.yukelola.core.domain.model.actor.User

/**
 * Lossless bidirectional mapper between [User] domain aggregate and [UserEntity].
 * Storage only: contains no credential hashing, authentication, or authorization rules.
 */
object UserMapper {

    fun toEntity(domain: User): UserEntity {
        return UserEntity(
            id = domain.id,
            businessId = domain.businessId,
            branchId = domain.branchId,
            username = domain.username,
            fullName = domain.fullName,
            role = domain.role.name,
            isActive = domain.isActive
        )
    }

    fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            businessId = entity.businessId,
            branchId = entity.branchId,
            username = entity.username,
            fullName = entity.fullName,
            role = Role.valueOf(entity.role),
            isActive = entity.isActive
        )
    }
}
