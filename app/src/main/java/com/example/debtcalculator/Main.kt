package com.example.debtcalculator

import com.example.debtcalculator.data.Expense
import com.example.debtcalculator.data.Group
import com.example.debtcalculator.data.User
import com.example.debtcalculator.services.DebtCalculatorService

fun main() {
    val debtCalculatorService = DebtCalculatorService()

    val userJoe = User("12341234", "Joe Mama", null)
    val userJohn = User("12345678", "John Bandit", null)
    val userSonny = User("87654321", "Sonny Riskiks", null)

    val banankage = Expense(100f, "Banankage", userJohn, arrayOf(userJoe, userSonny, userJohn))
    val riskiks = Expense(10.95f, "Riskiks", userSonny, arrayOf(userJoe, userJohn, userSonny))
    val skraldesække = Expense(250f, "Skraldesække", userJoe, arrayOf(userJohn, userJoe, userSonny))

    val group = Group(
        "Group",
        setOf(userJoe, userJohn, userSonny),
        mutableListOf(banankage, riskiks, skraldesække),
        arrayOf(),
        id = 1
        )
    val balances = debtCalculatorService.calculateBalances(group)
    println(balances)

    val transactions = debtCalculatorService.determineTransactions(group)
    println(transactions)
}