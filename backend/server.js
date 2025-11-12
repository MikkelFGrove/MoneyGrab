import express from 'express';
import sqlite3  from 'sqlite3';
import cors from 'cors';
import bcrypt from 'bcrypt';

const app = express();

app.use(express.json());
app.use(cors());

const db = new sqlite3.Database('MoneyGrab')

db.serialize(() => {
    //Nulstiller tabellerne når serveren starter op
    db.run(`DROP TABLE IF EXISTS groups`);
    db.run(`DROP TABLE IF EXISTS users`);
    db.run(`DROP TABLE IF EXISTS expenses`);
    db.run(`DROP TABLE IF EXISTS transactions`);
    db.run(`DROP TABLE IF EXISTS usersInGroup`);
    db.run(`DROP TABLE IF EXISTS payersInExpense`);
    db.run(`DROP TABLE IF EXISTS transactionsInExpense`);

    db.run(`PRAGMA foreign_keys= ON`);

    //Opretter tabellerne
    db.run(`CREATE TABLE IF NOT EXISTS groups (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    image BLOB
    )`);

    db.run(`CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    phoneNumber TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    name TEXT NOT NULL,
    image BLOB
    )`);

    db.run(`CREATE TABLE IF NOT EXISTS messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sender INTEGER NOT NULL,
    "group" INTEGER NOT NULL,
    content TEXT NOT NULL,
    timeStamp TEXT NOT NULL,
    FOREIGN KEY (sender) REFERENCES users(id),
    FOREIGN KEY ("group") REFERENCES groups(id)
    )`);

    db.run(`CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    owner INTEGER NOT NULL,
    "group" INTEGER NOT NULL,
    description TEXT NOT NULL,
    amount REAL NOT NULL,
    timeStamp TEXT NOT NULL,
    FOREIGN KEY (owner) REFERENCES users(id),
    FOREIGN KEY ("group") REFERENCES groups(id)
    )`);

    db.run(`CREATE TABLE IF NOT EXISTS transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sender INTEGER NOT NULL,
    receiver INTEGER NOT NULL,
    amount REAL NOT NULL,
    creationTime TEXT NOT NULL,
    paymentTime TEXT NOT NULL,
    paid INTEGER NOT NULL,
    FOREIGN KEY (sender) REFERENCES users(id),
    FOREIGN KEY (receiver) REFERENCES users(id)
    )`);

    db.run(`CREATE TABLE IF NOT EXISTS usersInGroup (
    user INTEGER NOT NULL,
    "group" INTEGER NOT NULL,
    timeStamp TEXT NOT NULL,
    FOREIGN KEY (user) REFERENCES users(id),
    FOREIGN KEY ("group") REFERENCES groups(id)
    )`);

    db.run(`CREATE TABLE IF NOT EXISTS payersInExpense (
    user INTEGER NOT NULL,
    expense INTEGER NOT NULL,
    FOREIGN KEY (user) REFERENCES users(id),
    FOREIGN KEY (expense) REFERENCES expenses(id)
    )`);

    db.run(`CREATE TABLE IF NOT EXISTS transactionsInExpense (
    expense INTEGER NOT NULL,
    "transaction" INTEGER NOT NULL,
    FOREIGN KEY (expense) REFERENCES expenses(id),
    FOREIGN KEY ("transaction") REFERENCES transactions(id)
    )`);


    // Dummy data for tabellerne (AI genereret og ikke verificeret)
     db.run(`INSERT INTO groups (name) VALUES
        ('Sommerhus-turen'),
        ('Roomies'),
        ('Arbejdsholdet')
    `);

    db.run(`INSERT INTO users (phoneNumber, password, name) VALUES
        ('12345678', 'pass1', 'Anna'),
        ('87654321', 'pass2', 'Mikkel'),
        ('11223344', 'pass3', 'Sara'),
        ('44332211', 'pass4', 'Jonas')
    `);

    db.run(`INSERT INTO usersInGroup (user, "group", timeStamp) VALUES
        (1, 1, datetime('now')),
        (2, 1, datetime('now')),
        (3, 1, datetime('now')),
        (4, 2, datetime('now')),
        (1, 2, datetime('now'))
    `);

    db.run(`INSERT INTO messages (sender, "group", content, timeStamp) VALUES
        (1, 1, 'Hej alle! Klar til turen?', datetime('now')),
        (2, 1, 'Jeps, glæder mig!', datetime('now')),
        (3, 1, 'Jeg tager snacks med.', datetime('now'))
    `);

    db.run(`INSERT INTO expenses (owner, "group", description, amount, timeStamp) VALUES
        (1, 1, 'Leje af sommerhus', 2000.00, datetime('now')),
        (2, 1, 'Mad og drikke', 600.00, datetime('now'))
    `);

    db.run(`INSERT INTO payersInExpense (user, expense) VALUES
        (1, 1),
        (2, 2)
    `);

    db.run(`INSERT INTO transactions (sender, receiver, amount, creationTime, paymentTime, paid) VALUES
        (2, 1, 666.67, datetime('now'), 'pending', 0),
        (3, 1, 666.67, datetime('now'), 'pending', 0),
        (3, 2, 200.00, datetime('now'), datetime('now'), 1)
    `);

    db.run(`INSERT INTO transactionsInExpense (expense, "transaction") VALUES
        (1, 1),
        (1, 2),
        (2, 3)
    `);
});
// get group messages
app.get('/groups/:id/messages', (req, res) => {

    const { id } = req.params;
    db.all(
        'SELECT * FROM messages WHERE "group" = ?',
        [id],
        (err, rows) => {
            if(err) return res.status(500).json({error: err.message});
            if(!rows) return res.status(404).json({error: 'messages not found'});
            res.json(rows);
        });
});

