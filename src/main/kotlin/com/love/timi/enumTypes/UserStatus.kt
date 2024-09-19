package com.love.timi.enumTypes

enum class UserStatus(val code: Int) {
    PENDING(35),
    ACTIVE(36),
    INACTIVE(37),
    WITHDRAWN(38),
    DORMANCY(39),
    SUSPENDED(40);

    companion object {
        fun fromCode(code: Int): UserStatus? = entries.firstOrNull { it.code == code }
        fun fromName(name: String): UserStatus? = entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
        fun getUserStatus(name: String): UserStatus? = entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
}
