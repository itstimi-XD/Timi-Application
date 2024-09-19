package com.love.timi.data.dto.response

import com.love.timi.data.dto.request.UserIpList
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email

data class MyInfoRes(

    @field:Schema(description = "이메일")
    @field:Email(message = "유효한 이메일 주소를 입력해주세요.")
    val email: String?,

    @field:Schema(description = "사용자 이름")
    val name: String?,

    @field:Schema(description = "사용자 회사명")
    val company: String?,

    @field:Schema(description = "사용자 팀명")
    val team: String?,

    @field:Schema(description = "사용자 역할(권한) Id")
    val roleId: Long,

    @field:Schema(description = "사용자 역할(권한)명")
    val roleName: String,

    @field:Schema(description = "사용자 IP 목록")
    val userIpList: List<UserIpList>?
)