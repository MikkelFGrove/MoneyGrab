package com.example.debtcalculator.data

import java.sql.Timestamp

data class Expense (
    val id: Int,
    val amount: Float,
    val description: String,
    val lender: User,
    val timeStamp: Timestamp,
    val payers: Array<User>
)

