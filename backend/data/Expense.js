class Expense {
    constructor(amount, description, lender, payers = [], isPaid ) {
        this.amount = amount;
        this.description = description;
        this.lender = lender;
        this.payers = payers;
        this.isPaid = isPaid;
    }

    toJSON() {
        return {
            amount: this.amount,
            description: this.description,
            lender: this.lender.toJSON(),
            payers: this.payers.toJSON(),
            isPaid: this.isPaid
        };
    }
}

module.exports = Expense;