package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

@Schema(description = "Data Transfer Object for Permission Per Menu")
data class PermissionPerMenuReq(

    @field:NotBlank
    @field:Schema(description = "역할 Id", required = true)
    val roleId: String?,

    @field:Valid
    @field:Schema(description = "메뉴 권한 목록", required = true)
    val menuPermissionList: List<MenuPermission>?

)

data class MenuPermission(
    @field:NotBlank
    @field:Schema(description = "메뉴 Id", required = false)
    val menuId: String?,

    @field:Schema(description = "승인 권한 여부", required = false)
    val approval: Boolean?,

    @field:Schema(description = "생성 권한 여부", required = false)
    val create: Boolean?,

    @field:Schema(description = "조회 권한 여부", required = false)
    val read: Boolean?,

    @field:Schema(description = "수정 권한 여부", required = false)
    val update: Boolean?,

    @field:Schema(description = "삭제 권한 여부", required = false)
    val delete: Boolean?,
)