// get group by id
app.get('/groups/:id', (req, res) => {
    const { id } = req.params;
    db.get(
        'SELECT * FROM groups WHERE id = ?',
        [id],
        (err, row) => {
            if(err) return res.status(500).json({error: err.message});
            if(!row) return res.status(404).json({error: 'group not found'});
            res.json(row);
        });
});

//Insert new group
app.post('/groups', (req, res) => {
    const { name, image} = req.body;

    db.run('INSERT INTO groups VALUES (?, ?)',
        [name, image],
        function(err) {
            if (err) return res.status(500).json({ error: err.message });
            return res.json({ group_id: this.lastID });
        }
    );
});

// TODO: POST /expenses

//TODO: POST /transactions


//Insert new user
app.post('/users', async (req, res) => {
    try {
            const { phoneNumber, password, name, image } = req.body;
            const hashedPassword = await bcrypt.hash(password, 5);
            
            db.run('INSERT INTO users (phoneNumber, password, name, image) VALUES (?, ?, ?, ?)',
                [phoneNumber, hashedPassword, name, image],
                function(err) {
                    if(err) return res.status(500).json({ error: err.message});

                    const user_id = this.lastID;
                    db.get('SELECT id, phoneNumber, name, image FROM users WHERE id = ?', [user_id], (err2, row) => {
                        if (err2) return res.status(500).json ({ error: err2.message});
                        if(!row) return res.status(404).json({error: 'user not found'});
                        return res.json(row);
                    });
                });
        } catch (e) {
            return res.status(500).json({ error: e.message });
        }
});

//Login user
app.post('/login', async (req, res) => {
    try {
        const {phoneNumber, password } = req.body;
    
        db.get('SELECT * FROM users WHERE phoneNumber = ?',
            [phoneNumber],
            async (err, user) => {
                if (err) return res.status(500).json({err: err.message});
                if(!user) return res.status(404).json({err: 'User not found'});

                const isMatch = await bcrypt.compare(password, user.password);
                if(!isMatch) return res.status(401).json({err: 'Wrong password'});

                const {id, name, image } = user;
                return res.json({ id, phoneNumber, name, image});
            }
        );
    } catch (e) {
        return res.status(500).json({err: e.message});
    }
});

//Get user by id
app.get('/users/:id', (req, res) => {
    const { id } = req.params;
    db.get('SELECT * FROM users WHERE id = ?',
        [id],
        (err, row) => {
            if(err) return res.status(500).json({error: err.message});
            if(!row) return res.status(404).json({err: 'User not found'});
            res.json(row);
        });
});

app.get('/users/:id/expenses', (req, res) => {
    const { id } = req.params;
    db.all(`SELECT expense.id, expense.owner, expense."group", expense.description, expense.amount, expense.timeStamp
            FROM expenses expense
            INNER JOIN payersInExpense pIE on expense.id = pIE.expense
            WHERE pIE.user = ?`,
        [id],
        (err, rows) => {
            if(err) return res.status(500).json({err: err.message });
            if(!rows) return res.status(404).json({err: 'No expenses found for this user' });
            res.json(rows);
        });
});

app.listen(3000, () => console.log('The server is running on http://localhost:3000'));