package com.love.timi.data.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class AuthenticationRes(
    @field:Schema(description = "사용자 ID")
    val userId: Long,

    @field:Schema(description = "메뉴에 대한 권한들")
    val permissions: List<String>,

    @field:Schema(description = "비밀번호 변경 필요 여부")
    val isPwChangeRequired: Boolean
)