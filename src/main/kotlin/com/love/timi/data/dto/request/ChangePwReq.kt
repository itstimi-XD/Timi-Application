package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Data Transfer Object for Changing Password Request")
data class ChangePwReq(
        @field:Schema(description = "기존 비밀번호", required = true)
        @field:NotBlank
        val oldPassword: String?,

        @field:Schema(description = "변경될 비밀번호", required = true)
        @field:NotBlank
        val newPassword: String?
)
