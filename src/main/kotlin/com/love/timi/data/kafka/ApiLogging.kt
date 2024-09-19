package com.love.timi.data.kafka

data class ApiLogging(
        val userId: Long? = null,
        val requestUri: String? = null,
        val acrudType: String? = null,
        val requestParams: String? = null,
        val categoryMain: String? = null,
        val categorySub: String? = null,
        val description: String? = null,
        val requestIp: String? = null,
        val transactionId: String? = null,
        val responseStatus: String? = null,
        val requestBody: String? = null
)
