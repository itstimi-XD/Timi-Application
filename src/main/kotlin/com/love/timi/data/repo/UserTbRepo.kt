package com.love.timi.data.repo

import com.love.timi.data.UserTb
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import com.love.timi.data.predicate.UserTbPredicate

interface UserTbRepo: JpaRepository<UserTb, Long>, QuerydslPredicateExecutor<UserTb>, UserTbRepoCustom {
    fun countByRoleId(roleId: Long): Int
    fun searchByRoleId(roleId: Long): List<UserTb>
}

fun UserTbRepo.findOne(userTb: UserTb): UserTb? = findOne(UserTbPredicate.search(userTb)).orElse(null)
