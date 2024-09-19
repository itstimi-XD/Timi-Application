package com.love.timi.data.predicate

import com.love.timi.data.QUserTb
import com.love.timi.data.UserTb
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.OrderSpecifier

open class UserTbPredicate {

   companion object {
        fun search(data: UserTb): Predicate {
            val _data: QUserTb = QUserTb.userTb
            val builder = BooleanBuilder()
               data.userId?.let { builder.and(_data.userId.eq(data.userId)) }
               data.email?.let { builder.and(_data.email.eq(data.email)) }
               data.password?.let { builder.and(_data.password.eq(data.password)) }
               data.name?.let { builder.and(_data.name.eq(data.name)) }
               data.registeredAt?.let { builder.and(_data.registeredAt.eq(data.registeredAt)) }
               data.approvedAt?.let { builder.and(_data.approvedAt.eq(data.approvedAt)) }
               data.lastLoginAt?.let { builder.and(_data.lastLoginAt.eq(data.lastLoginAt)) }
               data.comment?.let { builder.and(_data.comment.eq(data.comment)) }
               data.failCount?.let { builder.and(_data.failCount.eq(data.failCount)) }
               data.twoFactorSecret?.let { builder.and(_data.twoFactorSecret.eq(data.twoFactorSecret)) }
               data.companyName?.let { builder.and(_data.companyName.eq(data.companyName)) }
               data.teamName?.let { builder.and(_data.teamName.eq(data.teamName)) }
               data.statusName?.let { builder.and(_data.statusName.eq(data.statusName)) }
               data.termsAccepted?.let { builder.and(_data.termsAccepted.eq(data.termsAccepted)) }
               data.roleId?.let { builder.and(_data.roleId.eq(data.roleId)) }
               data.isTwoFactorAuth?.let { builder.and(_data.isTwoFactorAuth.eq(data.isTwoFactorAuth)) }
               data.pwChangeRequiredAt?.let { builder.and(_data.pwChangeRequiredAt.eq(data.pwChangeRequiredAt)) }
            return builder
       }

       fun searchLike(data: UserTb) : Predicate {
           val _data: QUserTb = QUserTb.userTb
           val builder = BooleanBuilder()

           // 항상 roleId가 30이 아닌 사용자만 필터링
           builder.and(_data.roleId.ne(30L))

           data.email?.let { builder.and(_data.email.like("%$it%")) }
           data.name?.let { builder.and(_data.name.like("%$it%")) }
           data.companyName?.let { builder.and(_data.companyName.like("%$it%")) }
           data.teamName?.let { builder.and(_data.teamName.like("%$it%")) }
           // 가입일 범위 검색 조건 추가
           when {
               data.startDate != null && data.endDate != null -> {
                   builder.and(_data.registeredAt.between(data.startDate, data.endDate))
               }
               data.startDate != null -> {
                   builder.and(_data.registeredAt.goe(data.startDate))
               }
               data.endDate != null -> {
                   builder.and(_data.registeredAt.loe(data.endDate))
               }
           }
           data.approvedAt?.let { builder.and(_data.approvedAt.eq(data.approvedAt)) }
           data.lastLoginAt?.let { builder.and(_data.lastLoginAt.eq(data.lastLoginAt)) }
           data.roleId?.let { builder.and(_data.roleId.eq(data.roleId)) }
           data.statusName?.let { builder.and(_data.statusName.eq(data.statusName)) }
           return builder
       }

       fun searchLike(data: UserTb, userIdList: List<Long?>) : Predicate {
           val _data: QUserTb = QUserTb.userTb
           val builder = BooleanBuilder()
           // userIdList가 비어있지 않으면 userIdList 에 해당하는 userId 만 검색
           if (userIdList.isNotEmpty()) {
               builder.and(_data.userId.`in`(userIdList))
           }
           data.roleId?.let { builder.and(_data.roleId.eq(data.roleId)) }
           data.email?.let { builder.and(_data.email.like("%$it%")) }
           data.name?.let { builder.and(_data.name.like("%$it%")) }
           data.statusName?.let { builder.and(_data.statusName.eq(data.statusName)) }
           return builder
       }

       fun searchLikeWithRoleIds(data: UserTb, roleIds: List<Long>): Predicate {
           val _data: QUserTb = QUserTb.userTb
           val builder = BooleanBuilder()

           // 항상 roleId가 30이 아닌 사용자만 필터링
           builder.and(_data.roleId.ne(30L))

           if (roleIds.isNotEmpty()) {
               builder.and(_data.roleId.`in`(roleIds))
           }

           data.email?.let { builder.and(_data.email.like("%$it%")) }
           data.name?.let { builder.and(_data.name.like("%$it%")) }
           data.companyName?.let { builder.and(_data.companyName.like("%$it%")) }
           data.teamName?.let { builder.and(_data.teamName.like("%$it%")) }
           // 가입일 범위 검색 조건 추가
           when {
               data.startDate != null && data.endDate != null -> {
                   builder.and(_data.registeredAt.between(data.startDate, data.endDate))
               }
               data.startDate != null -> {
                   builder.and(_data.registeredAt.goe(data.startDate))
               }
               data.endDate != null -> {
                   builder.and(_data.registeredAt.loe(data.endDate))
               }
           }
           data.approvedAt?.let { builder.and(_data.approvedAt.eq(data.approvedAt)) }
           data.lastLoginAt?.let { builder.and(_data.lastLoginAt.eq(data.lastLoginAt)) }
           data.statusName?.let { builder.and(_data.statusName.eq(data.statusName)) }
           return builder
       }


       /**
        * `statusName`이 "PENDING"인 사용자를 우선하여 정렬하고, 나머지 사용자들은 `lastLoginAt`으로 내림차순 정렬하는 정렬 조건 반환
        * `lastLoginAt`이 null인 경우는 맨 마지막으로 정렬하는 정렬 조건 반환
        */
       fun orderByStatusAndLastLogin(): Array<OrderSpecifier<*>> {
           val _data: QUserTb = QUserTb.userTb

           // "PENDING" 상태를 우선시하는 정렬 조건
           val orderByStatus = Expressions.stringTemplate("CASE WHEN {0} = 'PENDING' THEN 0 ELSE 1 END", _data.statusName).asc()

           // `lastLoginAt`이 null인 경우를 처리하는 정렬 조건
           val orderByNullLastLoginAt = Expressions.numberTemplate(Integer::class.java, "CASE WHEN {0} IS NULL THEN 1 ELSE 0 END", _data.lastLoginAt).asc()

           // `lastLoginAt`으로 내림차순 정렬
           val orderByLastLoginAtDesc = _data.lastLoginAt.desc()

           return arrayOf(orderByStatus, orderByNullLastLoginAt, orderByLastLoginAtDesc)
       }
    }
}
