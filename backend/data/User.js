class User {
    constructor(id, phoneNumber, name, image = null) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.image = image;
    }

    toJSON() {
        return {
            phoneNumber: this.phoneNumber,
            name: this.name,
            image: this.image
        };
    }
}

module.exports = User;