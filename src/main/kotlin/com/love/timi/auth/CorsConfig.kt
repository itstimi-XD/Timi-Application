package com.love.timi.auth

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig: WebMvcConfigurer {
    val METHOD = arrayOf("GET", "POST", "PUT", "DELETE")
    val HEADER = arrayOf("Origin","Accept","X-Requested-With","Content-Type","Access-Control-Request-Method","Access-Control-Request-Headers","Authorization")
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods(*METHOD)
            .allowedHeaders(*HEADER)
            .allowCredentials(true)
            .maxAge(3600)
    }
}
