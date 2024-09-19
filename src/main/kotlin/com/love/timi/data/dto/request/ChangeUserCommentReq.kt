package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class ChangeUserCommentReq(

    @field:Schema(description = "사용자 비고", required = true)
    val comment: String?,

    @field:Schema(hidden = true)
    val temp: String?
)
