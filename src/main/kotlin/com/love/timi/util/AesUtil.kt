/**
 * FILENAME - util/AesUtil.kt
 * CREATE - 2023-05-01 / 이승규
 * AES 암호화 유틸리티
 */
package com.love.timi.util

import com.love.timi.exception.ErrorMessage
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AesUtil {
    private const val AESKEY256 = "J*%V*q*6DSeArc_(&H=&<^cxzP%/b@ZB" // 32글자 문자열

    /**
     * Encrypts a string using AES encryption algorithm.
     *
     * @param str the string to be encrypted
     * @return the encrypted string
     * @throws ErrorMessage.CHIPER_ERROR.exception if an error occurs during encryption
     */
    fun encodeAES(str: String, iv: String): String {
        return try {
            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            val ivParameterSpec = IvParameterSpec(iv.toByteArray())
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(AESKEY256.toByteArray(StandardCharsets.UTF_8), "AES"), ivParameterSpec)

            Base64.getEncoder().encodeToString(cipher.doFinal(padToMultiple(16, str.toByteArray(Charsets.UTF_8))))
        } catch (e: Exception) {
            e.printStackTrace()
            throw ErrorMessage.UNKNOWN_ERROR.exception
        }
    }

    /**
     * Decodes a given AES encrypted string using the AES/CBC/PKCS5Padding algorithm.
     *
     * @param str The AES encrypted string to be decoded.
     * @return The decoded string.
     * @throws CustomException If there is an error during the decoding process.
     */
    fun decodeAES(str: String, iv: String): String {
        return try {
            val decoded = Base64.getDecoder().decode(str)

            val ivParameterSpec = IvParameterSpec(iv.toByteArray())

            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(AESKEY256.toByteArray(StandardCharsets.UTF_8), "AES"), ivParameterSpec
            )
            String(removePadding(cipher.doFinal(decoded)), StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            throw ErrorMessage.UNKNOWN_ERROR.exception
        }
    }

    private fun padToMultiple(multiple: Int, src: ByteArray): ByteArray {
        var paddedSize = src.size
        val remainder = src.size.rem(multiple)
        if (remainder != 0) {
            paddedSize = src.size + multiple - remainder
        }
        return src.copyOf(paddedSize).also {
            for (i in src.size until paddedSize) {
                it[i] = (multiple - remainder).toByte()
            }
        }
    }

    private fun removePadding(src: ByteArray): ByteArray {
        val paddingCount = src.last().toInt()
        return if (paddingCount in 1 until 16) {
            src.copyOfRange(0, src.size - paddingCount)
        } else {
            src
        }
    }
}