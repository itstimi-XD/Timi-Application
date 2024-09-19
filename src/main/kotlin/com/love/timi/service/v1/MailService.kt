package com.love.timi.service.v1

import com.love.timi.service.common.BaseService
import com.love.timi.util.RandomCode
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class MailService(
    private var javaMailSender: JavaMailSender
): BaseService(){

    @Value("\${spring.mail.sender}")
    private lateinit var senderEmail: String

    companion object{
        private var number = 0

        private fun generateNumber(){
            number = RandomCode().randomSixCode(6)
        }
    }

    fun createCodeMail(email: String): MimeMessage {

        generateNumber()
        val message = javaMailSender.createMimeMessage()

        try {
            message.setFrom(senderEmail)
            message.setRecipients(MimeMessage.RecipientType.TO, email)
            message.subject = "인증번호 발송"
            var body = ""
            body += "<h3>요청하신 인증 번호 입니다.</h3>"
            body += "<h1>$number</h1>"
            body += "<h3>감사합니다.</h3>"
            message.setText(body, "UTF-8", "html")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return message
    }

    fun sendApprovalMail(email: String) {

        val message = javaMailSender.createMimeMessage()

        try {
            message.setFrom(senderEmail)
            message.setRecipients(MimeMessage.RecipientType.TO, email)
            message.subject = "[Global Search] 회원가입 승인 안내"
            var body = ""
            body += "<h3>안녕하세요, Global Search 관리자입니다.</h3>"
            body += "<h3>귀하의 회원가입이 승인되었습니다.</h3>"
            body += "<h3>감사합니다.</h3>"
            message.setText(body, "UTF-8", "html")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        javaMailSender.send(message)
    }

    fun sendPwResetResultMail(email:String, password:String){

        val message = javaMailSender.createMimeMessage()

        try {
            message.setFrom(senderEmail)
            message.setRecipients(MimeMessage.RecipientType.TO, email)
            message.subject = "[Global Search] 비밀번호 초기화 안내"
            var body = ""
            body += "<h3>안녕하세요, Global Search 관리자입니다.</h3>"
            body += "<h3>귀하의 비밀번호가 $password 로 초기화 되었습니다.</h3>"
            body += "<h3>감사합니다.</h3>"
            message.setText(body, "UTF-8", "html")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        javaMailSender.send(message)
    }

    fun sendCodeMail(email: String): Int{
        javaMailSender.send(createCodeMail(email))
        return number
    }


    fun sendTermsUpdateMail(newVersion: String, content: String) {
        val userEmails = getAllUserEmails() // 모든 사용자 이메일 목록을 가져오는 메서드

        userEmails.forEach { email ->
            val mimeMessage = javaMailSender.createMimeMessage()

            try {
                log.info(">>> Sending email to: $email")
                mimeMessage.setFrom(senderEmail)
                mimeMessage.setRecipients(MimeMessage.RecipientType.TO, email)
                mimeMessage.subject = "약관 업데이트 안내"
                val body = """
                    <h3>안녕하세요, Global Search 관리자입니다.</h3>
                    <h3>개인정보보호수집동의 약관이 v$newVersion 으로 업데이트 되었습니다.</h3>
                    <h3>업데이트된 약관 내용은 다음과 같습니다:</h3>
                    <p>$content</p>
                    <h3>감사합니다.</h3>
                """.trimIndent()
                mimeMessage.setText(body, "UTF-8", "html")
                javaMailSender.send(mimeMessage)
                log.info(">>> Email sent to: $email")
            } catch (e: Exception) {
                log.error(">>> Failed to send email to: $email", e)
            }
        }
    }
}