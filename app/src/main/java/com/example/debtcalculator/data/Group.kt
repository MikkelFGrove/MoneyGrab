package com.example.debtcalculator.data

data class Group (
    var id: Int,
    var name: String,
    var users: Set<User>,
    var expenses: MutableList<Expense>,
    val description: String,
    var tabClosed: Boolean,
    var messages: List<Message>,
)