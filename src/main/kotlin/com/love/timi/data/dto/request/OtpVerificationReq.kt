package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Data Transfer Object for OTP Verification")
data class OtpVerificationReq(
    @field:Schema(description = "2차인증 시크릿 키", required = true)
    @field:NotBlank
    val twoFactorSecret: String?,

    @field:Schema(description = "인증 코드", required = true)
    @field:NotBlank
    val code: String?
)
