package com.love.timi.service.common

import com.love.timi.service.v1.MailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener

@Configuration
class Kafka: BaseService() {
    @Autowired lateinit var mailService: MailService

    /**
     * 카프카 메시지 수신
     * @param message 수신된 메시지 본문
     */
    @KafkaListener(topics = ["terms_update_email"])
    private fun getTermsUpdateEmailMessage(message: Map<String, Any>) {
        log.info(">>> Received terms update email message: $message")

        val newVersion = message["newVersion"]
        val content = message["content"]

        if (newVersion !is String) {
            log.error("newVersion is not a String or is null: $newVersion")
            return
        }

        if (content !is String) {
            log.error("content is not a String or is null: $content")
            return
        }

        // 메일 서비스로 메시지 전달
        log.info(">>> Sending terms update email with newVersion: $newVersion and content: $content")
        mailService.sendTermsUpdateMail(newVersion, content)
        log.info(">>> Terms update email sent")
    }
}