package com.love.timi.data.repo

import com.love.timi.data.RoleChangeHistoryTb
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import com.love.timi.data.predicate.RoleChangeHistoryTbPredicate

interface RoleChangeHistoryTbRepo: JpaRepository<RoleChangeHistoryTb, Int>, QuerydslPredicateExecutor<RoleChangeHistoryTb> {

}

fun RoleChangeHistoryTbRepo.findOne(roleChangeHistoryTb: RoleChangeHistoryTb): RoleChangeHistoryTb? = findOne(RoleChangeHistoryTbPredicate.search(roleChangeHistoryTb)).orElse(null)
