package com.love.timi.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
object LoveProperties {

    @Component
    @ConfigurationProperties(prefix="love.chiper")
    object Chiper {
        lateinit var aes256: String
        lateinit var ivspec: String
    }

    @Component
    @ConfigurationProperties(prefix="love.search")
    object Search {
        lateinit var gatewayUrl: String
    }

    @Component
    @ConfigurationProperties(prefix="spring.application")
    object Application {
        lateinit var name: String
    }

    @Component
    @ConfigurationProperties(prefix = "love.project")
    object Project {
        lateinit var date: String
        lateinit var version: String
        lateinit var build: String
    }

}