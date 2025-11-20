class Balance {
    constructor(user, balance) {
        this.user = user;
        this.balance = balance;
    }

    toJSON() {
        return {
            user: this.user.toJSON(),
            balance: this.balance
        };
    }
}


module.exports = Balance;