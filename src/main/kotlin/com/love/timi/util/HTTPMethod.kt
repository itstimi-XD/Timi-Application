package com.love.timi.util

import org.apache.http.client.methods.*

enum class HTTPMethod(private var httpClazz: Class<*>) {
    GET(HttpGet::class.java), PUT(HttpPut::class.java), POST(HttpPost::class.java), DELETE(HttpDelete::class.java);

    val messageObject: HttpRequestBase
        get() = httpClazz.getDeclaredConstructor().newInstance() as HttpRequestBase
}