package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Schema(description = "Data Transfer Object for Password Reset with OTP")
data class PwResetWithOtpReq(

        @field:Schema(description = "이메일", required = true)
        @field:NotBlank
        @field:Email
        val email: String?,

        @field:Schema(description = "비밀번호", required = true)
        @field:NotBlank
        val password: String?,

        @field:Schema(description = "인증 코드", required = true)
        @field:NotBlank
        val code: String?
)
