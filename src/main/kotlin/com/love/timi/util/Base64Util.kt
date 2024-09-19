package com.love.timi.util

object Base64Util {

    /**
     * Base64 인코딩
     * @param str 인코딩 할 문자열
     * @return 인코딩 된 문자열
     */
    fun encodeBase64(str: String): String {
        return java.util.Base64.getEncoder().encodeToString(str.toByteArray())
    }

    /**
     * Base64 디코딩
     * @param str 디코딩 할 문자열
     * @return 디코딩 된 문자열
     */
    fun decodeBase64(str: String): String {
        return String(java.util.Base64.getDecoder().decode(str))
    }
}