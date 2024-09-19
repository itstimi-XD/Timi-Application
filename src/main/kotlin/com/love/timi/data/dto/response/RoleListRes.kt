package com.love.timi.data.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class RoleListRes(
    @field:Schema(description = "Data")
    val list: List<RolePermissionResponseDTO>
)
