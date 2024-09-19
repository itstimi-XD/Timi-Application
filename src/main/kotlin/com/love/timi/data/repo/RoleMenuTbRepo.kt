package com.love.timi.data.repo;

import com.love.timi.data.RoleMenuTb
import com.love.timi.data.predicate.RoleMenuTbPredicate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface RoleMenuTbRepo: JpaRepository<RoleMenuTb, Int>, QuerydslPredicateExecutor<RoleMenuTb> {
    fun findByRoleId(roleId: Int): List<RoleMenuTb>
    fun findByRoleIdAndShowYn(roleId: Long, showYn: String): List<RoleMenuTb>
    fun findByRoleIdAndMenuId(roleId: Long, menuId: Long): RoleMenuTb?
    fun findByMenuIdAndApprovalYn(menuId: Long, approvalYn: String): List<RoleMenuTb> // 새로운 메서드 추가
}

fun RoleMenuTbRepo.findOne(roleMenuTb: RoleMenuTb): RoleMenuTb? = findOne(RoleMenuTbPredicate.search(roleMenuTb)).orElse(null)
