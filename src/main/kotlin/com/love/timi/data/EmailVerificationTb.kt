package com.love.timi.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.annotations.Expose
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "email_verification_tb", schema = "user_auth")
class EmailVerificationTb {

    @Id
    @Column(name = "email")
    @Expose
    var email: String? = null
   
    @Column(name = "verification_code")
    @Expose
    var verificationCode: String? = null
   
    @Transient
    @Expose
    var createdAtString: String? = null
    @Column(name = "created_at")
    @JsonIgnore
    var createdAt: LocalDateTime? = null
        set(value) {
            field = value
            this.createdAtString = value?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

    @Transient
    @Expose
    var expiresAtString: String? = null
    @Column(name = "expires_at")
    @JsonIgnore
    var expiresAt: LocalDateTime? = null
        set(value) {
            field = value
            this.expiresAtString = value?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

    @Column(name = "is_verified")
    @Expose
    var isVerified: String? = null
   
   fun copy(): EmailVerificationTb {
       return EmailVerificationTb().apply {
       this.email = this@EmailVerificationTb.email
       this.verificationCode = this@EmailVerificationTb.verificationCode
       this.createdAt = this@EmailVerificationTb.createdAt
       this.expiresAt = this@EmailVerificationTb.expiresAt
       this.isVerified = this@EmailVerificationTb.isVerified
       }
   }
}
