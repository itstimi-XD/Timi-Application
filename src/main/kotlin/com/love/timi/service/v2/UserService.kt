package com.love.timi.service.v2

import com.love.timi.data.*
import com.love.timi.data.dto.*
import com.love.timi.data.dto.request.*
import com.love.timi.data.dto.response.*
import com.love.timi.data.kafka.Notification
import com.love.timi.data.kafka.TermsAgreement
import com.love.timi.data.predicate.LoginHistoryTbPredicate
import com.love.timi.data.predicate.UserTbPredicate
import com.love.timi.data.repo.*
import com.love.timi.enumTypes.UserStatus
import com.love.timi.exception.ErrorMessage
import com.love.timi.service.common.BaseService
import com.love.timi.service.v1.MailService
import com.love.timi.util.*
import com.love.timi.util.CommonUtil.isIpValid
import com.love.timi.util.ShaUtil.encodeSHA256
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import org.apache.commons.codec.binary.Base32
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeParseException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.HashMap

@Service("UserServiceV2")
class UserService: BaseService() {

    // 순환참조 문제로 인해 BaseService가 아닌 이 곳에 의존성 주입함
    @Autowired lateinit var authService: AuthService
    @Autowired lateinit var mailService: MailService

    private val iv = "cF?5NN6R?c9xoaL#"
    // ========================================================================================================================

    /**
     * 인증메일 발송
     * @param emailSendReq: EmailSendReq
     * @return HashMap
     */
    fun verifyEmail(emailSendReq: EmailSendReq): HashMap<String, Any> {
        // 입력받은 이메일 형식 검증
        if (!CommonUtil.isEmailValid(emailSendReq.email!!)) throw ErrorMessage.INVALID_EMAIL.exception

        // 인증메일 발송
        val code = mailService.sendCodeMail(emailSendReq.email)

        //데이터베이스에 인증 코드 정보 저장
        val emailData = emailVerificationTbRepo.save(EmailVerificationTb().apply {
            this.email = emailSendReq.email
            this.verificationCode = code.toString()
            this.createdAt = LocalDateTime.now(ZoneOffset.UTC)
            this.expiresAt = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(3)
            this.isVerified = "N"
        }).copy()

        return HashMap<String, Any>().apply {
            this["result"] = "Success"
            this["verifiedEmail"] = emailData
        }
    }

    /**
     * 인증메일 내의 인증번호 확인
     * @param emailVerificationReq: EmailVerificationReq
     * @return HashMap
     */
    @Transactional
    fun confirmEmail(emailVerificationReq: EmailVerificationReq): HashMap<String, Any> {
        // 데이터베이스에서 해당 이메일로 저장된 인증 정보 조회
        val dbEmailVerification =
            emailVerificationTbRepo.findOne(EmailVerificationTb().apply { this.email = emailVerificationReq.email })
                ?.copy()
                ?: throw ErrorMessage.EMAIL_VERIFICATION_NOT_FOUND.exception

        // 인증번호 만료 시간 확인
        dbEmailVerification.expiresAt?.let {
            if (it.isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
                emailVerificationTbRepo.delete(dbEmailVerification)
                throw ErrorMessage.EMAIL_VERIFICATION_CODE_EXPIRED.exception
            }
        }
        // 인증 코드가 일치하는지 확인
        dbEmailVerification.verificationCode?.let {
            if (it != emailVerificationReq.code) throw ErrorMessage.EMAIL_VERIFICATION_CODE_MISMATCH.exception
        }

        // 인증정보의 이메일로 사용자 찾아보기
        val user = userTbRepo.findOne(UserTb().apply { this.email = emailVerificationReq.email })?.copy()

        // 사용자가 존재하지 않는 경우 - 회원가입 시의 인증으로 간주
        if (user == null) {
            // 회원가입 대상인 도메인으로부터 왔는지 확인
            val domains = listOf("hyundai-autoever.com","ict-companion.com", "hyundai.com")
            val isValidDomain = CommonUtil.isEmailFromOneOfDomains(emailVerificationReq.email!!, domains)
            // 대상이 아닐 경우 에러 발생
            if(!isValidDomain) throw ErrorMessage.INVALID_EMAIL_DOMAIN.exception

            // 인증 상태를 Y로 변경
            dbEmailVerification.isVerified = "Y"
            // 인증정보 저장
            emailVerificationTbRepo.save(dbEmailVerification)
            // 성공 응답 반환
            return HashMap<String, Any>().apply {
                this["result"] = "Success"
            }
        }
        // 사용자가 존재하는 경우 - 상태에 따라 처리
        when (user.statusName) {
            UserStatus.INACTIVE.name, UserStatus.DORMANCY.name -> {
                // 사용자 상태를 활성화로 변경
                user.statusName = UserStatus.ACTIVE.name
                // 비밀번호 + OTP 실패 횟수 초기화
                user.failCount = 0
                // 사용자 정보 업데이트
                userTbRepo.save(user)
                // 인증정보 삭제
                emailVerificationTbRepo.delete(dbEmailVerification)
            }
            else -> throw ErrorMessage.EMAIL_ALREADY_REGISTERED.exception
        }

        return HashMap<String, Any>().apply {
            this["result"] = "Success"
        }
    }

