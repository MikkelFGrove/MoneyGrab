package com.example.debtcalculator.data

data class Group (
    val id: Int,
    val name: String,
    val users: Set<User>,
    val tabClosed: Boolean,
    val expenses: Array<Expense>,
    val messages: Array<Message>,
)