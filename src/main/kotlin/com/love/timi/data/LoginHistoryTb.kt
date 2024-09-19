package com.love.timi.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.annotations.Expose
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "login_history_tb", schema = "user_auth")
class LoginHistoryTb {

    @Transient
    @Expose
    var number: Int? = null

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_history_id")
    var loginHistoryId: Long? = null
   
    @Column(name = "name")
    @Expose
    var name: String? = null
   
    @Column(name = "role_id")
    @Expose
    var roleId: Long? = null
    @Transient
    @Expose
    var roleName: String? = null

    @Column(name = "ip_address")
    @Expose
    var ipAddress: String? = null
   
    @Transient
    @Expose
    var loginAtString: String? = null
    @Column(name = "login_at")
    @JsonIgnore
    var loginAt: LocalDateTime? = null
        set(value) {
            field = value
            this.loginAtString = value?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

    @Column(name = "email")
    @Expose
    var email: String? = null

    @Transient
    var startDate: LocalDateTime? = null

    @Transient
    var endDate: LocalDateTime? = null
   fun copy(): LoginHistoryTb {
       return LoginHistoryTb().apply {
       this.loginHistoryId = this@LoginHistoryTb.loginHistoryId
       this.name = this@LoginHistoryTb.name
       this.roleId = this@LoginHistoryTb.roleId
       this.ipAddress = this@LoginHistoryTb.ipAddress
       this.loginAt = this@LoginHistoryTb.loginAt
       this.email = this@LoginHistoryTb.email
       }
   }
}
