package com.love.timi.data.repo

import com.love.timi.data.LoginHistoryTb
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import com.love.timi.data.predicate.LoginHistoryTbPredicate

interface LoginHistoryTbRepo: JpaRepository<LoginHistoryTb, Int>, QuerydslPredicateExecutor<LoginHistoryTb> {

}

fun LoginHistoryTbRepo.findOne(loginHistoryTb: LoginHistoryTb): LoginHistoryTb? = findOne(LoginHistoryTbPredicate.search(loginHistoryTb)).orElse(null)
