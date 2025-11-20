package com.example.debtcalculator.services

import com.example.debtcalculator.data.Balance
import com.example.debtcalculator.data.Group
import com.example.debtcalculator.data.Transaction
import com.example.debtcalculator.data.User
import kotlin.math.abs

class DebtCalculatorService {
    fun calculateBalances(group: Group): ArrayList<Balance> {
        val map = HashMap<User, Float>()
        for (expense in group.expenses) {
            val lender = expense.owner
            val pricePerUser = expense.amount / expense.payers.size

            // Update lender's balance
            val lenderBalance = if (expense.payers.contains(lender)) expense.amount - pricePerUser else expense.amount
            map.put(lender, (map.get(lender) ?: 0f) + lenderBalance)

            // Update payers balances
            for (payer in expense.payers) {
                if (lender != payer) map.put(payer, (map.get(payer) ?: 0f) - pricePerUser)
            }
        }
        val balances = ArrayList<Balance>()

        for (user in map.keys) {
            balances.add(Balance(user, (map.get(user) ?: 0f)))
        }
        return balances
    }

    fun determineTransactions(balances: ArrayList<Balance>): ArrayList<Transaction> {
        balances.sortBy { balance -> balance.balance }
        val transactions = ArrayList<Transaction>()
        var index = balances.size - 1
        for (balance in balances) {
            if (balance.balance >= 0) {
                break
            }
            // Keep sending money until balance is smaller than 1 cent/øre
            while (balance.balance < -0.01) {
                val receiver = balances.get(index)

                // Case for sender owing less or the same as the current receiver is missing
                if (abs(balance.balance) <= abs(receiver.balance)) {
                    transactions.add(Transaction(abs(balance.balance), balance.user, receiver.user))

                    // Set sender balance to 0 and add negative balance from sender to receiver
                    receiver.balance += balance.balance
                    balance.balance = 0f
                }
                // Case for sender owing more than current receiver is missing
                else {
                    transactions.add(
                        Transaction(
                            abs(balances.get(index).balance),
                            balance.user,
                            receiver.user
                        )
                    )
                    balance.balance += receiver.balance
                    receiver.balance = 0f
                    index--
                }
            }
        }
        return transactions
    }

    fun determineTransactions(group: Group): ArrayList<Transaction> {
        return determineTransactions(calculateBalances(group))
    }
}