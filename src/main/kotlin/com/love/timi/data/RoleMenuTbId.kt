package com.love.timi.data
import jakarta.persistence.IdClass
import java.io.Serializable

@IdClass(RoleMenuTbId::class)
class RoleMenuTbId : Serializable {
    var roleId: Long? = null
    var menuId: Long? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as RoleMenuTbId

        if (roleId != that.roleId) return false
        return menuId == that.menuId
    }

    override fun hashCode(): Int {
        var result = roleId?.hashCode() ?: 0
        result = 31 * result + (menuId?.hashCode() ?: 0)
        return result
    }
}