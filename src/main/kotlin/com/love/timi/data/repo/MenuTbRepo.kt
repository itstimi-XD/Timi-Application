package com.love.timi.data.repo

import com.love.timi.data.MenuTb
import com.love.timi.data.predicate.MenuTbPredicate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor


interface MenuTbRepo: JpaRepository<MenuTb, Int>, QuerydslPredicateExecutor<MenuTb> {
    fun findByRootMenuIdIsNull(): List<MenuTb>
}
fun MenuTbRepo.findOne(menuTb: MenuTb): MenuTb? =  findOne(MenuTbPredicate.search(menuTb)).orElse(null)
