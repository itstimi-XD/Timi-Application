package com.love.timi.data.dto.response

import com.google.gson.annotations.Expose
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Data Transfer Object for User IP Response")
data class UserIpRes(
    @Expose
    @field:Schema(description = "IP of the user")
    val ip: String
)