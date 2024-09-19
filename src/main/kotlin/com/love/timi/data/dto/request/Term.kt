package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull


data class Term(

    @field:Schema(description = "약관 Id(종류를 나타냄, 기본 약관의 경우 1)", required = true)
    @field:NotNull
    val termId: Long,

    @field:Schema(description = "약관 버전", required = true)
    @field:NotBlank
    val version: String,

    @field:Schema(description = "약관 동의 여부", required = true)
    @field:NotBlank
    val agreeYn: String,
)
