package com.example.debtcalculator.data

import kotlin.time.TimeMark

data class Message(
    val sender: User,
    val message: String,
    val timeStamp: TimeMark,
)
