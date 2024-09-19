package com.love.timi.controller.v2

import com.love.timi.auth.AuthType
import com.love.timi.controller.common.BaseController
import com.love.timi.data.LoginHistoryTb
import com.love.timi.data.UserTb
import com.love.timi.data.dto.request.*
import com.love.timi.data.dto.response.*
import com.love.timi.response.RestResponse
import com.love.timi.service.v2.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Tag(name = "User Controller V2", description = "사용자 관련 컨트롤러입니다.")
@RestController("UserControllerV2")
@RequestMapping("/user-auth/api/v2/user")
class UserController: BaseController() {

    @Autowired lateinit var userService: UserService


    // ========================================================================================================================
    // 회원가입
    // ========================================================================================================================

    /**
     * 인증메일 발송
     */
    @Operation(summary ="이메일 가입여부 확인 및 인증메일 발송", description = "이메일 가입여부 확인 및 인증메일 발송 endpoint")
    @ApiResponse(responseCode = "200", description = "이메일 가입여부 확인 및 인증메일 발송 성공")
    @RequestMapping(value = ["/verify-email"], method = [RequestMethod.POST])
    fun verifyEmail(@Valid @RequestBody emailSendReq: EmailSendReq, bindingResult: BindingResult): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(userService.verifyEmail(emailSendReq)).responseEntity()
    }
    /**
     * 인증메일 내의 인증번호 확인
     */
    @Operation(summary ="인증메일 내의 인증번호 확인", description = "인증메일 내의 인증번호 확인 endpoint")
    @ApiResponse(responseCode = "200", description = "인증메일 내의 인증번호 확인 성공")
    @RequestMapping(value = ["/confirm-email"], method = [RequestMethod.POST])
    fun confirmEmail(@Valid @RequestBody emailVerificationReq: EmailVerificationReq, bindingResult: BindingResult): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(userService.confirmEmail(emailVerificationReq)).responseEntity()
    }
    /**
     * ip주소 가져오기
     */
    @Operation(summary ="ip주소 가져오기", description = "ip주소 가져오기 endpoint")
    @ApiResponse(responseCode = "200", description = "ip주소 가져오기 성공")
    @RequestMapping(value = ["/get-ip"], method = [RequestMethod.GET])
    fun getIpAddr(): ResponseEntity<RestResponse<UserIpRes>> {
        return RestResponse<UserIpRes>().ok().setBody(userService.getIp(getTokenData())).responseEntity()
    }
    /**
     * 회원가입
     */
    @Operation(summary ="회원가입", description = "회원가입 endpoint")
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @RequestMapping(value = ["/register"], method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun register(@Valid @RequestBody registerReq: RegisterReq, bindingResult: BindingResult): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(userService.register(registerReq)).responseEntity()
    }

    // ========================================================================================================================
    // 로그인
    // ========================================================================================================================

    /**
     * 로그인(1차 로그인. id, pw를 검증한다.)
     */
    @Operation(summary ="로그인(1차 로그인. id, pw를 검증한다.)", description = "로그인(1차 로그인. id, pw를 검증한다.) endpoint")
    @ApiResponse(responseCode = "200", description = "1차 로그인 성공")
    @RequestMapping(value = ["/login"], method = [RequestMethod.POST])
    fun login(@Valid @RequestBody pwVerificationReq: PwVerificationReq, bindingResult: BindingResult): ResponseEntity<RestResponse<PasswordVerificationRes>> {
        val loginResponse = userService.login(pwVerificationReq, getTokenData())
        return RestResponse<PasswordVerificationRes>().ok().setBody(loginResponse).responseEntity()
    }

    /**
     * OTP값 검증(2차 로그인. otp발생기에서 발생한 값이 맞는지 검증한다.)
     */
    @Operation(summary ="OTP값 검증(2차 로그인. otp발생기에서 발생한 값이 맞는지 검증한다.)", description = "OTP값 검증(2차 로그인. otp발생기에서 발생한 값이 맞는지 검증한다.) endpoint")
    @ApiResponse(responseCode = "200", description = "OTP값 검증 성공")
    @RequestMapping(value = ["/verify-otp"], method = [RequestMethod.POST])
    fun verifyOtp(@Valid @RequestBody otpVerificationReq: OtpVerificationReq, bindingResult: BindingResult): ResponseEntity<RestResponse<FinLogSuccessRes>> {
        return RestResponse<FinLogSuccessRes>().ok().setBody(userService.verifyOtp(otpVerificationReq, getTokenData())).responseEntity()
    }

    /**
     * 사용자 정보 요청
     */
    @Operation(summary ="사용자 정보 요청", description = "사용자 정보 요청 endpoint")
    @RequestMapping(value = ["/ask-user-info"], method = [RequestMethod.GET])
    @AuthType([AuthType.Authority.MAIN_MENU_READ])
    fun askInfo(): ResponseEntity<RestResponse<UserInfoRes>> {
        return RestResponse<UserInfoRes>().ok().setBody(userService.askUserInfo(getTokenData())).responseEntity()
    }

    // ========================================================================================================================

    /**
     * 로그아웃
     */
    @Operation(summary ="로그아웃", description = "로그아웃 endpoint")
    @RequestMapping(value = ["/logout"], method = [RequestMethod.POST])
    @AuthType([AuthType.Authority.MAIN_MENU_READ])
    fun logout(): ResponseEntity<RestResponse<HashMap<String, Long?>>> {
        return RestResponse<HashMap<String, Long?>>().ok().setBody(userService.logout(getTokenData())).responseEntity()
    }

    /**
     * 토큰 갱신
     */
    @Operation(summary ="토큰 갱신", description = "토큰 갱신 endpoint")
    @RequestMapping(value = ["/renew-token"], method = [RequestMethod.POST])
    fun renewToken(): ResponseEntity<RestResponse<AuthenticationRes>> {
        return RestResponse<AuthenticationRes>().ok().setBody(userService.renewToken(getTokenData())).responseEntity()
    }
    // ========================================================================================================================
    // 로그인 > 비밀번호 초기화(재설정)
    // ========================================================================================================================

    /**
     * 비밀번호 초기화(재설정) with OTP [로그인 > 비밀번호 초기화]
     */
    @Operation(summary ="비밀번호 초기화(재설정) with OTP [로그인 > 비밀번호 초기화]", description = "비밀번호 초기화 endpoint")
    @RequestMapping(value = ["/password/reset"], method = [RequestMethod.POST])
    fun passwordReset(@Valid @RequestBody pwResetWithOtpReq: PwResetWithOtpReq, bindingResult: BindingResult): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(userService.resetPassword(pwResetWithOtpReq)).responseEntity()
    }

    /**
     * 비밀번호 변경 - 로그인 후 [홈 > 비밀번호 변경]
     */
    @Operation(summary ="비밀번호 변경 - 로그인 후 [홈 > 비밀번호 변경] ", description = "비밀번호 변경 endpoint")
    @AuthType([AuthType.Authority.MAIN_MENU_READ])
    @RequestMapping(value = ["/password/change"], method = [RequestMethod.POST])
    fun changePassword(@Valid @RequestBody changePwReq: ChangePwReq, bindingResult: BindingResult): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(userService.changePassword(changePwReq, getTokenData())).responseEntity()
    }

    /**
     * 비밀번호 변경 필요일을 요청시점 + 30일로 변경 [홈 > 비밀번호 변경]
     */
    @Operation(summary ="비밀번호 변경 필요일을 요청시점 + 30일로 변경 [홈 > 비밀번호 변경]", description = "비밀번호 변경 필요일을 요청시점 + 30일로 변경 endpoint")
    @AuthType([AuthType.Authority.MAIN_MENU_READ])
    @RequestMapping(value = ["/password/defer-change"], method = [RequestMethod.POST])
    fun deferChangePassword(): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(userService.deferPwChangeRequiredAt(getTokenData())).responseEntity()
    }

    // ========================================================================================================================
    // 마이페이지 > 내 정보
    // ========================================================================================================================
    /**
     * 내 정보 가져오기
     */
    @Operation(summary ="내 정보 가져오기", description = "내 정보 가져오기 endpoint")
    @RequestMapping(value = ["/my-info"], method = [RequestMethod.GET])
    @AuthType([AuthType.Authority.MAIN_MENU_READ])
    fun getMyInfo(): ResponseEntity<RestResponse<MyInfoRes?>> {
        return RestResponse<MyInfoRes?>().ok().setBody(userService.getMyInfo(getTokenData().userId)).responseEntity()
    }
    /**
     * 회원정보 수정
     */
    @Operation(summary ="내 정보 수정", description = "내 정보 수정 endpoint")
    @RequestMapping(value = ["/my-info"], method = [RequestMethod.PUT])
    @AuthType([AuthType.Authority.MAIN_MENU_READ])
    fun updateMyInfo(@Valid @RequestBody updateMyInfoReq: UpdateMyInfoReq, bindingResult: BindingResult): ResponseEntity<out Any> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(userService.modifyMyInfo(updateMyInfoReq, getTokenData())).responseEntity()
    }

    // ========================================================================================================================
    // 유저관리 > 유저 목록
    // ========================================================================================================================

    /**
     * 유저 목록 가져오기 [유저 관리 > 유저 목록]
     */
    @Operation(summary ="유저 목록 가져오기 [유저 관리 > 유저 목록]", description = "유저 목록 가져오기 endpoint")
    @AuthType([AuthType.Authority.USER_LIST_READ])
    @RequestMapping(value = ["/get-user-list"], method = [RequestMethod.GET])
    fun getUserList(@Valid @ParameterObject userListReq: UserListReq, bindingResult: BindingResult): ResponseEntity<RestResponse<PageListRes<UserTb>>> {
        return RestResponse<PageListRes<UserTb>>().ok().setBody(userService.getUserList(userListReq)).responseEntity()
    }

    /**
     * 유저 상세 정보 가져오기 [유저 관리 > 유저 목록 - 상세], [유저 관리 > IP 접근관리 - IP 등록]
     */
    @Operation(summary ="유저 상세 정보 가져오기 [유저 관리 > 유저 목록 - 상세]", description = "유저 상세 정보 가져오기 endpoint")
    @AuthType([AuthType.Authority.USER_LIST_READ, AuthType.Authority.USER_IP_MANAGEMENT_CREATE])
    @RequestMapping(value = ["/{userId}/detail"], method = [RequestMethod.GET])
    fun getUserDetail(@PathVariable("userId") userId: Long): ResponseEntity<RestResponse<UserTb>> {
        return RestResponse<UserTb>().ok().setBody(userService.getUserDetail(userId)).responseEntity()
    }

    /**
     * 비밀번호 초기화 by 관리자 [유저 관리 > 유저 목록]
     */
    @Operation(summary ="비밀번호 초기화 by 관리자 [유저 관리 > 유저 목록]", description = "비밀번호 초기화 by 관리자 endpoint")
    @AuthType([AuthType.Authority.USER_LIST_UPDATE])
    @RequestMapping(value = ["/{userId}/reset-password"], method = [RequestMethod.PUT])
    fun resetPasswordByAdmin(@PathVariable("userId") userId: Long): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(userService.resetPasswordByAdmin(userId)).responseEntity()
    }

    /**
     * 회원 상태 변경( 회원가입 승인, 회원 탈퇴, 회원 정지 등)
     */
    @Operation(summary ="회원가입 승인, 회원 상태 변경", description = "회원가입 승인, 회원 상태 변경 endpoint")
    @AuthType([AuthType.Authority.USER_LIST_APPROVE, AuthType.Authority.USER_LIST_UPDATE])
    @RequestMapping(value = ["/{userId}/status"], method = [RequestMethod.PUT])
    fun changeUserStatus(@PathVariable("userId") userId: Long, @Valid @RequestBody changeUserStatusReq: ChangeUserStatusReq, bindingResult: BindingResult ): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(userService.changeUserStatus(userId, changeUserStatusReq)).responseEntity()
    }

    /**
     * 회원 comment 변경
     */
    @Operation(summary ="회원 comment 변경", description = "회원 comment 변경 endpoint")
    @AuthType([AuthType.Authority.USER_LIST_UPDATE])
    @RequestMapping(value = ["/{userId}/comment"], method = [RequestMethod.PUT])
    fun changeUserComment(@PathVariable("userId") userId: Long, @Valid @RequestBody changeUserCommentReq: ChangeUserCommentReq, bindingResult: BindingResult ): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        return RestResponse<HashMap<String, Any>>().ok().setBody(userService.changeUserComment(userId, changeUserCommentReq)).responseEntity()
    }

    /**
     * 회원 로그인 이력 목록 가져오기
     */
    @Operation(summary ="회원 로그인 이력 목록 가져오기", description = "회원 로그인 이력 목록 가져오기 endpoint")
    @AuthType([AuthType.Authority.USER_LOGIN_HISTORY_READ])
    @RequestMapping(value = ["/get-login-history"], method = [RequestMethod.GET])
    fun getLoginHistory(@Valid @ParameterObject loginHistoryReq: LoginHistoryReq, bindingResult: BindingResult): ResponseEntity<RestResponse<PageListRes<LoginHistoryTb>>> {
        return RestResponse<PageListRes<LoginHistoryTb>>().ok().setBody(userService.getLoginHistory(loginHistoryReq)).responseEntity()
    }

    // ========================================================================================================================
    // Common Service > 유저 Id 목록
    // ========================================================================================================================
    /**
     * 사용자 아이디 리스트 by roleId
     */
    @Operation(summary = "user Id list search by role Id", description = "사용자 아이디 리스트를 역할 아이디로 검색할 수 있는 API")
    @RequestMapping(value = ["/id-list/by-role"], method = [RequestMethod.GET])
    fun getUserIdsByRole(@RequestParam roleId: Long): ResponseEntity<RestResponse<HashMap<String, List<Long>>>> {
        return RestResponse<HashMap<String, List<Long>>>().ok().setBody(userService.getUserIdsByRole(roleId)).responseEntity()
    }

    /**
     * 전체 사용자 아이디 리스트
     */
    @Operation(summary = "Overall userId List", description = "전체 사용자 아이디 리스트를 반환하는 API")
    @RequestMapping(value = ["/id-list/all"], method = [RequestMethod.GET])
    fun getAllUserIds(): ResponseEntity<RestResponse<HashMap<String, List<Long>>>> {
        return RestResponse<HashMap<String, List<Long>>>().ok().setBody(userService.getAllUserIds()).responseEntity()
    }

}