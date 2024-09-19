package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class IpAddReq(
    @field:Schema(description = "User ID", required = true)
    @field:NotNull
    val userId: Long,

    @field:Schema(description = "IP of the user", required = true)
    @field:NotBlank
    val address: String,

    @field:Schema(description = "IP description of the user", required = false)
    val desc: String?
)