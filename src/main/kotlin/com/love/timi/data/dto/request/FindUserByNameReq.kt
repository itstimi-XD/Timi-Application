package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class FindUserByNameReq(

    @field:Schema(description = "검색 대상자 이름", required = true)
    val name: String?,

    @field:Schema(hidden = true)
    val temp: String?

)