package com.love.timi.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.annotations.Expose
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "role_change_history_tb", schema = "user_auth")
class RoleChangeHistoryTb {

    @Expose
    @Transient
    var number: Int? = null

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_change_history_id")
    var roleChangeHistoryId: Long? = null
   
    @Column(name = "user_id")
    var userId: Long? = null

    @Expose
    @Column(name = "after_role_id")
    var afterRoleId: Long? = null

    @Expose
    @Transient
    var afterRoleName: String? = null

    @Transient
    @Expose
    var changedAtString: String? = null
    @Column(name = "changed_at")
    @JsonIgnore
    var changedAt: LocalDateTime? = null
        set(value) {
            field = value
            this.changedAtString = value?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

    @Column(name = "email")
    @Expose
    var email: String? = null
   
    @Column(name = "name")
    @Expose
    var name: String? = null
   
    @Column(name = "change_history")
    @Expose
    var changeHistory: String? = null

    @Transient
    var startDate: LocalDateTime? = null

    @Transient
    var endDate: LocalDateTime? = null
   fun copy(): RoleChangeHistoryTb {
       return RoleChangeHistoryTb().apply {
       this.roleChangeHistoryId = this@RoleChangeHistoryTb.roleChangeHistoryId
       this.userId = this@RoleChangeHistoryTb.userId
       this.afterRoleId = this@RoleChangeHistoryTb.afterRoleId
       this.changedAt = this@RoleChangeHistoryTb.changedAt
       this.email = this@RoleChangeHistoryTb.email
       this.name = this@RoleChangeHistoryTb.name
       this.changeHistory = this@RoleChangeHistoryTb.changeHistory
       }
   }
}
