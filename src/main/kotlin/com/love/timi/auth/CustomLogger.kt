package com.love.timi.auth

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*

class CustomLogger(name: String) {
    
    private val log :Logger = LoggerFactory.getLogger(name)

    private fun getTid(): String {
        return try {
            val request = getRequest()
            request.getHeader("X-Transaction-Id")
        } catch (_: Exception) { "None-Request-Transaction" }
    }

    private fun getRequest(): HttpServletRequest {
        return (Objects.requireNonNull(RequestContextHolder.getRequestAttributes()) as ServletRequestAttributes).request
    }

    fun trace(p0: String?) {
        log.trace("[${getTid()}] $p0")
    }

    fun trace(p0: String?, p1: Any?) {
        log.trace("[${getTid()}] $p0", p1)
    }

    fun trace(p0: String?, p1: Any?, p2: Any?) {
        log.trace("[${getTid()}] $p0", p1, p2)
    }

    fun trace(p0: String?, vararg p1: Any?) {
        log.trace("[${getTid()}] $p0", p1)
    }

    fun trace(p0: String?, p1: Throwable?) {
        log.trace("[${getTid()}] $p0", p1)
    }

    fun debug(p0: String?) {
        log.debug("[${getTid()}] $p0")
    }

    fun debug(p0: String?, p1: Any?) {
        log.debug("[${getTid()}] $p0", p1)
    }

    fun debug(p0: String?, p1: Any?, p2: Any?) {
        log.debug("[${getTid()}] $p0", p1, p2)
    }

    fun debug(p0: String?, vararg p1: Any?) {
        log.debug("[${getTid()}] $p0", p1)
    }

    fun debug(p0: String?, p1: Throwable?) {
        log.debug("[${getTid()}] $p0", p1)
    }

    fun info(p0: String?) {
        log.info("[${getTid()}] $p0")
    }

    fun info(p0: String?, p1: Any?) {
        log.info("[${getTid()}] $p0", p1)
    }

    fun info(p0: String?, p1: Any?, p2: Any?) {
        log.info("[${getTid()}] $p0", p1, p2)
    }

    fun info(p0: String?, vararg p1: Any?) {
        log.info("[${getTid()}] $p0", p1)
    }

    fun info(p0: String?, p1: Throwable?) {
        log.info("[${getTid()}] $p0", p1)
    }

    fun warn(p0: String?) {
        log.warn("[${getTid()}] $p0")
    }

    fun warn(p0: String?, p1: Any?) {
        log.warn("[${getTid()}] $p0", p1)
    }

    fun warn(p0: String?, vararg p1: Any?) {
        log.warn("[${getTid()}] $p0", p1)
    }

    fun warn(p0: String?, p1: Any?, p2: Any?) {
        log.warn("[${getTid()}] $p0", p1, p2)
    }

    fun warn(p0: String?, p1: Throwable?) {
        log.warn("[${getTid()}] $p0", p1)
    }

    fun error(p0: String?) {
        log.error("[${getTid()}] $p0")
    }

    fun error(p0: String?, p1: Any?) {
        log.error("[${getTid()}] $p0", p1)
    }

    fun error(p0: String?, p1: Any?, p2: Any?) {
        log.error("[${getTid()}] $p0", p1, p2)
    }

    fun error(p0: String?, vararg p1: Any?) {
        log.error("[${getTid()}] $p0", p1)
    }

    fun error(p0: String?, p1: Throwable?) {
        log.error("[${getTid()}] $p0", p1)
    }
}