    /*
     * 현재 요청의 IP 조회
     */
    fun getIp(tokenData: TokenData): UserIpRes {
        return UserIpRes(tokenData.ip?: throw ErrorMessage.PERMISSION_DENIED.exception)
    }

    /**
     * 회원가입
     * @request registerRequest: RegisterReq
     * @return SuccessResponse
     */
    @Transactional
    fun register(registerRequest: RegisterReq): HashMap<String, Any> {
        // 이메일 형식 검증
        if (!CommonUtil.isEmailValid(registerRequest.email!!)) throw ErrorMessage.INVALID_EMAIL.exception
        // 이메일 가입여부 확인
        userTbRepo.findOne(UserTb().apply { this.email = registerRequest.email })?.copy()
            ?.let { throw ErrorMessage.EMAIL_ALREADY_REGISTERED.exception }
        // 이메일 인증 정보 가져옴
        val dbEmailVerification =
            emailVerificationTbRepo.findOne(EmailVerificationTb().apply { this.email = registerRequest.email })?.copy()
                ?: throw ErrorMessage.EMAIL_VERIFICATION_NOT_FOUND.exception
        // 인증 상태 확인
        dbEmailVerification.let {
            if (it.isVerified == "N") throw ErrorMessage.EMAIL_VERIFICATION_NOT_FOUND.exception
        }

        // 사용자가 약관들에 모두 동의 했는지 확인
        registerRequest.terms.forEach{ term ->
            if (term.agreeYn == "N") throw ErrorMessage.TERM_AGREEMENT_REQUIRED.exception
        }


        // 추후에 사용자 정보 사용해야 해서 일단 변수 선언
        val user: UserTb

        //ip를 저장해야하는 역할이냐 아니냐에 따라서 달라지는 회원가입 단계
        when (registerRequest.role) {
            // 운영자를 선택한 사용자는 ip 입력이 필수
            29L -> {
                //ip 정보가 없으면 예외발생
                if (registerRequest.ip?.isEmpty() == true) throw ErrorMessage.NO_IP_ADDRESS.exception
                // 회원 정보 저장
                user = userTbRepo.save(setCommonUserProperties(UserTb(), registerRequest).copy())
                // ip 저장
                registerRequest.ip?.forEach { ip ->
                    if (!ip.address?.let { isIpValid(it) }!!) throw ErrorMessage.INVALID_IP_ADDRESS.exception
                    ipTbRepo.save(IpTb().apply {
                        this.userId = user.userId
                        this.address = ip.address
                        this.description = ip.desc
                        this.registeredAt = LocalDateTime.now(ZoneOffset.UTC)
                    })
                }
            }
            // 운영자 외의 사용자는 ip저장 없이 바로 회원가입
            else -> {
                user = userTbRepo.save(setCommonUserProperties(UserTb(), registerRequest))
            }
        }
        //회원가입 완료되기 직전에 인증정보 삭제
        emailVerificationTbRepo.delete(dbEmailVerification)

        // 사용자의 약관 동의 여부 kafka message 발행
        val gson = GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create()

        // 사용자가 동의한 내역 족족 하나씩 Common Service 가져갈 용으로(?) 메세지 발행
        registerRequest.terms.forEach { term ->

            kafkaSend("terms_agreement", gson.fromJson(gson.toJson(
                TermsAgreement(
                    userId = user.userId,
                    termId = term.termId,
                    version = term.version,
                    agreeYn = term.agreeYn
                )
            ), object : TypeToken<HashMap<String, Any>>() {}.type)
            )
        }

        // 운영자 역할의 사람들에게 승인 대기중인 사용자 명 수 포함하여 알림 발행
        val pendingUserCount = userTbRepo.count(UserTbPredicate.search(
            UserTb().apply {
                this.statusName = UserStatus.PENDING.name
            }))

        // 사용자 목록에서 수정권한 있는 역할
        val roles = roleMenuTbRepo.findByMenuIdAndApprovalYn(161, "Y")
            .filter { it.roleId != 30L }
        if(roles.size > 2 || roles.size == 2){
            throw ErrorMessage.USER_LIST_APPROVAL_ROLE_DUPLICATED.exception
        }
        val roleId = roles.first().roleId

        kafkaSend("notification", gson.fromJson(gson.toJson(
            Notification(
                userId = null,
                roleId = roleId,
                showAble = "Y",
                link = "/user-management/user-list",
                korMessage = "${pendingUserCount}건의 회원가입 요청이 승인 대기 중입니다.",
                engMessage = "${pendingUserCount} of account are pending status."
            )
        ), object : TypeToken<HashMap<String, Any>>() {}.type)
        )
        return HashMap<String, Any>().apply {
            this["result"] = "Success"
            this["user"] = user
        }
    }
    private fun setCommonUserProperties(user: UserTb, registerRequest: RegisterReq): UserTb {
        user.email = registerRequest.email
        user.password = registerRequest.password
        user.name = registerRequest.name
        user.companyName = registerRequest.company
        user.teamName = registerRequest.team
        user.roleId = registerRequest.role
        // 추후 해당 컬럼 삭제 및 관련 코드 모두 삭제
        user.termsAccepted = "Y"
        user.failCount = 0
        user.registeredAt = LocalDateTime.now(ZoneOffset.UTC)
        user.statusName = UserStatus.PENDING.name
        user.isTwoFactorAuth = "N"
        user.pwChangeRequiredAt = LocalDateTime.now(ZoneOffset.UTC).plusDays(180)
        return user
    }
    // ========================================================================================================================

