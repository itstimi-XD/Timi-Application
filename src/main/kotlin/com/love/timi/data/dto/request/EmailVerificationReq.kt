package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Schema(description = "Data Transfer Object for Email Verification")
data class EmailVerificationReq(
    @field:Schema(description = "Email of the user", required = true)
    @field:NotBlank
    @field:Email
    val email: String?,

    @field:Schema(description = "인증 코드", required = true)
    @field:NotBlank
    val code: String?
)
