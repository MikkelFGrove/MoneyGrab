package com.example.debtcalculator.data

data class Transaction (
    val amount: Float,
    val sender: User,
    val receiver: User
)