    fun isAccessibleIp(userId: Long, ip: String): Boolean {
        val ipTb = ipTbRepo.findOne(IpTb().apply { this.userId = userId; this.address = ip })?.copy()
        log.info("IP Access Attempt for UserID: $userId with IP: $ip, Access Allowed: ${ipTb != null}")
        return ipTb != null
    }
    /**
     * 로그인(1차 로그인. id, pw를 검증한다.)
     * @param pwVerificationReq
     * @return HashMap
     */
    fun login(pwVerificationReq: PwVerificationReq, tokenData: TokenData): PasswordVerificationRes {
        // email로 저장된 회원정보 조회
        val dbUser = userTbRepo.findOne(UserTb().apply { this.email = pwVerificationReq.email })?.copy()
            ?: throw ErrorMessage.USER_NOT_FOUND.exception

        // 사용자 상태 활성화 여부 검증 및 예외처리 함수 호출
        isUserActive(dbUser)
        // 운영자 계정은 등록된 IP만 접근 가능
        if (dbUser.roleId == 29L) {
            val ipAddr = tokenData.ip ?: throw ErrorMessage.PERMISSION_DENIED.exception
            log.info("Verifying access for admin user: ${dbUser.userId} from IP: $ipAddr")
            if (!isAccessibleIp(dbUser.userId!!, ipAddr)) throw ErrorMessage.IP_NOT_ALLOWED.exception
        }
        // 90일 이상 로그인 안한 계정은 휴면처리
        dbUser.lastLoginAt?.let {
            if (it.plusDays(90).isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
                // 휴면 계정 처리 로그
                log.info("Last login check for user: ${dbUser.userId}, Last Login: $it")
                dbUser.statusName = UserStatus.DORMANCY.name
                userTbRepo.save(dbUser)
                throw ErrorMessage.DORMANCY_USER.exception
            }
        }
        // 로그인 실패 회수가 5회 이상이 아닌지 확인
        dbUser.failCount?.let {
            // 로그인 실패 회수 확인 로그
            log.info("Login attempt for user: ${dbUser.userId}, Fail Count: $it")
            if (it >= 5) {
                dbUser.statusName = UserStatus.INACTIVE.name // 5회 이상 실패시 계정 비활성화
                userTbRepo.save(dbUser)
                throw ErrorMessage.LOGIN_FAIL_EXCEED_LIMIT.exception
            }
        }
        //테스트용 암호화된 비번 생성 및 프린트
//        println(justGetGaraPw(dbUser.password!!))

        // 암호화된 비밀번호가 같은지 확인, 같지 않을 경우 failCount 1추가
        if (!isPwEqual(pwVerificationReq.password!!, dbUser.password!!)) {
            dbUser.failCount = dbUser.failCount?.plus(1)
            userTbRepo.save(dbUser)
            throw ErrorMessage.PASSWORD_MISMATCH.exception.putData("failCount", dbUser.failCount!!)
        }

        return if (dbUser.isTwoFactorAuth == "Y") {
            val twoFactorSecret = dbUser.twoFactorSecret ?: throw ErrorMessage.NO_TWO_FACTOR_SECRET.exception
            PasswordVerificationRes(
                twoFactorSecret = AesUtil.encodeAES("${dbUser.userId}:${twoFactorSecret}:${System.currentTimeMillis()}", iv),
                twoFactorAuth = dbUser.isTwoFactorAuth!!,
                qrCodeUrl = null,
                secretKey = null
            )
        } else {
            setUpGoogleAuthentication(dbUser)
        }
    }

