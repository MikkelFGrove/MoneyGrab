package com.example.debtcalculator.data

data class Expense (
    var id: Int,
    var amount: Float,
    var description: String,
    var group: Int,
    var owner: User,
    var payers: List<User>
)

