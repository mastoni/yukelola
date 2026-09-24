package id.yukelola.core.database.mapper

import id.yukelola.core.database.entity.UserEntity
import id.yukelola.core.domain.model.actor.Role
import id.yukelola.core.domain.model.actor.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserMapperTest {

    @Test
    fun `toEntity maps owner User with null branchId losslessly`() {
        val domain = User(
            id = "user-owner-001",
            businessId = "biz-001",
            branchId = null,
            username = "owner_boss",
            fullName = "Pak Bos Besar",
            role = Role.OWNER,
            isActive = true
        )

        val entity = UserMapper.toEntity(domain)

        assertEquals("user-owner-001", entity.id)
        assertEquals("biz-001", entity.businessId)
        assertNull(entity.branchId)
        assertEquals("owner_boss", entity.username)
        assertEquals("Pak Bos Besar", entity.fullName)
        assertEquals("OWNER", entity.role)
        assertTrue(entity.isActive)
    }

    @Test
    fun `toEntity maps cashier User with branchId losslessly`() {
        val domain = User(
            id = "user-kasir-001",
            businessId = "biz-001",
            branchId = "branch-001",
            username = "kasir_siti",
            fullName = "Siti Kasir",
            role = Role.CASHIER,
            isActive = true
        )

        val entity = UserMapper.toEntity(domain)

        assertEquals("user-kasir-001", entity.id)
        assertEquals("biz-001", entity.businessId)
        assertEquals("branch-001", entity.branchId)
        assertEquals("kasir_siti", entity.username)
        assertEquals("Siti Kasir", entity.fullName)
        assertEquals("CASHIER", entity.role)
        assertTrue(entity.isActive)
    }

    @Test
    fun `toDomain maps UserEntity to domain losslessly`() {
        val entity = UserEntity(
            id = "user-mgr-001",
            businessId = "biz-001",
            branchId = "branch-002",
            username = "manager_budi",
            fullName = "Budi Manager",
            role = "MANAGER",
            isActive = false
        )

        val domain = UserMapper.toDomain(entity)

        assertEquals("user-mgr-001", domain.id)
        assertEquals("biz-001", domain.businessId)
        assertEquals("branch-002", domain.branchId)
        assertEquals("manager_budi", domain.username)
        assertEquals("Budi Manager", domain.fullName)
        assertEquals(Role.MANAGER, domain.role)
        assertFalse(domain.isActive)
    }

    @Test
    fun `preserves all 3 canonical Role values`() {
        val roles = Role.values()
        assertEquals(3, roles.size)

        for (role in roles) {
            val domain = User(
                id = "user-${role.name}",
                businessId = "biz-001",
                branchId = "branch-001",
                username = "user_${role.name.lowercase()}",
                fullName = "Full Name ${role.name}",
                role = role,
                isActive = true
            )

            val entity = UserMapper.toEntity(domain)
            assertEquals(role.name, entity.role)

            val roundTrip = UserMapper.toDomain(entity)
            assertEquals(role, roundTrip.role)
        }
    }
}