    /**
     * 비밀번호 검증
     * @param reqPw
     * @param dbPw
     * @return Boolean
     */
    fun isPwEqual(reqPw:String, dbPw:String):Boolean {
        // 1. Client에서 온 비밀번호를 디코딩
        val decodedPw = try {
            Base64Util.decodeBase64(reqPw)
        } catch (e: Exception) {
            throw ErrorMessage.FAIL_TO_DECODE.exception
        }
        //2. /를 기준ㅡ로 나눠 두번 암호화한 비밀번호와 salt값을 얻음
        val (pw, salt) = decodedPw.split("/")

        // 3. 얻은 salt값으로 프론트와 같은 방식으로 db pw를 암호화
        val decodedDbPw = encodeSHA256("$dbPw/$salt")

        return pw == decodedDbPw
    }
    /**
     * 유저의 상태가 활성화 되었는지 확인
     * @param user
     * @return Boolean
     */
    fun isUserActive(user: UserTb): Boolean {
        return when (user.statusName?.let { UserStatus.getUserStatus(it) }) {
            UserStatus.PENDING -> throw ErrorMessage.NOT_YET_CONFIRM.exception
            UserStatus.INACTIVE -> throw ErrorMessage.INACTIVE_USER.exception
            UserStatus.SUSPENDED -> throw ErrorMessage.LOCK_USER.exception
            UserStatus.WITHDRAWN -> throw ErrorMessage.WITHDRAWN_USER.exception
            UserStatus.DORMANCY -> throw ErrorMessage.DORMANCY_USER.exception
            else -> true
        }
    }
    /**
     * 테스트용 암호화된 비번 생성
     * @param pw
     * @return String
     */
    fun justGetGaraPw(pw: String): String {
        val pwb = encodeSHA256(pw + "/" + "babo")
        val final = Base64Util.encodeBase64(pwb + "/" + "babo")

        return final
    }
    /**
    * 2차 인증 설정
    * @param user
    * @return HashMap
     */
    fun setUpGoogleAuthentication(user: UserTb): PasswordVerificationRes {
        val secret = Base32().encodeToString(encodeSHA256(user.email!!).toByteArray(Charsets.UTF_8).slice(0 until 40).toByteArray())
        val qrUrl = createQRUrl(user.email!!, secret)
        user.twoFactorSecret = secret
        userTbRepo.save(user)
        
        return PasswordVerificationRes(
            twoFactorSecret = AesUtil.encodeAES("${user.userId}:$secret:${System.currentTimeMillis()}", iv),
            twoFactorAuth = user.isTwoFactorAuth!!,
            qrCodeUrl = qrUrl,
            secretKey = secret,
        )
    }

    /**
     * 2차 인증 QR 생성
     * @param email
     * @param secret
     * @return String
     */
    fun createQRUrl(email: String, secret: String): String {
        return "https://qrcode.tec-it.com/API/QRCode?data=otpauth://totp/GPSS$email?secret=$secret"
    }

    /**
     * OTP 검증(2차 로그인. otp발생기에서 발생한 값이 맞는지 검증한다.)
     * @param otpVerificationReq
     * @return AuthenticationRes
     */
    fun verifyOtp(otpVerificationReq: OtpVerificationReq, tokenData: TokenData): FinLogSuccessRes {

        val secret = otpVerificationReq.twoFactorSecret
        val code = otpVerificationReq.code

        val secrets = AesUtil.decodeAES(secret!!, iv).split(":")
        val userId = secrets[0].toLong()

        val dbUser =
            userTbRepo.findOne(UserTb().apply { this.userId = userId })?.copy() ?: throw ErrorMessage.USER_NOT_FOUND.exception

        // 로그인 성공 시 SSE 접속을 위한 토큰 생성.
        val sseToken = AesUtil.encodeAES("${userId}:${dbUser.roleId}", iv)

        // 아래는 슈퍼유저로 간주되는 ID입니다. 새로운 슈퍼유저 ID를 추가하려면 여기에 추가하면 됩니다.
        if (userId == 999L || userId == 989L || userId == 1051L)
            return FinLogSuccessRes(
                userId = userId,
                permissions = ArrayList(),
                isPwChangeRequired = false,
                sseToken = sseToken
            )

        val secretKey = secrets[1]

        // 로그인 실패 회수가 5회 이상이 아닌지 확인
        dbUser.failCount?.let {
            if (it >= 5) {
                dbUser.statusName = UserStatus.INACTIVE.name // 5회 이상 실패시 계정 비활성화
                userTbRepo.save(dbUser)
                throw ErrorMessage.LOGIN_FAIL_EXCEED_LIMIT.exception
            }
        }
        // OTP값 검증
        if (code != null) {
            if (!checkCode(
                    secretKey,
                    code.toLong(),
                    System.currentTimeMillis() / 30000
                )
            ) {
                // 실패시 failCount 1추가
                dbUser.failCount = dbUser.failCount?.plus(1)
                userTbRepo.save(dbUser)
                throw ErrorMessage.OTP_MISMATCH.exception.putData("failCount", dbUser.failCount!!)
            }
        }

        dbUser.isTwoFactorAuth = "Y"
        dbUser.lastLoginAt = LocalDateTime.now(ZoneOffset.UTC)
        //로그인 성공시 failCount 0으로 초기화
        dbUser.failCount = 0
        userTbRepo.save(dbUser)

        val permissions = authService.getRoleMenuListWithM(dbUser.roleId!!.toInt())

        // 로그인 이력 저장
        loginHistoryTbRepo.save(LoginHistoryTb().apply {
            this.name = dbUser.name
            this.email = dbUser.email
            this.loginAt = LocalDateTime.now(ZoneOffset.UTC)
            this.ipAddress = tokenData.ip
            this.roleId = dbUser.roleId!!
        })

        return FinLogSuccessRes(
            userId = userId,
            permissions = permissions,
            isPwChangeRequired = (dbUser.pwChangeRequiredAt?.isBefore(LocalDateTime.now(ZoneOffset.UTC)) == true),
            sseToken = sseToken
        )
    }

