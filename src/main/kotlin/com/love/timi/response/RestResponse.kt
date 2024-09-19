package com.love.timi.response

import com.love.timi.exception.CustomException
import com.love.timi.service.common.LogService
import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class RestResponse<T>: LogService() {

    @JsonIgnore
    var body = ""
    @JsonIgnore
    var headers = HttpHeaders()
    @Expose
    var data: T? = null
    @Expose
    var code = 0
    @Expose
    var subCode = 0
    @Expose
    var error = ""

    /**
     * 생성자 기본헤더 셋팅
     */
    init {
        RestResponse()
    }

    /**
     * 요청 정상 처리 된 경우 호출
     * @return this
     */
    fun ok(): RestResponse<T> {
        val instance = this
        instance.code = HttpStatus.OK.value()
        instance.error = "Success"
        return instance
    }

    /**
     * 생성 요청 정상 처리 된 경우 호출
     * @return this
     */
    fun created(): RestResponse<T> {
        val instance = this
        instance.code = HttpStatus.CREATED.value()
        instance.error = "Success"
        return instance
    }

    /**
     * 에러 발생시 처리 (Exception Handler에서 사용)
     * @param e 서비스 정의 에러
     * @return this
     */
    fun customError(e: CustomException): RestResponse<T> {
        val instance = this
        instance.code = e.code
        instance.subCode = e.subCode
        instance.error = e.outputMessage?: e.msg
        return instance
    }

    /**
     * Response BODY 입력
     * @param body 입력할 데이터 Map
     * @return this
     */
    fun setBody(body: T): RestResponse<T> {
        data = body
        return this
    }

    /**
     * Response Header 입력
     * @param name Header Key
     * @param value Header Value
     * @return this
     */
    fun setHeader(name: String, value: String): RestResponse<T> {
        addHeader(name, value)
        return this
    }

    /**
     * Response Entity 생성
     * @return ResponseEntity
     */
    fun responseEntity(): ResponseEntity<RestResponse<T>> {
        return ResponseEntity<RestResponse<T>>(this, headers, HttpStatus.valueOf(code))
    }

    /**
     * 생성자가 호출
     * 기본 응답 헤더 추가
     */
    private fun RestResponse(): RestResponse<T> {
        addHeader("Content-Type", "application/json")
        addHeader("charset", "utf-8")
        addHeader("Access-Control-Allow-Origin", "*")
        addHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,PUT,OPTIONS")
        addHeader("Access-Control-Max-Age", "3600")
        addHeader("Access-Control-Allow-Headers", "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization")

        return this
    }

    /**
     * 헤더 추가
     * @param name Header Key
     * @param value Header Value
     */
    private fun addHeader(name: String, value: String?): RestResponse<T> {
        headers.set(name, value)
        return this
    }

    /**
     * 현재 객체 JSON으로 변환
     * @return Json String
     */
    private fun toJsonString(): String {
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(this)
    }
}