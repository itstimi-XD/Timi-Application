package com.love.timi.service.common

import com.love.timi.data.TokenData
import com.love.timi.data.UserTb
import com.love.timi.data.predicate.UserTbPredicate
import com.love.timi.data.repo.*
import com.love.timi.exception.ErrorMessage
import com.love.timi.util.CommonUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*

class BaseService: LogService() {
    @Autowired lateinit var userTbRepo: UserTbRepo

    @Autowired lateinit var emailVerificationTbRepo: EmailVerificationTbRepo

    @Autowired lateinit var ipTbRepo: IpTbRepo

    @Autowired lateinit var loginHistoryTbRepo: LoginHistoryTbRepo

    @Autowired lateinit var kafkaTemplate: KafkaTemplate<String, Map<String, Any>>

    @Autowired lateinit var roleTbRepo: RoleTbRepo

    @Autowired lateinit var roleChangeHistoryTbRepo: RoleChangeHistoryTbRepo

    @Autowired lateinit var roleMenuTbRepo: RoleMenuTbRepo

    @Autowired lateinit var menuTbRepo: MenuTbRepo

    fun getTokenData(): TokenData {
        return getRequest().getAttribute("tokenData") as TokenData
    }

    /**
     * AccessToken 에서 추출된 UserId 조회
     * @return UserId
     */
    fun getUserKey(): Long {
        return getTokenData().userId?: throw ErrorMessage.CANNOT_GET_USER_KEY.exception
    }

    /**
     * AccessToken 에서 추출된 UserPermissions 조회
     * @return UserPermissions
     */
    fun getPermissions(): String {
        return getTokenData().permissions?: throw ErrorMessage.PERMISSION_DENIED.exception
    }

    /**
     * Gateway 에서 발급된 Transaction ID 조회
     * @return Transaction ID
     */
    fun getTid(): String {
        return getTokenData().transactionId?: throw ErrorMessage.PERMISSION_DENIED.exception
    }

    /**
     * 게이트 웨이에서 보내준 IP 가져오기
     */
    fun getIpAddr() : String {
        return getTokenData().ip?: throw ErrorMessage.PERMISSION_DENIED.exception
    }

    /**
     * 현재 요청의 Request 조회
     * @return Request
     */
    fun getRequest(): HttpServletRequest {
        return (Objects.requireNonNull(RequestContextHolder.getRequestAttributes()) as ServletRequestAttributes).request
    }

    /**
     * Kafka 메시지 송신
     * @param topic 발행 대상 토픽
     * @param message 발송 메시지 본문
     */
    fun kafkaSend(topic: String, message: Map<String, Any>) {
        kafkaTemplate.send(topic, message)
    }

    /**
     * 전체 사용자 이메일 리스트(현대 도메인만, 메일 발송 가능 주소)
     * @return List<String>
     */
    fun getAllUserEmails(): List<String> {
        val userList = userTbRepo.findAll(UserTbPredicate.search(UserTb()))

        val domains = listOf("hyundai-autoever.com","ict-companion.com", "hyundai.com")

        val userEmails = userList.filter {
            it.roleId != 30L &&
                    (it.statusName == "ACTIVE" || it.statusName == "INACTIVE") &&
                    CommonUtil.isEmailFromOneOfDomains(it.email!!, domains) }      // check if email domain is valid
            .map { it.email!! }

        return userEmails
    }
}