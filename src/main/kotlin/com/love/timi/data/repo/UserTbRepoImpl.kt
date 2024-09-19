package com.love.timi.data.repo

import com.love.timi.data.QUserTb
import com.love.timi.data.UserTb
import com.love.timi.data.predicate.UserTbPredicate
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import com.querydsl.core.types.Predicate
import org.springframework.transaction.annotation.Transactional


open class UserTbRepoImpl : UserTbRepoCustom {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional(readOnly = true)
    override fun findAllWithCustomSortAndPageable(predicate: Predicate, pageable: Pageable): Page<UserTb> {
        val queryFactory = JPAQueryFactory(entityManager)

        // 리스트 조회 쿼리
        val content = queryFactory
                .selectFrom(QUserTb.userTb)
                .where(predicate)
                .orderBy(*UserTbPredicate.orderByStatusAndLastLogin())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch() // 결과 리스트 가져오기

        // 카운트 쿼리
        val count = queryFactory
                .select(QUserTb.userTb.count())
                .from(QUserTb.userTb)
                .where(predicate)
                .fetchOne() ?: 0L // 총 개수 가져오기, 결과가 null인 경우를 대비하여 0L로 대체

        return PageImpl(content, pageable, count)
    }
}