    /**
     * 사용자 정보 요청
     */
    fun askUserInfo(tokenData: TokenData): UserInfoRes {
        val userId = tokenData.userId ?: throw ErrorMessage.PERMISSION_DENIED.exception
        return userId.let {
            val user = userTbRepo.findOne(UserTb().apply { this.userId = it })?.copy()
                ?: throw ErrorMessage.USER_NOT_FOUND.exception
            val permissions = authService.getRoleMenuListWithM(user.roleId!!.toInt())
            UserInfoRes(
                userId = user.userId!!,
                userName = user.name!!,
                email = user.email.toString(),
                roleId = user.roleId!!,
                roleName = authService.getRoleNameById(user.roleId!!.toInt())!!,
                permissions = permissions,
                team = user.teamName!!,
                isPwChangeRequired = (user.pwChangeRequiredAt?.isBefore(LocalDateTime.now(ZoneOffset.UTC)) == true)
            )
        }
    }
    /**
     * OTP값 검증(2차 로그인. otp발생기에서 발생한 값이 맞는지 검증한다.)
     * @param secret
     * @param code
     * @return Boolean
     */
    private fun checkCode(secret: String, code: Long, t: Long): Boolean {
        val codec = Base32()
        val decodedKey = codec.decode(secret)
        val hash: Long = verifyCode(decodedKey, t)
        return hash == code
    }

    /**
     * OTP값 검증(2차 로그인. otp발생기에서 발생한 값이 맞는지 검증한다.)
     * @param key
     * @param t
     * @return Long
     */
    fun verifyCode(key: ByteArray, t: Long): Long {
        val data = ByteArray(8)
        var value = t
        run {
            var i = 8
            while (i-- > 0) {
                data[i] = value.toByte()
                value = value ushr 8
            }
        }

        val signKey = SecretKeySpec(key, "HmacSHA1")
        val mac: Mac = Mac.getInstance("HmacSHA1")
        mac.init(signKey)
        val hash: ByteArray = mac.doFinal(data)
        val offset = hash[20 - 1].toInt() and 0xF
        var truncatedHash: Long = 0
        for (i in 0..3) {
            truncatedHash = truncatedHash shl 8
            truncatedHash = truncatedHash or (hash[offset + i].toInt() and 0xFF).toLong()
        }
        truncatedHash = truncatedHash and 0x7FFFFFFFL
        truncatedHash %= 1000000
        return truncatedHash
    }
    // ========================================================================================================================

    /**
     * 로그아웃
     * @return HashMap
     */
    fun logout(tokenData: TokenData): HashMap<String, Long?> {
        val userId = tokenData.userId
        return HashMap<String, Long?>().apply {
            this["userId"] = userId
        }
    }

    /**
     * 토큰 갱신
     */
    fun renewToken(tokenData: TokenData): AuthenticationRes {
        val userId = tokenData.userId ?: throw ErrorMessage.PERMISSION_DENIED.exception
        val dbUser = userTbRepo.findOne(UserTb().apply { this.userId = userId })?.copy()
            ?: throw ErrorMessage.USER_NOT_FOUND.exception
        val permissions = authService.getRoleMenuListWithM(dbUser.roleId!!.toInt())

        return AuthenticationRes(
            userId = userId,
            permissions = permissions,
            isPwChangeRequired = (dbUser.pwChangeRequiredAt?.isBefore(LocalDateTime.now(ZoneOffset.UTC)) == true)
        )
    }
    // ========================================================================================================================

    /**
     * 비밀번호 초기화(재설정)
     * @param pwResetWithOtpReq
     * @return HashMap
     */
    fun resetPassword(pwResetWithOtpReq: PwResetWithOtpReq): HashMap<String, Any> {
        // 입력받은 이메일로 저장된 회원정보 조회
        val user = userTbRepo.findOne(UserTb().apply { this.email = pwResetWithOtpReq.email })?.copy()
            ?: throw ErrorMessage.USER_NOT_FOUND.exception

        // 사용자 상태 활성화 여부 검증
        isUserActive(user)

        val otpCode = pwResetWithOtpReq.code
        // OTP값 검증
        if (otpCode != null) {
            if (!checkCode(user.twoFactorSecret!!, otpCode.toLong(), System.currentTimeMillis() / 30000)) {
                throw ErrorMessage.OTP_MISMATCH.exception
            }
        }
        // 비밀번호 업데이트 하고 저장
        user.password = pwResetWithOtpReq.password
        userTbRepo.save(user)

        return HashMap<String, Any>().apply {
            this["result"] = "Success"
        }
    }

    /**
     * 비밀번호 변경
     * @param changePasswordReq
     * @return HashMap
     */
    fun changePassword(changePasswordReq: ChangePwReq, tokenData: TokenData): HashMap<String, Any> {
        // GW로부터 userid를 받아와서 userTb 조회
        val user = userTbRepo.findOne(UserTb().apply { this.userId = tokenData.userId })
            ?.copy()
            ?: throw ErrorMessage.USER_NOT_FOUND.exception

        // 입력받은 현재 비밀번호와 저장된 비밀번호가 일치하는지 확인
        if(!isPwEqual(changePasswordReq.oldPassword!!, user.password!!)) {
            throw ErrorMessage.PASSWORD_MISMATCH.exception
        }

        val isPwCanBeChange = user.password == changePasswordReq.newPassword

        // 기존 db pw 와 새로 입력시킬 비밀번호가 일치하는지 확인
        if(isPwCanBeChange) throw ErrorMessage.PASSWORD_NEED_DIFFERENCE.exception

        // 새로운 비밀번호로 업데이트 하고
        user.password = changePasswordReq.newPassword
        //비밀번호 변경 필요일을 요청시점 + 180일
        user.pwChangeRequiredAt = LocalDateTime.now(ZoneOffset.UTC).plusDays(180)

        // 저장
        userTbRepo.save(user)

        return HashMap<String, Any>().apply {
            this["result"] = "Success"
        }
    }

