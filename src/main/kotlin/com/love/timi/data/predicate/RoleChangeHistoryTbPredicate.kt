package com.love.timi.data.predicate

import com.love.timi.data.RoleChangeHistoryTb
import com.love.timi.data.QRoleChangeHistoryTb
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate

open class RoleChangeHistoryTbPredicate {

   companion object {
        fun search(data: RoleChangeHistoryTb): Predicate {
            val _data: QRoleChangeHistoryTb = QRoleChangeHistoryTb.roleChangeHistoryTb
            val builder = BooleanBuilder()
               data.roleChangeHistoryId?.let { builder.and(_data.roleChangeHistoryId.eq(data.roleChangeHistoryId)) }
               data.userId?.let { builder.and(_data.userId.eq(data.userId)) }
               data.afterRoleId?.let { builder.and(_data.afterRoleId.eq(data.afterRoleId)) }
               data.changedAt?.let { builder.and(_data.changedAt.eq(data.changedAt)) }
               data.email?.let { builder.and(_data.email.eq(data.email)) }
               data.name?.let { builder.and(_data.name.eq(data.name)) }
               data.changeHistory?.let { builder.and(_data.changeHistory.eq(data.changeHistory)) }
        return builder
       }

         fun searchLike(data: RoleChangeHistoryTb) : Predicate {
              val _data: QRoleChangeHistoryTb = QRoleChangeHistoryTb.roleChangeHistoryTb
              val builder = BooleanBuilder()
              data.email?.let { builder.and(_data.email.like(data.email)) }
              data.name?.let { builder.and(_data.name.like(data.name)) }
              data.afterRoleId?.let { builder.and(_data.afterRoleId.eq(data.afterRoleId)) }
             // 등록일 범위 검색 조건 추가
             when {
                 data.startDate != null && data.endDate != null -> {
                     builder.and(_data.changedAt.between(data.startDate, data.endDate))
                 }
                 data.startDate != null -> {
                     builder.and(_data.changedAt.goe(data.startDate))
                 }
                 data.endDate != null -> {
                     builder.and(_data.changedAt.loe(data.endDate))
                 }
             }
//              // 변경일 범위 검색 조건 추가
//              data.startDate?.let { startDate ->
//                data.endDate?.let { endDate ->
//                     builder.and(_data.changedAt.between(startDate, endDate))
//                }
//              }
              return builder
         }
    }
}
