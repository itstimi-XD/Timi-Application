package com.love.timi.util

import java.util.regex.Pattern

object CommonUtil {
    fun isEmailValid(email: String): Boolean {
        return Pattern.compile(
            "^([\\w-]+(?:\\.[\\w-]+)*)@"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4}$"
        ).matcher(email).matches()
    }

    fun isIpValid(ip: String): Boolean {
        return Pattern.compile(
            "^((([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]))$"
        ).matcher(ip).matches()
    }

    // 특정 도메인의 이메일인지 확인하는 메소드
    fun isEmailFromDomain(email: String, domain: String): Boolean {
        return Pattern.compile(
                "^([\\w-]+(?:\\.[\\w-]+)*)@"
                        + Pattern.quote(domain) + "$"
        ).matcher(email).matches()
    }

    // 세 개의 도메인 중 하나인지 확인하는 메소드
    fun isEmailFromOneOfDomains(email: String, domains: List<String>): Boolean {
        // 도메인 리스트를 정규식 문자열로 변환 (예: "domain1.com|domain2.com|domain3.com")
        val domainsRegex = domains.joinToString("|") { Pattern.quote(it) }

        return Pattern.compile(
                "^([\\w-]+(?:\\.[\\w-]+)*)@"
                        + "($domainsRegex)$"
        ).matcher(email).matches()
    }
}