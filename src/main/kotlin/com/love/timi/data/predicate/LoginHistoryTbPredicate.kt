package com.love.timi.data.predicate

import com.love.timi.data.LoginHistoryTb
import com.love.timi.data.QLoginHistoryTb
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate

open class LoginHistoryTbPredicate {

   companion object {
        fun search(data: LoginHistoryTb): Predicate {
            val _data: QLoginHistoryTb = QLoginHistoryTb.loginHistoryTb
            val builder = BooleanBuilder()
            data.loginHistoryId?.let { builder.and(_data.loginHistoryId.eq(data.loginHistoryId)) }
            data.name?.let { builder.and(_data.name.eq(data.name)) }
            data.roleId?.let { builder.and(_data.roleId.eq(data.roleId)) }
            data.ipAddress?.let { builder.and(_data.ipAddress.eq(data.ipAddress)) }
            data.loginAt?.let { builder.and(_data.loginAt.eq(data.loginAt)) }
            data.email?.let { builder.and(_data.email.eq(data.email)) }
            return builder
        }
       fun searchLike(data: LoginHistoryTb) : Predicate {
           val _data: QLoginHistoryTb = QLoginHistoryTb.loginHistoryTb
           val builder = BooleanBuilder()

           // 항상 roleId가 30이 아닌 사용자만 필터링
           builder.and(_data.roleId.ne(30L))

           data.email?.let { builder.and(_data.email.like("%$it%")) }
           data.roleId?.let { builder.and(_data.roleId.eq(data.roleId)) }

           // 등록일 범위 검색 조건 추가
           when {
               data.startDate != null && data.endDate != null -> {
                   builder.and(_data.loginAt.between(data.startDate, data.endDate))
               }
               data.startDate != null -> {
                   builder.and(_data.loginAt.goe(data.startDate))
               }
               data.endDate != null -> {
                   builder.and(_data.loginAt.loe(data.endDate))
               }
           }
           return builder
       }
    }
}
