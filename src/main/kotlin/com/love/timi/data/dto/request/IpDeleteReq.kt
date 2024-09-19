package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class IpDeleteReq(

    @field:Schema(description = "사용자 IP Id")
    @field:NotNull
    val userIpId: Long

)
