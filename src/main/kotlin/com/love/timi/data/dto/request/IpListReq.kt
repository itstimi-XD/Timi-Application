package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Data Transfer Object for IP restrict List")
data class IpListReq(

    @field:Schema(description = "페이지", required = false)
    val page: Int?,

    @field:Schema(description = "이메일", required = false)
    val email: String?,

    @field:Schema(description = "사용자 이름", required = false)
    val name: String?,

    @field:Schema(description = "IP 주소", required = false)
    val ip: String?
)
