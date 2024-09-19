package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Data Transfer Object for Password Verification")
data class PwVerificationReq(

    @field:NotBlank
    @field:Schema(description = "이메일", required = true)
    val email: String?,

    @field:NotBlank
    @field:Schema(description = "비밀번호", required = true)
    val password: String?
)
