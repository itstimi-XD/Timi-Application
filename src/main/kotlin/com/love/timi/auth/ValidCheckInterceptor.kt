package com.love.timi.auth

import com.love.timi.data.kafka.ApiLogging
import com.love.timi.exception.CustomException
import com.love.timi.exception.ErrorMessage
import com.love.timi.response.RestResponse
import com.love.timi.service.common.BaseService
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.validation.BindingResult
import org.springframework.web.util.ContentCachingRequestWrapper

@Aspect
@Component
class ValidCheckInterceptor: BaseService() {

    val mapper: ObjectMapper = ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)

    @Around("execution(* com.love.timi.controller.v*.*.*(..))")
    fun anyMethod(joinPoint: ProceedingJoinPoint): Any? {
        val objs = joinPoint.args
        for (obj in objs) {
            if (obj is BindingResult) {
                if (obj.hasErrors()) {
                    throw ErrorMessage.INVALID_PARAMETER.exception.setMsgValue(obj.fieldErrors.joinToString(", ") { it.field })
                }
            }
        }
        val request = getRequest()
        val requestBody = if(request is ContentCachingRequestWrapper) String((request).contentAsByteArray) else ""
        val requestParameters = request.parameterMap.entries.joinToString(", ") { "${it.key}=[${it.value.joinToString(",")}]" }

        logRequestAndParameters(request, requestBody, requestParameters)
        for (obj in joinPoint.args.filterIsInstance<BindingResult>()) { handleBindingResult(obj) }
        val permissions = (joinPoint.signature as MethodSignature).method.getAnnotation(AuthType::class.java)?.auth

        return try {
            handleJoinPoint(joinPoint, request, permissions, requestBody, requestParameters)
        } catch (exception: Exception) {
            handleException(exception, request, permissions, requestBody, requestParameters)
        }
    }

    private fun logRequestAndParameters(request: HttpServletRequest, requestBody: String, requestParameters: String) {
        log.info(">>> Request")
        log.info("> URL: ${request.requestURI} (${request.method})")
        log.info("> Parameters: $requestParameters")
        log.info("> Body: $requestBody")
    }

    private fun handleBindingResult(obj: BindingResult) {
        if (obj.hasErrors()) {
            throw ErrorMessage.INVALID_PARAMETER.exception.setMsgValue(obj.fieldErrors.joinToString(", ") { it.field })
        }
    }

    private fun handleJoinPoint(joinPoint: ProceedingJoinPoint, request: HttpServletRequest, permissions: Array<AuthType.Authority>?, requestBody: String, requestParameters: String): Any? {
        val result = joinPoint.proceed(joinPoint.args)
        val status = if (result is ResponseEntity<*>) {
            log.info(">>> Response")
            log.info("> Status: ${result.statusCode}")
            log.info("> Body: ${mapper.writeValueAsString(result.body)}")
            log.info(" ")
            result.statusCode.toString()
        } else ""
        logKafka(request, permissions, requestBody, requestParameters, status)
        return result
    }

    private fun handleException(exception: Exception, request: HttpServletRequest, permissions: Array<AuthType.Authority>?, requestBody: String, requestParameters: String): Any {
        log.info(">>> Exception")
        log.info("> Message: ${exception.message}")
        val customException = if (exception is CustomException) { exception } else { ErrorMessage.UNKNOWN_ERROR.exception }
        val result = RestResponse<HashMap<String, Any>>().customError(customException).setBody(customException.data).responseEntity()
        val status = result.statusCode.toString()
        log.info("> Status: $status")
        log.info("> Body: ${mapper.writeValueAsString(result.body)}")
        logKafka(request, permissions, requestBody, requestParameters, "$status (${customException.subCode})", customException.outputMessage?: customException.msg)
        throw exception
    }

    private fun logKafka(request: HttpServletRequest, permissions: Array<AuthType.Authority>?, requestBody: String, requestParameters: String, status: String, errorMessage: String? = null) {
        val gson = GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create()

        // userId가 null인 경우 함수 즉시 종료
        val userId = getTokenData().userId
        if (userId == null) {
            log.error("UserId is null. Exiting logKafka function.")
            return
        }

        log.info("Preparing ApiLogging object for userId: $userId")

        val apiLogging = ApiLogging(
            userId,
            "${request.requestURI} (${request.method})",
            permissions?.map { it.code }?.distinct()?.joinToString(",") ?: "N",
            requestParameters,
            permissions?.joinToString(",") { it.category ?: "" },
            permissions?.joinToString(",") { it.subCategory ?: "" },
            errorMessage,
            getIpAddr(),
            getTid(),
            status,
            requestBody
        )

        try {
            val apiLoggingJson = gson.toJson(apiLogging)
            log.info("ApiLogging JSON: $apiLoggingJson")
            kafkaSend("api_logging", gson.fromJson(apiLoggingJson, object : TypeToken<HashMap<String, Any>>() {}.type))
        } catch (e: Exception) {
            log.error("Error while sending Kafka log: ${e.message}", e)
        }
    }

}