    /*
    * 비밀번호 변경 필요일을 요청시점 + 30일 로 변경
     */
    fun deferPwChangeRequiredAt(tokenData: TokenData): HashMap<String, Any> {
        val user = userTbRepo.findOne(UserTb().apply { this.userId = tokenData.userId })?.copy()
            ?: throw ErrorMessage.USER_NOT_FOUND.exception
        user.pwChangeRequiredAt = LocalDateTime.now(ZoneOffset.UTC).plusDays(30)
        userTbRepo.save(user)
        return HashMap<String, Any>().apply {
            this["result"] = "Success"
        }
    }
    // ========================================================================================================================


    /**
     * 내 정보 수정
     * @param userTb
     * @return HashMap
     */
    @Transactional
    fun modifyMyInfo(updateMyInfoReq: UpdateMyInfoReq, tokenData: TokenData): HashMap<String, Any> {
        val user = userTbRepo.findOne(UserTb().apply { this.userId = tokenData.userId })?.copy()?: throw ErrorMessage.USER_NOT_FOUND.exception

        updateMyInfoReq.name?.let {
            user.name = it
        }
        updateMyInfoReq.company?.let{
            user.companyName = it
        }
        updateMyInfoReq.team?.let {
            user.teamName = it
        }
        if(user.roleId == 29L){

            val newlyAddedIpIds = mutableSetOf<Long>()

            // 신규 등록 및 수정 처리
            updateMyInfoReq.userIpList.forEach { userIp ->
                if (userIp.userIpId == null) {
                    // userIpId가 null이면 신규 등록
                    log.info("Registering new IP address '{}' with description '{}' for user ID: {}", userIp.address, userIp.desc, user.userId)
                    // 신규 IP 등록 로직
                    try {
                        if (!isIpValid(userIp.address!!)) throw ErrorMessage.INVALID_IP_ADDRESS.exception
                        val savedIp = ipTbRepo.save(IpTb().apply {
                            this.userId = user.userId
                            this.address = userIp.address
                            userIp.desc?.let {
                                this.description = it
                            }
                            this.registeredAt = LocalDateTime.now(ZoneOffset.UTC)
                        })
                        newlyAddedIpIds.add(savedIp.ipId!!)
                        log.info("Saved IP with ID: ${savedIp.ipId} for user ID: $user.userId")
                    } catch (e: Exception) {
                        log.error("Error saving IP for user ID: $user.userId", e)
                        throw e // 혹은 적절한 예외 처리
                    }

                } else {
                    // userIpId가 null이 아니면 수정
                    log.info("Updating existing IP (ID: {}) to address '{}' with description '{}' for user ID: {}", userIp.userIpId, userIp.address, userIp.desc, user.userId)
                    // 기존 IP 수정 로직
                    val ipTb = ipTbRepo.findOne(IpTb().apply { this.ipId = userIp.userIpId; this.userId = user.userId })?.copy()
                        ?: throw ErrorMessage.IP_NOT_FOUND.exception
                    if (!isIpValid(userIp.address!!)) throw ErrorMessage.INVALID_IP_ADDRESS.exception
                    ipTb.address = userIp.address
                    ipTb.description = userIp.desc
                    ipTb.modifiedAt = LocalDateTime.now(ZoneOffset.UTC)
                    ipTbRepo.save(ipTb)
                }
            }
            // 삭제 처리
            val existingIps = ipTbRepo.findAllByUserId(user.userId!!).map { it.copy() }
            val userIpIdsToUpdateOrAdd = updateMyInfoReq.userIpList.mapNotNull { it.userIpId }.toSet() + newlyAddedIpIds
            existingIps.forEach { existingIp ->
                if (existingIp.ipId !in userIpIdsToUpdateOrAdd) {
                    log.info("Deleting IP (ID: {}) with address '{}' for user ID: {}", existingIp.ipId, existingIp.address, user.userId)
                    ipTbRepo.delete(existingIp)
                }
            }
        }
        userTbRepo.save(user)
        log.info("User info update process completed successfully for user ID: {}", user.userId)
        return HashMap<String, Any>().apply {
            this["response"] = "Success"
        }
    }

