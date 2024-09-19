package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class NewIP (
    @field:Schema(description = "IP of the user", required = true)
    val address: String?,

    @field:Schema(description = "IP description of the user", required = false)
    val desc: String?
)
