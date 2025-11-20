class Group {
    constructor(name, users = [], expenses = [], messages = [], id) {
        this.name = name;
        this.users = users;
        this.expenses = expenses;
        this.messages = messages;
        this.id = id;
    }

    toJSON() {
        return {
            name: this.name,
            users: this.users.toJSON(),
            expenses: this.expenses.toJSON(),
            messages: this.messages,
            id: this.id
        };
    }
}

module.exports = Group;