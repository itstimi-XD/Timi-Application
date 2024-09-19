package com.love.timi.data.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Data Transfer Object for Page List Response")
data class PageListRes<T>(
    @field:Schema(description = "전체 페이지 수")
    val totalPages: Int?,

    @field:Schema(description = "현재 페이지")
    val currentPage: Int?,

    @field:Schema(description = "Page size")
    val pageSize: Int?,

    @field:Schema(description = "Total elements")
    val totalElements: Long?,

    @field:Schema(description = "Data")
    val list: List<T>
)