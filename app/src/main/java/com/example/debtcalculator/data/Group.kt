package com.example.debtcalculator.data

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type

data class Group (
    var id: Int,
    var name: String,
    var users: Set<User>,
    var expenses: MutableList<Expense>,
    val description: String,
    @JsonAdapter(IntToBooleanAdapter::class)
    var isClosed: Boolean,
    val tabClosed: Boolean,
    var messages: List<Message>,
)

class IntToBooleanAdapter : JsonDeserializer<Boolean> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Boolean {
        return json.asInt == 1
    }
}