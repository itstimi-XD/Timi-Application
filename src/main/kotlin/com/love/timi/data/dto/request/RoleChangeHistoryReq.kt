package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email

@Schema(description = "Data Transfer Object for Role Change History")
data class RoleChangeHistoryReq(
    @field:Schema(description = "페이지", required = false)
    val page: Int?,

    @field:Schema(description = "이메일", required = false)
    @field:Email
    val email: String?,

    @field:Schema(description = "사용자 이름", required = false)
    val name: String?,

    @field:Schema(description = "변경된 역할 Id", required = false)
    val roleId: String?,

    @field:Schema(description = "시작일", required = false)
    val startDate: String?,

    @field:Schema(description = "종료일", required = false)
    val endDate: String?
)
