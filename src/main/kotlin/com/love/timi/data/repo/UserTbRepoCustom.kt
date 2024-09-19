package com.love.timi.data.repo

import com.love.timi.data.UserTb
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import com.querydsl.core.types.Predicate

interface UserTbRepoCustom {
    fun findAllWithCustomSortAndPageable(predicate: Predicate, pageable: Pageable): Page<UserTb>
}
