package com.example.debtcalculator.data

data class Group (
    val name: String,
    val users: Set<User>,
    val expenses: Array<Expense>,
    val messages: Array<Message>,
)