package com.love.timi.data.dto.response

data class RoleMenuRes(
    val menuId: Long,
    val label: String,
    val number: Int?,
    val children: List<RoleMenuRes>?,
    val permission: PermissionDTO?
)
data class PermissionDTO(
    val menuId: Long,
    val approval: Boolean,
    val create: Boolean,
    val read: Boolean,
    val update: Boolean,
    val delete: Boolean
)