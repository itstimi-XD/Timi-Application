package com.love.timi.util

import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest

object ShaUtil {

    /**
     * SHA-256 암호화
     * @param str 암호화 할 문자열
     * @return 암호화 된 문자열
     */
    fun encodeSHA256(str: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(str.toByteArray(Charset.forName("UTF-8")))
        return String.format("%064x", BigInteger(1, digest.digest()))
    }
}