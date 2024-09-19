package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Data Transfer Object for Login History")
data class LoginHistoryReq(
    @field:Schema(description = "페이지", required = false)
    val page: Int?,

    @field:Schema(description = "이메일", required = false)
    val email: String?,

    @field:Schema(description = "사용자 역할 Id", required = false)
    val roleId: String?,

    @field:Schema(description = "시작일", required = false)
    val startDate: String?,

    @field:Schema(description = "종료일", required = false)
    val endDate: String?,
)
