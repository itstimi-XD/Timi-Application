package com.love.timi.data.kafka

data class TermsAgreement(
    val userId: Long? = null,
    val termId: Long? = null,
    val version: String? = null,
    val agreeYn: String? = null
)
