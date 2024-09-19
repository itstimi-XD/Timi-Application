package com.love.timi.util

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import kotlin.math.ceil

class MapDeserializer: JsonDeserializer<Map<String, Any>> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Map<String, Any> {
        return read(json) as Map<String, Any>
    }

    private fun read(`in`: JsonElement): Any? {
        when {
            `in`.isJsonArray -> {
                val list: MutableList<Any?> = ArrayList()
                val arr = `in`.asJsonArray
                for (anArr in arr) {
                    list.add(read(anArr))
                }
                return list
            }
            `in`.isJsonObject -> {
                val map: MutableMap<String, Any?> = HashMap()
                val obj = `in`.asJsonObject
                val entitySet = obj.entrySet()
                for (entry: Map.Entry<String, JsonElement> in entitySet) {
                    map[entry.key] = read(entry.value)
                }
                return map
            }
            `in`.isJsonPrimitive -> {
                val prim = `in`.asJsonPrimitive
                when {
                    prim.isBoolean -> return prim.asBoolean
                    prim.isString -> return prim.asString
                    prim.isNumber -> {
                        val num = prim.asNumber
                        return if (ceil(num.toDouble()) == num.toLong().toDouble()) num.toLong()
                        else num.toDouble()
                    }
                }
            }
        }
        return null
    }
}