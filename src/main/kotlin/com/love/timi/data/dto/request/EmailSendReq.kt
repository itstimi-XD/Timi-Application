package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Schema(description = "Data Transfer Object for Email Send")
data class EmailSendReq(

    @field:NotBlank
    @field:Email
    @field:Schema(description = "이메일", required = true)
    val email: String?,

    @field:Schema(hidden = true)
    val temp: String?

)
