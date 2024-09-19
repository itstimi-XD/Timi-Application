package com.love.timi.auth

import com.love.timi.data.TokenData
import com.love.timi.exception.ErrorMessage
import com.love.timi.service.common.BaseService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Service
class AuthInterceptor: HandlerInterceptor, BaseService() {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        request.setAttribute("tokenData", TokenData(
            userId = request.getHeader("X-Authorization-Id")?.toLong(),
            ip = request.getHeader("X-Origin-Address"),
            transactionId = request.getHeader("X-Transaction-Id")
        ))
        if (request.method == "OPTIONS" || !request.requestURI.startsWith("/user-auth/api/v")) return true

        // 토큰 없이 요청 가능한지 체크
        if (handler !is HandlerMethod || handler.getMethodAnnotation(AuthType::class.java) == null) return true

        // 슈퍼유저 아이디 확인
        // 아래의 아이디들은 DB 상에서 슈퍼유저로 간주되는 유저들의 아이디입니다.
        // 추가적인 슈퍼유저를 지정하려면 아래 리스트에 해당 유저의 아이디를 추가하면 됩니다.
        val superUserIds = listOf(999L, 989L, 1051L)
        if (superUserIds.contains(request.getHeader("X-Authorization-Id").toLong())) {
            request.setAttribute("tokenData", TokenData(
                userId = request.getHeader("X-Authorization-Id").toLong(),
                permissions = request.getHeader("X-Authorization-Permissions"),
                ip = request.getHeader("X-Origin-Address"),
                transactionId = request.getHeader("X-Transaction-Id")
            ))
            return true
        }

        // inner api 요청인지 확인
        if (request.getHeader("X-Authorization-Permissions").contains("INNERAPIPERMISSIONS")) {
            request.setAttribute("tokenData", TokenData(
                userId = request.getHeader("X-Authorization-Id").toLong(),
                permissions = request.getHeader("X-Authorization-Permissions"),
                ip = request.getHeader("X-Origin-Address"),
                transactionId = request.getHeader("X-Transaction-Id")
            ))
            return true
        }

        try {
            val permissions = request.getHeader("X-Authorization-Permissions")
            handler.getMethodAnnotation(AuthType::class.java)?.let {
                    permissionType -> permissionType.auth.forEach {
                if (permissions.contains(it.code)) {
                    request.setAttribute("tokenData", TokenData(
                        userId = request.getHeader("X-Authorization-Id").toLong(),
                        permissions = permissions,
                        ip = request.getHeader("X-Origin-Address"),
                        transactionId = request.getHeader("X-Transaction-Id")
                    ))
                    log.info(" 　　　　　　∧,,　　　")
                    log.info(" 　　　　　ヾ ｀. ､`フ")
                    log.info("　　　　(,｀'´ヽ､､ﾂﾞ")
                    log.info("　　 (ヽｖ'　　　`''ﾞつ")
                    log.info("　　　,ゝ　 ⌒`ｙ'''´")
                    log.info("　　 （ (´＾ヽこつ")
                    log.info("　　　 ) )")
                    log.info("　　　(ノ")
                    return true
                }
            }
            } ?: return true
        } catch (e: Exception) {
            throw ErrorMessage.PERMISSION_DENIED.exception
        }

        throw ErrorMessage.PERMISSION_DENIED.exception
    }
}