    // ========================================================================================================================
    /**
     * 유저 목록 가져오기
     * @param userListReq
     * @return HashMap
     */
    fun getUserList(userListReq: UserListReq): PageListRes<UserTb> {
        val page = userListReq.page ?: 1

        val roleId = if(userListReq.roleId?.toLong() == 0L) null else userListReq.roleId?.toLong()

        val statusName = userListReq.status?.takeIf { it != 0 }?.let {
            UserStatus.fromCode(it)?.name
        }

        val userTb: UserTb = UserTb().apply {
            this.statusName = statusName
            this.roleId = roleId
            this.email = userListReq.email.takeIf { it?.isNotBlank() == true }
            this.name = userListReq.name.takeIf { it?.isNotBlank() == true }
            this.startDate = userListReq.startDate?.takeIf{it.isNotBlank()}?.let{
                try {
                    LocalDateTime.parse(it + "T00:00:00")
                } catch(e: DateTimeParseException){
                    null
                }
            }
            this.endDate = userListReq.endDate?.takeIf{it.isNotBlank()}?.let{
                try {
                    LocalDateTime.parse(it + "T23:59:59")
                } catch(e: DateTimeParseException){
                    null
                }
            }
        }
        val userList = userTbRepo.findAllWithCustomSortAndPageable(UserTbPredicate.searchLike(userTb), PageRequest.of(page-1, 10))
        // 조회된 사용자 목록을 먼저 메모리에 적재
        var userDtoList = userList.content.map { it.copy() }

        // 이제 각 사용자에게 순서 번호 할당
        userDtoList = userDtoList.mapIndexed { index, userEntity ->
            val number = index + 1 + (10 * (page - 1))
            convertToUserTb(userEntity, number)
        }
        return PageListRes(
            totalPages = userList.totalPages,
            currentPage = page,
            pageSize = 10,
            totalElements = userList.totalElements,
            list = userDtoList,
        )
    }

    fun convertToUserTb(userTb: UserTb, number: Int): UserTb{
        userTb.number = number
        userTb.roleName = authService.getRoleNameById(userTb.roleId!!.toInt())!!
        // 상태 이름으로부터 상태 코드를 설정
        userTb.statusName?.let { name ->
            val code = UserStatus.fromName(name)?.code
            userTb.statusCode = code
        }
        // comment 필드가 null이면 빈 문자열로 설정
        userTb.comment = userTb.comment ?: ""
        // 마지막 로그인 시간이 null이면 빈 문자열로 설정
        userTb.lastLoginAtString = userTb.lastLoginAtString ?: ""
        // 등록일시가 null이면 빈 문자열로 설정
        userTb.registeredAtString = userTb.registeredAtString ?: ""
        // 회원 가입 신청일이 null이면 빈 문자열로 설정
        userTb.registeredAtString = userTb.registeredAtString ?: ""
        userTb.name = MaskingUtil.maskName(userTb.name)
        userTb.email = MaskingUtil.maskEmail(userTb.email)
        return userTb
    }

    /**
     * 유저 상세 정보 가져오기
     * @param userId
     * @return userTb
     */
    fun getUserDetail(userId: Long): UserTb {
        val user = userTbRepo.findOne(UserTb().apply { this.userId = userId })?.copy()
            ?: throw ErrorMessage.USER_NOT_FOUND.exception
        user.roleName = authService.getRoleNameById(user.roleId!!.toInt())!!
        return user
    }

    /**
     * 내 정보 가져오기
     * @param userId
     * @return MyInfoResponseDTO
     */
    fun getMyInfo(userId: Long?): MyInfoRes? {
        userId ?: throw ErrorMessage.PERMISSION_DENIED.exception
        val user = userTbRepo.findOne(UserTb().apply { this.userId = userId })?.copy()
            ?: throw ErrorMessage.USER_NOT_FOUND.exception
        var userIpList = emptyList<UserIpList>()
        if(user.roleId == 29L){
            userIpList = ipTbRepo.findByUserId(userId)
                .map {
                    UserIpList(
                        userIpId = it.ipId!!,
                        address = it.address,
                        desc = it.description
                    )
                }
        }

        return MyInfoRes(
            email = user.email!!,
            name = user.name!!,
            company = user.companyName!!,
            team = user.teamName!!,
            roleId = user.roleId!!,
            roleName = authService.getRoleNameById(user.roleId!!.toInt())!!,
            userIpList = userIpList
        )
    }

