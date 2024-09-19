package com.love.timi.util

object MaskingUtil {

    // 이름 마스킹 메서드
    fun maskName(name: String?): String? {
        return name?.let {
            if (isKoreanName(it)) {
                maskKoreanName(it)
            } else {
                maskEnglishName(it)
            }
        }
    }

    // 한국어 이름인지 확인하는 메서드
    private fun isKoreanName(name: String): Boolean {
        return name.any { it in '가'..'힣' }
    }

    // 한국어 이름 마스킹 메서드
    private fun maskKoreanName(name: String): String {
        return if (name.length > 2) {
            name.first() + "*".repeat(name.length - 2) + name.last()
        } else {
            name.first() + "*".repeat(name.length - 1)
        }
    }

    // 영어 이름 마스킹 메서드
    private fun maskEnglishName(name: String): String {
        return if (name.length > 2) {
            name.first() + "*".repeat(name.length - 2) + name.last()
        } else {
            name.first() + "*".repeat(name.length - 1)
        }
    }

    // 이메일 마스킹 메서드
    fun maskEmail(email: String?): String? {
        return email?.let {
            val parts = it.split("@")
            if (parts.size == 2) {
                val localPart = parts[0]
                val domainPart = parts[1]
                val maskedLocalPart = maskName(localPart) ?: localPart
                "$maskedLocalPart@$domainPart"
            } else it
        }
    }

    // IP 주소 마스킹 메서드
    fun maskIpAddress(ipAddress: String?): String? {
        return ipAddress?.let {
            if (it.contains(":")) {
                // IPv6 주소 마스킹
                val parts = it.split(":")
                if (parts.size == 8) {
                    parts.subList(0, 4).joinToString(":") + ":****:****:****:****"
                } else {
                    it
                }
            } else {
                // IPv4 주소 마스킹
                val parts = it.split(".")
                if (parts.size == 4) {
                    parts.subList(0, 3).joinToString(".") + ".xxx"
                } else {
                    it
                }
            }
        }
    }
}