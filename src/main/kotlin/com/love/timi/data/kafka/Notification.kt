package com.love.timi.data.kafka

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Kafka Topic : notification", description = "Common을 통해 알림이 필요한 내용을 남길 수 있는 토픽")
data class Notification(
    val userId: Long? = null,
    val roleId: Long? = null,
    val showAble: String? = null,
    val korMessage: String? = null,
    val engMessage: String? = null,
    val link: String? = null,
    val data: String? = null
)
