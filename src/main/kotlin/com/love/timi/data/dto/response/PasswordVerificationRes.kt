package com.love.timi.data.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "PasswordVerificationResponseDTO", description = "로그인 응답 DTO")
data class PasswordVerificationRes(

    @Schema(name = "twoFactorSecret", description = "암호화된 2FA Secret Key")
    val twoFactorSecret: String,

    @Schema(name = "twoFactorAuth", description = "2FA 활성화 여부")
    val twoFactorAuth: String,

    @Schema(name = "qrCodeUrl", description = "QR Code URL")
    val qrCodeUrl: String?,

    @Schema(name = "secretKey", description = "암호화되지 않은 Secret Key")
    val secretKey: String?,
)
