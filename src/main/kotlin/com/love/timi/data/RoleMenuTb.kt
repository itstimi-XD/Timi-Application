package com.love.timi.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.annotations.Expose
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "role_menu_tb", schema = "user_auth")
@IdClass(RoleMenuTbId::class) // 복합 키 식별자 클래스 지정
class RoleMenuTb {

    @Id
    @JsonIgnore
    @Column(name = "role_id")
    var roleId: Long? = null

    @Id
    @Column(name = "menu_id")
    @Expose
    var menuId: Long? = null
   
    @Column(name = "approval_yn")
    @Expose
    var approvalYn: String? = null
   
    @Column(name = "create_yn")
    @Expose
    var createYn: String? = null
   
    @Column(name = "read_yn")
    @Expose
    var readYn: String? = null
   
    @Column(name = "update_yn")
    @Expose
    var updateYn: String? = null
   
    @Column(name = "delete_yn")
    @Expose
    var deleteYn: String? = null
   
    @Transient
    @JsonIgnore
    var registeredAtString: String? = null
    @Column(name = "registered_at")
    @JsonIgnore
    var registeredAt: LocalDateTime? = null
        set(value) {
            field = value
            this.registeredAtString = value?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

    @Transient
    @JsonIgnore
    var modifiedAtString: String? = null
    @Column(name = "modified_at")
    @JsonIgnore
    var modifiedAt: LocalDateTime? = null
        set(value) {
            field = value
            this.modifiedAtString = value?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

    @Column(name = "show_yn")
    @JsonIgnore
    var showYn: String? = null

   fun copy(): RoleMenuTb {
       return RoleMenuTb().apply {
       this.roleId = this@RoleMenuTb.roleId
       this.menuId = this@RoleMenuTb.menuId
       this.approvalYn = this@RoleMenuTb.approvalYn
       this.createYn = this@RoleMenuTb.createYn
       this.readYn = this@RoleMenuTb.readYn
       this.updateYn = this@RoleMenuTb.updateYn
       this.deleteYn = this@RoleMenuTb.deleteYn
       this.registeredAt = this@RoleMenuTb.registeredAt
       this.modifiedAt = this@RoleMenuTb.modifiedAt
       this.showYn = this@RoleMenuTb.showYn
       }
   }
}


