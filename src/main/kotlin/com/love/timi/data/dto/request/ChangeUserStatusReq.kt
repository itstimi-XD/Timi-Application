package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Data Transfer Object for Change User Status")
data class ChangeUserStatusReq(
    @field:Schema(description = "사용자 계정 상태. 회원가입 승인시 active, 그 외에는 원하는 상태값", required = true)
    @field:NotBlank
    val status: String?,

    @field:Schema(hidden = true)
    val temp: String?
)
