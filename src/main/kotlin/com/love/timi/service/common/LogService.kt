package com.love.timi.service.common

import com.fasterxml.jackson.annotation.JsonIgnore
import com.love.timi.auth.CustomLogger
import org.springframework.stereotype.Service

@Service
class LogService {
    @JsonIgnore
    val log = CustomLogger("Love-Timi-Service")
}