package com.example.debtcalculator.data

data class Expense (
    val name: String,
    val amount: Float,
    val description: String,
    val lender: User,
    val payers: Array<User>
)

