package com.love.timi.auth

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper

@Component
class CachingRequestFilter : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val cachingRequest = ContentCachingRequestWrapper(request as HttpServletRequest)
        chain.doFilter(cachingRequest, response)
    }
}