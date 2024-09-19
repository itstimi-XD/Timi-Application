package com.love.timi.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.annotations.Expose
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*


@Entity
@Table(name = "menu_tb", schema = "user_auth")
class MenuTb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    @Expose
    @field:Schema(description = "Menu ID")
    var menuId: Long? = null

    @Column(name = "name")
    @Expose
    @field:Schema(description = "Name")
    var name: String? = null

    @Column(name = "menu_order")
    @Expose
    @field:Schema(description = "Menu Order")
    var menuOrder: Long? = null

    @Column(name = "root_menu_id")
    @Expose
    @field:Schema(description = "Root Menu ID")
    var rootMenuId: Long? = null

    @Column(name = "menu_url")
    @Expose
    @field:Schema(description = "Menu URL")
    var menuUrl: String? = null

    @Column(name = "icon_name")
    @JsonIgnore
    var iconName: String? = null
   
    @Column(name = "label")
    @Expose
    @field:Schema(description = "Label")
    var label: String? = null

    @Transient
    @Expose
    @field:Schema(description = "하위메뉴")
    var children: MutableList<MenuTb>? = null

    @Transient
    @Expose
    @field:Schema(description = "권한")
    var permission: RoleMenuTb? = null

    fun copy(): MenuTb {
        return MenuTb().apply {
            this.menuId = this@MenuTb.menuId
            this.name = this@MenuTb.name
            this.menuOrder = this@MenuTb.menuOrder
            this.rootMenuId = this@MenuTb.rootMenuId
            this.menuUrl = this@MenuTb.menuUrl
            this.iconName = this@MenuTb.iconName
            this.label = this@MenuTb.label
        }
    }
}
