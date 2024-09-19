package com.love.timi.util

class RandomCode {

    fun randomSixCode(wishLength: Int): Int{
        var code = ""
        for(i in 1..wishLength){
            code += (1..9).random()
        }
        return code.toInt()
    }
}