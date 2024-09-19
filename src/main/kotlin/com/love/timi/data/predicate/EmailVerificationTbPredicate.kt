package com.love.timi.data.predicate

import com.love.timi.data.EmailVerificationTb
import com.love.timi.data.QEmailVerificationTb
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate

class EmailVerificationTbPredicate {

   companion object {
        fun search(data: EmailVerificationTb): Predicate {
            val _data: QEmailVerificationTb = QEmailVerificationTb.emailVerificationTb
            val builder = BooleanBuilder()
               data.email?.let { builder.and(_data.email.eq(data.email)) }
               data.verificationCode?.let { builder.and(_data.verificationCode.eq(data.verificationCode)) }
               data.createdAt?.let { builder.and(_data.createdAt.eq(data.createdAt)) }
               data.expiresAt?.let { builder.and(_data.expiresAt.eq(data.expiresAt)) }
               data.isVerified?.let { builder.and(_data.isVerified.eq(data.isVerified)) }
        return builder
       }
    }
}
