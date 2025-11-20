class Expense {
    constructor(amount, description, lender, payers = [] ) {
        this.amount = amount;
        this.description = description;
        this.lender = lender;
        this.payers = payers;
    }

    toJSON() {
        return {
            amount: this.amount,
            description: this.description,
            lender: this.lender.toJSON(),
            payers: this.payers.toJSON()
        };
    }
}

module.exports = Expense;