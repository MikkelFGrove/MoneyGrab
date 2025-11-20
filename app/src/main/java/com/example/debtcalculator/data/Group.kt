package com.example.debtcalculator.data

data class Group (
    val name: String,
    val users: Set<User>,
    var expenses: MutableList<Expense>,
    val description: String,
    val messages: Array<Message>,
    val id: Int
)