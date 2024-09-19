package com.love.timi.data.dto.response

data class PeopleByName(
    val people: List<Person>? = null
)

data class Person(
    val userId: Long? = null,
    val email: String? = null,
    val name: String? = null,
    val company: String? = null,
    val team: String? = null,
)
