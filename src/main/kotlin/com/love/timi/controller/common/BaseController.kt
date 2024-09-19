package com.love.timi.controller.common

import com.love.timi.data.TokenData
import com.love.timi.exception.ErrorMessage
import com.love.timi.service.common.LogService
import com.google.gson.GsonBuilder
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.HashMap

class BaseController: LogService() {
    fun getTokenData(): TokenData {
        return getRequest().getAttribute("tokenData") as TokenData
    }

    /**
     * form-data / x-www-form-urlencoded 형식으로 입력된 파라미터 조회
     * @param key Parameter Key
     * @return Parameter Value
     */
    fun getParameter(key: String): String {
        return getRequest().getParameter(key)?: throw ErrorMessage.INVALID_PARAMETER.exception.setMsgValue(key)
    }

    /**
     * form-data / x-www-form-urlencoded 형식으로 입력된 파라미터 조회
     * @param key Parameter Key
     * @param defaultValue Parameter Value가 Null인경우 대체 값
     * @return Parameter Value or defaultValue
     */
    fun getParameter(key: String, defaultValue: String): String {
        return getRequest().getParameter(key)?: defaultValue
    }

    /**
     * form-data / x-www-form-urlencoded 형식으로 입력된 파라미터 조회
     * @param key Parameter Key
     * @return Parameter Value or Null
     */
    fun getParameterOrNull(key: String): String? {
        return getRequest().getParameter(key)?: null
    }

    /**
     * form-data / x-www-form-urlencoded 형식으로 입력된 파라미터 전체 조회
     * @return Parameters Map
     */
    fun getParameters(): HashMap<String, String> {
        val parameters = HashMap<String, String>()
        getRequest().parameterNames.asIterator().forEachRemaining { parameters[it] = getParameter(it) }
        return parameters
    }

    /**
     * 게이트웨이에서 디코딩된 요청사용자 ID 조회
     * @return User ID
     */
    fun getUserKey(): String {
        return getRequest().getHeader("X-Authorization-Id")
    }

    /**
     * 게이트웨이에서 디코딩된 요청사용자 Permissions 조회
     * @return User Permissions
     */
    fun getPermissions(): String {
        return getRequest().getHeader("X-Authorization-Permissions")
    }

    /**
     * 게이트웨이에서 발급한 Transaction ID 조회
     * @return Transaction ID
     */
    fun getTid(): String {
        return getRequest().getHeader("Pilot-Transaction-Id")
    }

    /**
     * 요청의 Request 조회
     * @return 현재 요청의 Request
     */
    private fun getRequest(): HttpServletRequest {
        return RequestContextHolder.getRequestAttributes().let { (it as ServletRequestAttributes).request }
    }

    /**
     * application/json 형식으로 입력된 Body 데이터 전체 조회
     * 최초 1회만 조회 할 수 있음
     * @return Body List
     */
    fun getBodyArray(): List<*> {
        val sb = StringBuffer()
        getRequest().reader.readLines().forEach { sb.append(it) }
        return GsonBuilder().registerTypeAdapter(MutableList::class.java,
            com.love.timi.util.ArrayDeserializer()
        ).serializeNulls().create().fromJson(sb.toString(), List::class.java)?: throw ErrorMessage.INVALID_PARAMETER.exception.setMsgValue("body")
    }

    /**
     * application/json 형식으로 입력된 Body 데이터 전체 조회
     * 최초 1회만 조회 할 수 있음
     * @return Body Map
     */
    fun getBodyMap(): Map<*, *> {
        val sb = StringBuffer()
        getRequest().reader.readLines().forEach { sb.append(it) }
        return GsonBuilder().registerTypeAdapter(MutableMap::class.java,
            com.love.timi.util.MapDeserializer()
        ).serializeNulls().create().fromJson(sb.toString(), Map::class.java)?: throw ErrorMessage.INVALID_PARAMETER.exception.setMsgValue("body")
    }


}