package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull


data class IpUpdateReq(
    @field:Schema(description = "사용자 IP Id")
    @field:NotNull
    val userIpId: Long,

    @field:Schema(description = "사용자 IP")
    @field:NotBlank
    val address: String?,

    @field:Schema(description = "사용자 IP에 대한 설명")
    val desc: String?
)

