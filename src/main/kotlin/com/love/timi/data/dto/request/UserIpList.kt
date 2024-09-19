package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class UserIpList (
    @field:Schema(description = "사용자 IP Id")
    val userIpId: Long?,
    @field:Schema(description = "사용자 IP")
    val address: String?,
    @field:Schema(description = "사용자 IP에 대한 설명")
    val desc: String?
)