package com.love.timi.data.repo;

import com.love.timi.data.RoleTb
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import com.love.timi.data.predicate.RoleTbPredicate

interface RoleTbRepo: JpaRepository<RoleTb, Int>, QuerydslPredicateExecutor<RoleTb> {

}

fun RoleTbRepo.findOne(roleTb: RoleTb): RoleTb? = findOne(RoleTbPredicate.search(roleTb)).orElse(null)
