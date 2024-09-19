package com.love.timi.data.repo;

import com.love.timi.data.EmailVerificationTb
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import com.love.timi.data.predicate.EmailVerificationTbPredicate

interface EmailVerificationTbRepo: JpaRepository<EmailVerificationTb, Int>, QuerydslPredicateExecutor<EmailVerificationTb> {

}

fun EmailVerificationTbRepo.findOne(emailVerificationTb: EmailVerificationTb): EmailVerificationTb? = findOne(EmailVerificationTbPredicate.search(emailVerificationTb)).orElse(null)
