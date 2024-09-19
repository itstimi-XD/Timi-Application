package com.love.timi

import com.love.timi.data.EmailVerificationTb
import com.love.timi.data.IpTb
import com.love.timi.data.TokenData
import com.love.timi.data.UserTb
import com.love.timi.data.dto.request.*
import com.love.timi.data.dto.response.PasswordVerificationRes
import com.love.timi.service.v2.AuthService
import com.love.timi.service.v2.UserService
import org.apache.commons.codec.binary.Base32
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class HyundaiUserAuthApplicationTests() {
	@Autowired lateinit var userService: UserService
	@Autowired lateinit var authService: AuthService

	val tokenData = TokenData(
		userId = 1038,
		permissions = arrayOf("M101A", "M101C", "M101R", "M101U", "M101D", "M111A", "M111C", "M111R", "M111U", "M111D", "M171A", "M171C", "M171R", "M171U", "M171D", "M163A", "M163C", "M163R", "M163U", "M163D", "M181A", "M181C", "M181R", "M181U", "M181D", "M182A", "M182C", "M182R", "M182U", "M182D", "M183A", "M183C", "M183R", "M183U", "M183D", "M141A", "M141C", "M141R", "M141U", "M141D", "M161A", "M161C", "M161R", "M161U", "M161D", "M184A", "M184C", "M184R", "M184U", "M184D", "M151A", "M151C", "M151R", "M151U", "M151D", "M152A", "M152C", "M152R", "M152U", "M152D", "M154A", "M154C", "M154R", "M154U", "M154D", "M162A", "M162C", "M162R", "M162U", "M162D", "M121A", "M121C", "M121R", "M121U", "M121D", "M164A", "M164C", "M164R", "M164U", "M164D", "M185A", "M185C", "M185R", "M185U", "M185D", "M142A", "M142C", "M142R", "M142U", "M142D", "M153A", "M153C", "M153R", "M153U", "M153D", "M143A", "M143C", "M143R", "M143U", "M143D", "M144A", "M144C", "M144R", "M144U", "M144D", "M131A", "M131C", "M131R", "M131U", "M131D").joinToString(","),
		ip = "58.87.61.222",
		transactionId = "User-Mgmt-Junit-Transaction"
	)

	@Test
	fun contextLoads() {
		// User
		val verifiedEmail: EmailVerificationTb
		val user: UserTb
		val twoFactor: PasswordVerificationRes
		try {verifiedEmail = verifyEmailApi()} catch(e: Exception) { println("userService.verifyEmail Failed"); throw e }
		try {confirmEmailApi(verifiedEmail.verificationCode!!)} catch(e: Exception) { println("userService.confirmEmail Failed"); throw e }
		try {getIpApi()} catch(e: Exception) { println("userService.getIpApi Failed"); throw e }
		try {user = registerApi()} catch(e: Exception) { println("userService.register Failed"); throw e }
		val newTokenData = TokenData(user.userId, tokenData.permissions, tokenData.ip, tokenData.transactionId)
		try {changeUserStatusApi(user.userId!!)} catch(e: Exception) { println("userService.changeUserStatus Failed"); throw e }
		try {twoFactor = loginApi(user, newTokenData)} catch(e: Exception) { println("userService.login Failed"); throw e }
		try {verifyOtpApi(twoFactor, newTokenData)} catch(e: Exception) { println("userService.verifyOtp Failed"); throw e }
		try {askUserInfoApi()} catch(e: Exception) { println("userService.askUserInfo Failed"); throw e }
		try {renewTokenApi()} catch(e: Exception) { println("userService.renewToken Failed"); throw e }
		try {logoutApi()} catch(e: Exception) { println("userService.logout Failed"); throw e }
		try {changePasswordApi(newTokenData)} catch(e: Exception) { println("userService.changePassword Failed"); throw e }
		try {resetPasswordApi(user, twoFactor.secretKey!!)} catch(e: Exception) { println("userService.resetPassword Failed"); throw e }
		try {deferPwChangeRequiredAtApi()} catch(e: Exception) { println("userService.deferPwChangeRequiredAt Failed"); throw e }
		try {getMyInfoApi()} catch(e: Exception) { println("userService.getMyInfo Failed"); throw e }
		try {modifyMyInfoApi(newTokenData)} catch(e: Exception) { println("userService.modifyMyInfo Failed"); throw e }

		try {getUserListApi()} catch(e: Exception) { println("userService.getUserList Failed"); throw e }
		try {getUserDetailApi(user.userId!!)} catch(e: Exception) { println("userService.getUserDetail Failed"); throw e }
		try {resetPasswordByAdminApi(user.userId!!)} catch(e: Exception) { println("userService.resetPasswordByAdmin Failed"); throw e }
		try {changeUserCommentApi(user.userId!!)} catch(e: Exception) { println("userService.changeUserComment Failed"); throw e }
		try {getLoginHistoryApi()} catch(e: Exception) { println("userService.getLoginHistory Failed"); throw e }

		// Auth
		val ip: IpTb
		try {changeUserRoleApi(user.userId!!)} catch(e: Exception) { println("authService.changeUserRole Failed"); throw e }
		try {getRoleChangeHistoryApi()} catch(e: Exception) { println("authService.getRoleChangeHistory Failed"); throw e }
		try {getRoleMenuApi()} catch(e: Exception) { println("authService.getRoleMenu Failed"); throw e }
		try {modifyRoleMenuApi()} catch(e: Exception) { println("authService.modifyRoleMenu Failed"); throw e }
		try {getMenuTreeApi()} catch(e: Exception) { println("authService.getMenuTree Failed"); throw e }
		try {getFullMenuTreeApi()} catch(e: Exception) { println("authService.getFullMenuTree Failed"); throw e }
		try {getRoleMenuPermissionListApi()} catch(e: Exception) { println("authService.getRoleMenuPermissionList Failed"); throw e }
		try {getRoleChangeUserListApi()} catch(e: Exception) { println("authService.getRoleChangeUserList Failed"); throw e }
		try {getReviewerSearchApi()} catch(e: Exception) { println("authService.getReviewerSearch Failed"); throw e }
		try {getManagerSearchApi()} catch(e: Exception) { println("authService.getManagerSearch Failed"); throw e }
		try {ip = addIpApi(user.userId!!)} catch(e: Exception) { println("authService.addIp Failed"); throw e }
		try {getIpListApi()} catch(e: Exception) { println("authService.getIpList Failed"); throw e }
		try {updateIpApi(ip.ipId!!)} catch(e: Exception) { println("authService.updateIp Failed"); throw e }
		try {deleteIpApi(ip.ipId!!)} catch(e: Exception) { println("authService.deleteIp Failed"); throw e }

		// Delete Test User
		try {userService.deleteTestUser(user.userId!!)} catch (e: Exception) { println("authService.deleteTestUser Failed"); throw e }
	}

	// Auth
	private fun changeUserRoleApi(userId: Long) {
		authService.changeUserRole(userId, ChangeUserRoleReq(30, null))
		println("authService.changeUserRole Success")
	}
	private fun getRoleChangeHistoryApi() {
		authService.getRoleChangeHistory(RoleChangeHistoryReq(null, null, null, null, null, null))
		println("authService.getRoleChangeHistory Success")
	}
	private fun getRoleMenuApi() {
		authService.getRoleMenu(ChangeMenuPermissionListReq(25, null))
		println("authService.getRoleMenu Success")
	}
	private fun modifyRoleMenuApi() {
		authService.modifyRoleMenu(PermissionPerMenuReq("30", null))
		println("authService.modifyRoleMenu Success")
	}
	private fun getMenuTreeApi() {
		authService.getMenuTree(tokenData)
		println("authService.getMenuTree Success")
	}
	private fun getFullMenuTreeApi() {
		authService.getFullMenuTree()
		println("authService.getFullMenuTree Success")
	}
	private fun getRoleMenuPermissionListApi() {
		authService.getRoleMenuPermissionList()
		println("authService.getRoleMenuPermissionList Success")
	}
	private fun getRoleChangeUserListApi() {
		authService.getRoleChangeUserList(RoleChangeUserListReq(null, null, null, null))
		println("authService.getRoleChangeUserList Success")
	}
	private fun getReviewerSearchApi() {
		authService.getReviewerSearch(FindUserByNameReq("검", null))
		println("authService.getReviewerSearch Success")
	}
	private fun getManagerSearchApi() {
		authService.getManagerSearch(FindUserByNameReq("승", null))
		println("authService.getManagerSearch Success")
	}
	private fun addIpApi(userId: Long): IpTb {
		authService.changeUserRole(userId, ChangeUserRoleReq(29, null))
		val ipData = authService.addIp(IpAddReq(userId, tokenData.ip!!, null))
		println("authService.addIp Success")
		return ipData["ip"] as IpTb
	}
	private fun getIpListApi() {
		authService.getIpList(IpListReq(null, null, null, null))
		println("authService.getIpList Success")
	}
	private fun updateIpApi(ipId: Long) {
		authService.updateIp(IpUpdateReq(ipId, tokenData.ip, "JUNIT"))
		println("authService.updateIp Success")
	}
	private fun deleteIpApi(ipId: Long) {
		authService.deleteIp(IpDeleteReq(ipId))
		println("authService.deleteIp Success")
	}


	// User
	private fun verifyEmailApi(): EmailVerificationTb {
		val emailData = userService.verifyEmail(EmailSendReq("JUNITTEST@hyundai.com", null))
		println("userService.verifyEmail Success")
		return emailData["verifiedEmail"] as EmailVerificationTb
	}
	private fun confirmEmailApi(code: String) {
		userService.confirmEmail(EmailVerificationReq("JUNITTEST@hyundai.com", code))
		println("userService.confirmEmail Success")
	}
	private fun getIpApi() {
		userService.getIp(tokenData)
		println("userService.getIp Success")
	}
	private fun registerApi(): UserTb {
		val user = userService.register(
			RegisterReq(
				"JUNITTEST@hyundai.com",
				"ab255e4dc9b59ff3d7d14090f3ca4df5ef0000df2b1beec2067fda12aee464d1",
				"JUNIT",
				"JUNIT",
				"JUNIT",
				25,
				listOf(Term(1, "0.1", "Y")),
				null
			)
		)
		println("userService.register Success")
		return user["user"] as UserTb
	}
	// 승인부터
	private fun changeUserStatusApi(userId: Long) {
		userService.changeUserStatus(userId, ChangeUserStatusReq("ACTIVE", null))
		println("userService.changeUserStatus Success")
	}
	private fun loginApi(user: UserTb, newToken: TokenData): PasswordVerificationRes {
		val twoFactorData = userService.login(PwVerificationReq(user.email, "MDhjYTZhMjc0Y2YzYzBlN2YzNmVlNzM0ZGEwM2FkNWVkMzYyZTY2MTNmMTVjZWE0YzUxZGE4ZmI3Y2VhMzNlZC8xNzE3MTM2NTI0MzUw"), newToken)
		println("userService.login Success")
		return twoFactorData
	}
	// OTP code 생성 함수
	private fun generateOtpCode(secret: String): String {
		val decodedKey = Base32().decode(secret)
		val hash: Long = userService.verifyCode(decodedKey, System.currentTimeMillis() / 30000)
		println("CTP HASH >> $hash")
		return hash.toString()
	}
	private fun verifyOtpApi(twoFactor: PasswordVerificationRes, newToken: TokenData) {
		userService.verifyOtp(OtpVerificationReq(twoFactor.twoFactorSecret, generateOtpCode(twoFactor.secretKey!!)), newToken)
		println("userService.verifyOtp Success")
	}
	private fun askUserInfoApi() {
		userService.askUserInfo(tokenData)
		println("userService.askUserInfo Success")
	}
	private fun renewTokenApi() {
		userService.renewToken(tokenData)
		println("userService.renewToken Success")
	}
	private fun logoutApi() {
		userService.logout(tokenData)
		println("userService.logout Success")
	}
	private fun changePasswordApi(newToken: TokenData) {
		userService.changePassword(ChangePwReq("MDhjYTZhMjc0Y2YzYzBlN2YzNmVlNzM0ZGEwM2FkNWVkMzYyZTY2MTNmMTVjZWE0YzUxZGE4ZmI3Y2VhMzNlZC8xNzE3MTM2NTI0MzUw", "NDA3OTdjM2Y1ZTgzZTI2YTQ2NzZjYWJkYjE2Mzc2MjQ2OWNmYmU4NDgyOTIwMzk0OWNjZmM2YzRmZDY3NTk0Ni8xNzE3MTM2NjczMDA1"), newToken)
		println("userService.changePassword Success")
	}
	private fun resetPasswordApi(user: UserTb, secret: String) {
		userService.resetPassword(PwResetWithOtpReq(user.email, "d2579a756e10403fdac3592ea8469ca82c003f5b0bad0784d0cf299d57cc0596", generateOtpCode(secret)))
		println("userService.resetPassword Success")
	}
	private fun deferPwChangeRequiredAtApi() {
		userService.deferPwChangeRequiredAt(tokenData)
		println("userService.deferPwChangeRequiredAt Success")
	}
	private fun getMyInfoApi() {
		userService.getMyInfo(tokenData.userId)
		println("userService.getMyInfo Success")
	}
	private fun modifyMyInfoApi(newToken: TokenData) {
		userService.modifyMyInfo(UpdateMyInfoReq("JUNIT2", "JUNIT2", "JUNIT2", listOf()), newToken)
		println("userService.modifyMyInfo Success")
	}

	// 유저관리
	private fun getUserListApi() {
		userService.getUserList(UserListReq(null, null, null, null, null, null, null))
		println("userService.getUserList Success")
	}
	private fun getUserDetailApi(userId: Long) {
		userService.getUserDetail(userId)
		println("userService.getUserDetail Success")
	}
	private fun resetPasswordByAdminApi(userId: Long) {
		userService.resetPasswordByAdmin(userId)
		println("userService.resetPasswordByAdmin Success")
	}
	private fun changeUserCommentApi(userId: Long) {
		userService.changeUserComment(userId, ChangeUserCommentReq("JUNIT-TEST", null))
		println("userService.changeUserComment Success")
	}
	private fun getLoginHistoryApi() {
		userService.getLoginHistory(LoginHistoryReq(null, null, null, null, null))
		println("userService.getLoginHistory Success")
	}
}
