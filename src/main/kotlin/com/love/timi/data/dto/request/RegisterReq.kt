package com.love.timi.data.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "Data Transfer Object for Registration Request")
data class RegisterReq(
    @field:Schema(description = "Email of the user", required = true)
    @field:NotBlank
    @field:Email
    val email: String?,

    @field:Schema(description = "Password of the user", required = true)
    @field:NotBlank
    val password: String?,

    @field:Schema(description = "사용자 이름", required = true)
    @field:NotBlank
    val name: String?,

    @field:Schema(description = "회사명", required = true)
    @field:NotBlank
    val company: String?,

    @field:Schema(description = "팀명", required = true)
    @field:NotBlank
    val team: String?,

    @field:Schema(description = "Role ID of the user", required = true)
    @field:NotNull
    val role: Long?,

    @field:Schema(description = "Terms accepted by the user", required = true)
    @field:Valid
    val terms: List<Term>,

    @field:Schema(description = "IP of the user", required = false)
    @field:Valid
    val ip: List<NewIP>?
)

