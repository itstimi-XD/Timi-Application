package com.love.timi.data.repo

import com.love.timi.data.IpTb
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import com.love.timi.data.predicate.IpTbPredicate

interface IpTbRepo: JpaRepository<IpTb, Int>, QuerydslPredicateExecutor<IpTb> {
    fun deleteByUserId(userId: Long)
    fun findByUserId(userId: Long): List<IpTb>
    fun findAllByUserId(userId: Long): List<IpTb>
}

fun IpTbRepo.findOne(ipTb: IpTb): IpTb? = findOne(IpTbPredicate.search(ipTb)).orElse(null)
