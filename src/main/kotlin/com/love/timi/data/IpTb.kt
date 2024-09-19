package com.love.timi.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.annotations.Expose
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "ip_tb", schema = "user_auth")
class IpTb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ip_id")
    var ipId: Long? = null
   
    @Column(name = "user_id")
    var userId: Long? = null

    @Column(name = "address")
    @Expose
    var address: String? = null
   
    @Column(name = "description")
    @Expose
    var description: String? = null
   
    @Transient
    var registeredAtString: String? = null
    @Column(name = "registered_at")
    @JsonIgnore
    var registeredAt: LocalDateTime? = null
        set(value) {
            field = value
            this.registeredAtString = value?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

    @Transient
    var modifiedAtString: String? = null
    @Column(name = "modified_at")
    @JsonIgnore
    var modifiedAt: LocalDateTime? = null
        set(value) {
            field = value
            this.modifiedAtString = value?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

   fun copy(): IpTb {
       return IpTb().apply {
       this.ipId = this@IpTb.ipId
       this.userId = this@IpTb.userId
       this.address = this@IpTb.address
       this.description = this@IpTb.description
       this.registeredAt = this@IpTb.registeredAt
       this.modifiedAt = this@IpTb.modifiedAt
       }
   }
}
