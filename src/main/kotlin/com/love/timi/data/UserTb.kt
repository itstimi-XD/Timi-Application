package com.love.timi.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.annotations.Expose
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "user_tb", schema = "user_auth")
class UserTb {

    @Transient
    @Expose
    @field:Schema(description = "번호")
    var number: Int? = null

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @Expose
    var userId: Long? = null
   
    @Column(name = "email")
    @Expose
    @field:Schema(description = "이메일")
    var email: String? = null
   
    @Column(name = "password")
    @JsonIgnore
    var password: String? = null
   
    @Column(name = "name")
    @Expose
    @field:Schema(description = "이름")
    var name: String? = null

    @Column(name = "company_name")
    @Expose
    @field:Schema(description = "회사명")
    var companyName: String? = null

    @Column(name = "team_name")
    @Expose
    @field:Schema(description = "팀명")
    var teamName: String? = null

    @Transient
    @Expose
    @field:Schema(description = "회원가입 신청일")
    var registeredAtString: String? = null

    @Column(name = "registered_at")
    @JsonIgnore
    var registeredAt: LocalDateTime? = null
        set(value) {
            field = value
            this.registeredAtString = value?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

    @Transient
    @Expose
    @field:Schema(description = "회원가입 승인일")
    var approvedAtString: String? = null

    @Column(name = "approved_at")
    @JsonIgnore
    var approvedAt: LocalDateTime? = null
        set(value) {
            field = value
            this.approvedAtString = value?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

    @Transient
    @Expose
    @field:Schema(description = "최종 로그인일")
    var lastLoginAtString: String? = null

    @Column(name = "last_login_at")
    @JsonIgnore
    var lastLoginAt: LocalDateTime? = null
        set(value) {
            field = value
            this.lastLoginAtString = value?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

    @Column(name = "role_id")
    @Expose
    @field:Schema(description = "역할(권한) 번호")
    var roleId: Long? = null

    @Transient
    @Expose
    @field:Schema(description = "역할(권한) 이름")
    var roleName: String? = null

    @Column(name = "comment")
    @Expose
    @field:Schema(description = "비고")
//    @JsonInclude(JsonInclude.Include.ALWAYS)
    var comment: String? = null
   
    @Column(name = "fail_count")
    @Expose
    @field:Schema(description = "로그인 실패 횟수")
    var failCount: Long? = null
   
    @Column(name = "two_factor_secret")
    @JsonIgnore
    var twoFactorSecret: String? = null


    @Transient
    @Expose
    @field:Schema(description = "사용자 계정 상태")
    var statusCode: Int? = null

    @Column(name = "status_name")
    @Expose
    @field:Schema(description = "사용자 계정 상태")
    var statusName: String? = null
   
    @Column(name = "terms_accepted")
    @Expose
    @field:Schema(description = "약관 동의 여부")
    var termsAccepted: String? = null

    @Column(name = "is_two_factor_auth")
    @Expose
    @field:Schema(description = "2단계 인증 활성화 여부")
    var isTwoFactorAuth: String? = null

    @Transient
    @JsonIgnore
    var startDate: LocalDateTime? = null

    @Transient
    @JsonIgnore
    var endDate: LocalDateTime? = null

    @Transient
    @Expose
    @field:Schema(description = "비밀번호 변경 필요 여부")
    var pwChangeRequiredAtString: String? = null

    @Column(name = "pw_change_required_at")
    @JsonIgnore
    var pwChangeRequiredAt: LocalDateTime? = null
        set(value) {
            field = value
            this.pwChangeRequiredAtString = value?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

    fun copy(): UserTb {
       return UserTb().apply {
       this.userId = this@UserTb.userId
       this.email = this@UserTb.email
       this.password = this@UserTb.password
       this.name = this@UserTb.name
       this.registeredAt = this@UserTb.registeredAt
       this.approvedAt = this@UserTb.approvedAt
       this.lastLoginAt = this@UserTb.lastLoginAt
       this.comment = this@UserTb.comment
       this.failCount = this@UserTb.failCount
       this.twoFactorSecret = this@UserTb.twoFactorSecret
       this.companyName = this@UserTb.companyName
       this.teamName = this@UserTb.teamName
       this.statusName = this@UserTb.statusName
       this.termsAccepted = this@UserTb.termsAccepted
       this.roleId = this@UserTb.roleId
       this.isTwoFactorAuth = this@UserTb.isTwoFactorAuth
       this.pwChangeRequiredAt = this@UserTb.pwChangeRequiredAt
       }
   }
}
