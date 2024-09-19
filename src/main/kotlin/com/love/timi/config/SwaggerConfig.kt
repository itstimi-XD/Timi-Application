package com.love.timi.config

import com.love.timi.properties.LoveProperties
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Value("\${spring.application.name}")
    private lateinit var projectName: String

    @Bean
    fun openAPI(): OpenAPI {

        val local = Server()
        local.url = "http://localhost:8081/"
        local.description = "로컬"

        val jwtSecurityScheme = SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")

        return OpenAPI()
            .info(
                Info()
                    .title("$projectName Rest API Documentation")
                    .description("$projectName API 명세\n\n Build : ${LoveProperties.Project.build} \n")
                    .version(LoveProperties.Project.version)
            )
            .servers(listOf(local))
            .components(
                Components().addSecuritySchemes("JWTAuth", jwtSecurityScheme)
            )
            .addSecurityItem(SecurityRequirement().addList("JWTAuth"))
    }
}