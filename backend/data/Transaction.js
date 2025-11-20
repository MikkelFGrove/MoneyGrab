class Transaction {
    constructor(amount, sender, receiver) {
        this.amount = amount;
        this.sender = sender;
        this.receiver = receiver;
    }

    toJSON() {
        return {
            amount: this.amount,
            sender: this.sender.toJSON(),
            receiver: this.receiver.toJSON()
        };
    }
}

module.exports = Transaction;