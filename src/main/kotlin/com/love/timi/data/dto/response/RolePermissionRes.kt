package com.love.timi.data.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Data Transfer Object for Role Permission Response")
data class RolePermissionResponseDTO(
    @field:Schema(description = "사용자 역할(권한) Id")
    val roleId: Long,

    @field:Schema(description = "사용자 역할(권한)명")
    val roleName: String,

    @field:Schema(description = "메뉴에 대한 권한들")
    val permissionList: List<PermissionListDTO>,

    @field:Schema(description = "해당 역할 보유 인원")
    val userCount: Int
)

data class PermissionListDTO(
    val menu: String,
    val permissions: List<String>
)
