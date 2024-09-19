package com.love.timi.exception

import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/error")
class NotFoundController: AbstractErrorController(DefaultErrorAttributes()) {
    @RequestMapping
    fun error(): String {
        throw ErrorMessage.NOT_FOUND.exception
    }
}