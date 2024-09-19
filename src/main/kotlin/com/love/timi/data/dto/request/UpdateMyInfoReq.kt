package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class UpdateMyInfoReq(

    @field:Schema(description = "사용자 이름", required = false)
    val name: String?,

    @field:Schema(description = "회사명", required = false)
    val company: String?,

    @field:Schema(description = "팀명", required = false)
    val team: String?,

    @field:Schema(description = "사용자 IP 목록", required = false)
    val userIpList: List<UserIpList>
)
