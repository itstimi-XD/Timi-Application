package com.love.timi.data.predicate

import com.love.timi.data.MenuTb
import com.love.timi.data.QMenuTb
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate


open class MenuTbPredicate {

   companion object {
       fun search(data: MenuTb): Predicate {
            val _data: QMenuTb = QMenuTb.menuTb
            val builder = BooleanBuilder()
            data.menuId?.let { builder.and(_data.menuId.eq(data.menuId)) }
            data.name?.let { builder.and(_data.name.eq(data.name)) }
            data.menuOrder?.let { builder.and(_data.menuOrder.eq(data.menuOrder)) }
            data.rootMenuId?.let { builder.and(_data.rootMenuId.eq(data.rootMenuId)) }
            data.menuUrl?.let { builder.and(_data.menuUrl.eq(data.menuUrl)) }
            data.iconName?.let { builder.and(_data.iconName.eq(data.iconName)) }
            data.label?.let { builder.and(_data.label.eq(data.label)) }
            return builder
        }
    }
}
