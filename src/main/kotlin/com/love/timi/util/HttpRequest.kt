package com.love.timi.util

import com.love.timi.exception.ErrorMessage
import com.love.timi.properties.LoveProperties
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import org.apache.http.HttpHeaders
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicHeader
import org.apache.http.util.EntityUtils
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

class HttpRequest {
    /**
     * 내부망의 다른 서비스를 호출하기 위한 메서드
     * @param apiUrl 호출할 API URI (HOST 제외) ex) /api/v1/test
     * @param requestBody 요청 파라미터
     * @param method 요청 REST METHOD
     * @param userId 처리중인 사용자 ID
     * @param ip 요청자 IP
     * @param permissions 요청자 권한 목록
     * @param tid 요청 트랜잭션 ID
     * @return 응답 DATA Map
     */
    fun inApiCall(apiUrl: String, requestBody: Map<String, String>, method: HTTPMethod, userId: String, permissions: String, tid: String, ip: String): Map<String, Any> {
        return try {
            val request: HttpRequestBase = method.messageObject
            request.config = RequestConfig.custom().setSocketTimeout(30 * 1000).build()
            request.addHeader(BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.toString()))
            request.addHeader(BasicHeader("X-Authorization-Id", userId))
            request.addHeader(BasicHeader("X-Authorization-Permissions", permissions))
            request.addHeader(BasicHeader("X-Transaction-Id", tid))
            request.addHeader(BasicHeader("X-Origin-Address", ip))
            request.addHeader(BasicHeader("X-Inner-Token", Base64.getEncoder().encodeToString("${LoveProperties.Application.name}/${System.currentTimeMillis()}".toByteArray())))
            request.uri = URI.create("${LoveProperties.Search.gatewayUrl}/inner/service$apiUrl")
            if (request is HttpEntityEnclosingRequestBase) {
                request.entity = StringEntity(dataEncode(requestBody), ContentType.APPLICATION_FORM_URLENCODED)
            }
            val httpClients = HttpClients.createDefault()
            val response = httpClients.execute(request)
            val entity = response.entity
            val entityString = String(
                EntityUtils.toString(entity).trim { it <= ' ' }.toByteArray(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
            )
            GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create().fromJson<Map<String, Any>>(entityString, MutableMap::class.java)
        } catch (e: Exception) {
            throw ErrorMessage.SERVER_API_FAIL.exception
        }
    }

    /**
     * 외부 API를 form-urlencoded 형식으로 호출하기 위한 메서드
     * @param apiUrl 호출할 API URI (HOST 포함) ex) https://www.naver.com/
     * @param requestBody 요청 파라미터
     * @param headers 요청 헤더
     * @param method 요청 REST METHOD
     * @return 응답 DATA Map
     */
    fun outApiCall(apiUrl: String, requestBody: Map<String, String>, headers: Map<String, String>, method: HTTPMethod): Map<String, Any> {
        return try {
            val request: HttpRequestBase = method.messageObject
            request.config = RequestConfig.custom().setSocketTimeout(30 * 1000).build()
            request.addHeader(BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.toString()))
            headers.forEach {(key, value) ->
                request.addHeader(BasicHeader(key, value))
            }
            request.uri = URI.create(apiUrl)
            if (request is HttpEntityEnclosingRequestBase) { request.entity = StringEntity(dataEncode(requestBody), ContentType.APPLICATION_FORM_URLENCODED) }
            val httpClients = HttpClients.createDefault()
            val response = httpClients.execute(request)
            val entity = response.entity
            val entityString = String(
                EntityUtils.toString(entity).trim { it <= ' ' }.toByteArray(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
            )
            println(entityString)
            GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create().fromJson<Map<String, Any>>(entityString, MutableMap::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            throw ErrorMessage.SERVER_API_FAIL.exception
        }
    }

    /**
     * 외부 API를 JSON 형식으로 호출하기 위한 메서드
     * @param apiUrl 호출할 API URI (HOST 포함) ex) https://www.naver.com/
     * @param requestBody 요청 파라미터
     * @param headers 요청 헤더
     * @param method 요청 REST METHOD
     * @return 응답 DATA Map
     */
    fun outApiCallJson(apiUrl: String, requestBody: Map<String, String>, headers: Map<String, String>, method: HTTPMethod): Map<String, Any> {
        return try {
            val request: HttpRequestBase = method.messageObject
            request.config = RequestConfig.custom().setSocketTimeout(30 * 1000).build()
            request.addHeader(BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString()))
            headers.forEach {(key, value) ->
                request.addHeader(BasicHeader(key, value))
            }
            request.uri = URI.create(apiUrl)
            if (request is HttpEntityEnclosingRequestBase) {
                request.entity = StringEntity(toJson(requestBody), ContentType.APPLICATION_JSON)
            }
            val httpClients = HttpClients.createDefault()
            val response = httpClients.execute(request)
            val entity = response.entity
            val entityString = String(
                EntityUtils.toString(entity).trim { it <= ' ' }.toByteArray(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
            )
            GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create().fromJson<Map<String, Any>>(entityString, MutableMap::class.java)
        } catch (e: Exception) {
            throw ErrorMessage.SERVER_API_FAIL.exception
        }
    }

    /**
     * 요청 Data Body
     * @param data form-urlencoded 형식 문자열로 변환할 Data Body Map
     * @return form-urlencoded 형식으로 변환된 문자열
     */
    private fun dataEncode(data: Map<String, String>): String {
        val builder = StringBuilder()
        data.forEach { (key, value) ->
            if (builder.isNotEmpty()) {
                builder.append("&")
            }
            builder.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(value, StandardCharsets.UTF_8))
        }
        return builder.toString()
    }

    /**
     * 요청 Data Body
     * @param data application/json 형식 문자열로 변환할 Data Body Map
     * @return application/json 형식으로 변환된 문자열
     */
    private fun toJson(data: Map<String, String>): String? {
        return Gson().toJson(data)
    }

}