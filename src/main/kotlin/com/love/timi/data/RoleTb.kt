package com.love.timi.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.annotations.Expose
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "role_tb", schema = "user_auth")
class RoleTb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    @Expose
    var roleId: Long? = null
   
    @Column(name = "name")
    @Expose
    var name: String? = null
   
    @Column(name = "description")
    @Expose
    var description: String? = null
   
    @Transient
    @Expose
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
    var modifiedAtString: String? = null
    @Column(name = "modified_at")
    @JsonIgnore
    var modifiedAt: LocalDateTime? = null
        set(value) {
            field = value
            this.modifiedAtString = value?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

   fun copy(): RoleTb {
       return RoleTb().apply {
       this.roleId = this@RoleTb.roleId
       this.name = this@RoleTb.name
       this.description = this@RoleTb.description
       this.registeredAt = this@RoleTb.registeredAt
       this.modifiedAt = this@RoleTb.modifiedAt
       }
   }
}
