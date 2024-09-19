package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Data Transfer Object for Change User Role")
data class ChangeUserRoleReq(
    @field:Schema(description = "사용자 역할 Id값", required = true)
    val roleId: Long?,

    @field:Schema(hidden = true)
    val temp: String?
)
