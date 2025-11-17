package com.example.debtcalculator.data

data class Group (
    val id: Int,
    val name: String,
    val users: Set<User>,
    var expenses: MutableList<Expense>,
    val tabClosed: Boolean,
    val messages: Array<Message>,
    val id: Int
)