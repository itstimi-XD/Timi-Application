package com.love.timi.data.predicate

import com.love.timi.data.RoleTb
import com.love.timi.data.QRoleTb
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate

class RoleTbPredicate {

   companion object {
        fun search(data: RoleTb): Predicate {
            val _data: QRoleTb = QRoleTb.roleTb
            val builder = BooleanBuilder()
               data.roleId?.let { builder.and(_data.roleId.eq(data.roleId)) }
               data.name?.let { builder.and(_data.name.eq(data.name)) }
               data.description?.let { builder.and(_data.description.eq(data.description)) }
               data.registeredAt?.let { builder.and(_data.registeredAt.eq(data.registeredAt)) }
               data.modifiedAt?.let { builder.and(_data.modifiedAt.eq(data.modifiedAt)) }
        return builder
       }
    }
}
