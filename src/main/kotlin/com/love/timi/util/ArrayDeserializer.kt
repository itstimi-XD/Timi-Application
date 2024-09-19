package com.love.timi.util

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import kotlin.math.ceil

class ArrayDeserializer: JsonDeserializer<List<Any>> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<Any> {
        return read(json) as List<Any>
    }
    private fun read(`in`: JsonElement): Any? {
        return when {
            `in`.isJsonArray -> {
                val list: MutableList<Any?> = ArrayList()
                val arr = `in`.asJsonArray
                for (anArr in arr) {
                    list.add(read(anArr))
                }
                list
            }
            `in`.isJsonObject -> {
                val map: MutableMap<String, Any?> = HashMap()
                val obj = `in`.asJsonObject
                val entitySet = obj.entrySet()
                for (entry: Map.Entry<String, JsonElement> in entitySet) {
                    map[entry.key] = read(entry.value)
                }
                map
            }
            `in`.isJsonPrimitive -> {
                val prim = `in`.asJsonPrimitive
                when {
                    prim.isBoolean -> prim.asBoolean
                    prim.isString -> prim.asString
                    prim.isNumber -> {
                        val num = prim.asNumber
                        if (ceil(num.toDouble()) == num.toLong().toDouble()) num.toLong()
                        else num.toDouble()
                    }
                    else -> null
                }
            }
            else -> null
        }
    }
}