package com.love.timi.data.predicate

import com.love.timi.data.IpTb
import com.love.timi.data.QIpTb
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate

open class IpTbPredicate {

   companion object {
        fun search(data: IpTb): Predicate {
            val _data: QIpTb = QIpTb.ipTb
            val builder = BooleanBuilder()
               data.ipId?.let { builder.and(_data.ipId.eq(data.ipId)) }
               data.userId?.let { builder.and(_data.userId.eq(data.userId)) }
               data.address?.let { builder.and(_data.address.eq(data.address)) }
               data.description?.let { builder.and(_data.description.eq(data.description)) }
               data.registeredAt?.let { builder.and(_data.registeredAt.eq(data.registeredAt)) }
               data.modifiedAt?.let { builder.and(_data.modifiedAt.eq(data.modifiedAt)) }
        return builder
       }
         fun searchLike(data: IpTb) : Predicate {
             val _data: QIpTb = QIpTb.ipTb
             val builder = BooleanBuilder()
             data.address?.let { builder.and(_data.address.like("%$it%")) }
             return builder
         }
    }
}
