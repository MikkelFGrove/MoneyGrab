package com.example.debtcalculator.data

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type

data class Group (
    var id: Int,
    var name: String,
    var users: MutableSet<User>,
    var expenses: MutableList<Expense>,
    var description: String,
    @JsonAdapter(IntToBooleanAdapter::class)
    var isClosed: Boolean,
    var messages: List<Message>,
    var image: String?
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