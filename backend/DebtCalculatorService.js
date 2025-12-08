const Balance = require("./data/Balance");
const Transaction = require("./data/Transaction");


class DebtCalculatorService {

    calculateBalances(group) {
    const map = {}; 

    for (const expense of group.expenses) {
        const lender = expense.lender;
        const pricePerUser = expense.amount / expense.payers.length;

        if(expense.isPaid === 1) {
            continue;
        }

        // Check if lender is among payers
        const lenderOwesPart = expense.payers.some(p => p.phoneNumber === lender.phoneNumber);
        const lenderBalance = lenderOwesPart ? expense.amount - pricePerUser : expense.amount;

        map[lender.phoneNumber] = (map[lender.phoneNumber] || 0) + lenderBalance;

        for (const payer of expense.payers) {
            if (payer.phoneNumber !== lender.phoneNumber) {
                map[payer.phoneNumber] = (map[payer.phoneNumber] || 0) - pricePerUser;
            }
        }
    }

    const balances = [];
    for (const phone in map) {
        const user = group.users.find(u => u.phoneNumber === phone);
        balances.push({ user, balance: map[phone] });
    }

    return balances;
}


    determineTransactions(balances) {
        balances.sort((a, b) => a.balance - b.balance);

        let transactions = [];

        let index = balances.length -1;

        for(let balance of balances) {
            if(balance.balance >= 0) break;

            // Keep sending money until balance is smaller than 1 cent/øre
            while (balance.balance < -0.01) {
                let receiver = balances[index];

                // Case 1: Sender owes same amount or less than the receiver is owed
                if (Math.abs(balance.balance) <= Math.abs(receiver.balance)) {
                    transactions.push(new Transaction(Math.abs(balance.balance), balance.user, receiver.user));

                    // Set sender balance to 0 and add negative balance from sender to receiver
                    receiver.balance += balance.balance;
                    balance.balance = 0;

                // Case 2: Sender owes more than receiver is owed    
                } else {
                    transactions.push(new Transaction(Math.abs(receiver.balance), balance.user, receiver.user));

                    balance.balance += receiver.balance;
                    receiver.balance = 0;
                    index--;
                }
            }
        }
        return transactions;
    }

    determineTransactionsForgroup(group) {
        return this.determineTransactions(this.calculateBalances(group));
    }

}


module.exports = DebtCalculatorService;