package com.love.timi.data.predicate

import com.love.timi.data.RoleMenuTb
import com.love.timi.data.QRoleMenuTb
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate

class RoleMenuTbPredicate {

   companion object {
        fun search(data: RoleMenuTb): Predicate {
            val _data: QRoleMenuTb = QRoleMenuTb.roleMenuTb
            val builder = BooleanBuilder()
               data.roleId?.let { builder.and(_data.roleId.eq(data.roleId)) }
               data.menuId?.let { builder.and(_data.menuId.eq(data.menuId)) }
               data.approvalYn?.let { builder.and(_data.approvalYn.eq(data.approvalYn)) }
               data.createYn?.let { builder.and(_data.createYn.eq(data.createYn)) }
               data.readYn?.let { builder.and(_data.readYn.eq(data.readYn)) }
               data.updateYn?.let { builder.and(_data.updateYn.eq(data.updateYn)) }
               data.deleteYn?.let { builder.and(_data.deleteYn.eq(data.deleteYn)) }
               data.registeredAt?.let { builder.and(_data.registeredAt.eq(data.registeredAt)) }
               data.modifiedAt?.let { builder.and(_data.modifiedAt.eq(data.modifiedAt)) }
               data.showYn?.let { builder.and(_data.showYn.eq(data.showYn)) }
        return builder
       }
    }
}