    /**
     * 비밀번호 초기화 by 관리자
     */
    fun resetPasswordByAdmin(userId: Long): HashMap<String, Any> {
        val user = userTbRepo.findOne(UserTb().apply { this.userId = userId })?.copy()
            ?: throw ErrorMessage.USER_NOT_FOUND.exception
        val password = "|%4ZD@;3?P1(884}"
        user.password = encodeSHA256(password)
        userTbRepo.save(user)

        mailService.sendPwResetResultMail(user.email!!, password)
        return java.util.HashMap<String, Any>().apply {
            this["response"] = "Success"
        }
    }
    /**
     * 회원 로그인 이력 가져오기
     */
    fun getLoginHistory(loginHistoryReq: LoginHistoryReq): PageListRes<LoginHistoryTb> {
        val page = loginHistoryReq.page ?: 1
        val roleId = if(loginHistoryReq.roleId?.toLong() == 0L) null else loginHistoryReq.roleId?.toLong()
        val loginHistoryTb: LoginHistoryTb = LoginHistoryTb().apply {
            this.email = loginHistoryReq.email.takeIf { it?.isNotBlank() == true }
            this.roleId = roleId
            this.startDate = loginHistoryReq.startDate?.takeIf{it.isNotBlank()}?.let{
                try {
                    LocalDateTime.parse(it + "T00:00:00")
                } catch(e: DateTimeParseException){
                    null
                }
            }
            this.endDate = loginHistoryReq.endDate?.takeIf{it.isNotBlank()}?.let{
                try {
                    LocalDateTime.parse(it + "T23:59:59")
                } catch(e: DateTimeParseException){
                    null
                }
            }
        }
        val loginHistoryList = loginHistoryTbRepo.findAll(
                LoginHistoryTbPredicate.searchLike(loginHistoryTb),
                PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "loginAt"))).map { it.copy() }
        val loginHistoryTbList = loginHistoryList.content.map {
            val number = loginHistoryList.content.indexOf(it) + 1 + (10 * (page -1))
            convertToLoginHistoryTb(it.copy(), number)
        }
        return PageListRes(
            totalPages = loginHistoryList.totalPages,
            currentPage = page,
            pageSize = 10,
            totalElements = loginHistoryList.totalElements,
            list = loginHistoryTbList,
        )
    }

    fun convertToLoginHistoryTb(loginHistoryTb: LoginHistoryTb, number: Int): LoginHistoryTb {
        loginHistoryTb.number = number
        loginHistoryTb.roleName = authService.getRoleNameById(loginHistoryTb.roleId!!.toInt())!!
        loginHistoryTb.name = MaskingUtil.maskName(loginHistoryTb.name)
        loginHistoryTb.email = MaskingUtil.maskEmail(loginHistoryTb.email)
        loginHistoryTb.ipAddress = MaskingUtil.maskIpAddress(loginHistoryTb.ipAddress)
        return loginHistoryTb
    }

    /**
     * 회원 상태 변경( 회원가입 승인, 회원 탈퇴, 회원 정지 등)
     * @param changeUserStatusReq
     * @return HashMap
     */
    fun changeUserStatus(userId: Long, changeUserStatusReq: ChangeUserStatusReq): HashMap<String, Any> {
        val user = UserTb().apply {
            this.userId = userId
            this.statusName = changeUserStatusReq.status?.uppercase()
        }
        // 제공된 상태값이 Status enum인지 확인
        UserStatus.getUserStatus(user.statusName!!)?.let {
            user.statusName = it.name
        }?: throw ErrorMessage.STATUS_NOT_FOUND.exception


        userTbRepo.findOne(UserTb().apply { this.userId = user.userId })?.copy()
            ?.let {
                // 만약 사용자의 이전 상태가 PENIDNG이고 새로운 상태가 ACTIVE이면 approvedAt 필드를 설정
                if (it.statusName == UserStatus.PENDING.name && user.statusName == UserStatus.ACTIVE.name) {
                    it.approvedAt = LocalDateTime.now(ZoneOffset.UTC)
                    mailService.sendApprovalMail(it.email!!)
                    println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>승인 메일 전송 완료")
                }
                it.statusName = user.statusName
                userTbRepo.save(it)
            } ?: throw ErrorMessage.USER_NOT_FOUND.exception
        return HashMap<String, Any>().apply {
            this["response"] = "Success"
        }
    }

    /**
     * 회원 comment 변경
     * @param userId
     * @return HashMap
     */
    fun changeUserComment(userId: Long, changeUserCommentReq: ChangeUserCommentReq): HashMap<String, Any> {
        val user = UserTb().apply {
            this.userId = userId
            this.comment = changeUserCommentReq.comment
        }
        userTbRepo.findOne(UserTb().apply { this.userId = user.userId })?.copy()
            ?.let {
                it.comment = user.comment
                userTbRepo.save(it)
            } ?: throw ErrorMessage.USER_NOT_FOUND.exception
        return HashMap<String, Any>().apply {
            this["response"] = "Success"
        }
    }

    // Test User Delete Func
    fun deleteTestUser(userId: Long) {
        userTbRepo.deleteById(userId)
    }

    /**
     * 사용자 아이디 리스트 by roleId
     * @param roleId: Long
     * @return HashMap<String, List<Long>>
     */
    fun getUserIdsByRole(roleId: Long): HashMap<String, List<Long>> {

        val inRoleId = if(roleId == 0L) null else roleId

        if (inRoleId != null) {
            if (!roleTbRepo.existsById(inRoleId.toInt())) {
                throw ErrorMessage.ROLE_NOT_FOUND.exception
            }
        }

        val userIdList = userTbRepo.findAll(UserTbPredicate.search(UserTb().apply {
            this.roleId = inRoleId
        })).map { it.copy() }

        val userIds = userIdList.filter { it.roleId != 30L && (it.statusName == "ACTIVE" || it.statusName == "INACTIVE") }
            .map { it.userId!! }

        return HashMap<String, List<Long>>().apply {
            this["userIds"] = userIds
        }
    }

    /**
     * 전체 사용자 아이디 리스트
     * @return HashMap<String, List<Long>>
     */
    fun getAllUserIds(): HashMap<String, List<Long>> {

        val userList = userTbRepo.findAll(UserTbPredicate.search(UserTb()))

        val userIds = userList.filter { it.roleId != 30L && (it.statusName == "ACTIVE" || it.statusName == "INACTIVE") }
            .map { it.userId!! }

        return HashMap<String, List<Long>>().apply {
            this["userIds"] = userIds
        }
    }
}


