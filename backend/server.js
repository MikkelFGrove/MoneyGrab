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
    db.run('DROP TABLE IF EXISTS groups');
    db.run('DROP TABLE IF EXISTS users');
    db.run('DROP TABLE IF EXISTS messages')
    db.run('DROP TABLE IF EXISTS expenses');
    db.run('DROP TABLE IF EXISTS transactions');
    db.run('DROP TABLE IF EXISTS usersInGroup');
    db.run('DROP TABLE IF EXISTS payersInExpense');
    db.run('DROP TABLE IF EXISTS transactionsInExpense');

    db.run('PRAGMA foreign_keys= ON');

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
        (2, 1, 666.67, datetime('now'), '0', 0),
        (3, 1, 666.67, datetime('now'), '0', 0),
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

//Create new group
app.post('/groups', (req, res) => {
    const { name, image} = req.body;

    db.run('INSERT INTO groups (name, image) VALUES (?, ?)',
        [name, image],
        function(err) {
            if (err) return res.status(500).json({ error: err.message });
            return res.json({ group_id: this.lastID });
        }
    );
});

//Create new expense (This assumes that the payers is an array when being send to the backend)
app.post('/expenses', (req, res) => {
    const { owner, group, description, amount, payers } = req.body;

    db.run(`INSERT INTO expenses (owner, "group", description, amount, timeStamp)
            VALUES (?,?,?,?, CURRENT_TIMESTAMP)`,
        [owner, group, description, amount],
    function(err) {
        if(err) return res.status(500).json({err: err.message});

        const expenseId = this.lastID;
        const payerList = payers.length > 0 ? payers: [owner];

        const sqlStatement = db.prepare('INSERT INTO payersInExpense (user, expense) VALUES (?, ?)');
        payerList.forEach(user => sqlStatement.run(user, expenseId));
        sqlStatement.finalize((err) => {
            if(err) {
                return res.status(500).json({error: err});
            }

        return res.status(200).json({expense_id: expenseId});
        });
    });
});


//Create new transaction
app.post('/transactions', (req, res) =>{
    const { sender, receiver, amount, paid = 0, expenseId } = req.body;
    const paymentTimeValue = paid === 1 ? 'CURRENT_TIMESTAMP' : '0';

    const sqlStatement = `INSERT INTO transactions (sender, receiver, amount, creationTime, paymentTime, paid)
    VALUES (?, ?, ?, CURRENT_TIMESTAMP, ${paymentTimeValue}, ?)`;

    db.run(sqlStatement, 
        [sender, receiver, amount, paid],
    function(err) {
        if (err) return res.status(500).json({error: err.message});

        const transactionId = this.lastID;

        const sqlStmt = db.prepare('INSERT INTO transactionsInExpense (expense, "transaction") VALUES (?, ?)');
        expenseId.forEach(expense => sqlStmt.run(expense, transactionId));
        sqlStmt.finalize(finalerror => {
            if(finalerror) return res.status(500).json({error: finalerror.message});
            return res.status(200).json({ transaction_id: transactionId});
        });
    });
});


//Create new user
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
    
            db.get('SELECT phoneNumber, password, name, image FROM users WHERE phoneNumber = ?',
            [phoneNumber],
            async (err, user) => {
                if (err) return res.status(500).json({err: err.message});
                if(!user) return res.status(404).json({err: 'User not found'});

                const isMatch = await bcrypt.compare(password, user.password);
                if(!isMatch) return res.status(401).json({err: 'Wrong password'});

                const {phoneNumber, name, image } = user;
                return res.json({phoneNumber, name, image});
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
// Get expenses on a user
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

// Get groups on a user
app.get('/users/:id/groups', (req, res) => {
    const { id } = req.params;
    db.all(`SELECT groups.id, groups.name, groups.image 
            FROM groups 
            INNER JOIN usersInGroup uIG on groups.id = uIG."group" 
            WHERE uIG.user = ?`,
    [id],
    (err, rows) => {
        if(err) return res.status(500).json({err: err.message});
        if(!rows) return res.status(404).json({err: 'No groups found for this user'});
        res.json(rows);
    });
});

//Get expenses on a group
app.get('/groups/:id/expenses', (req, res) => {
  const { id } = req.params;
  db.all(
    `SELECT expense.id, expense.owner, expense."group", expense.description, expense.amount, expense.timeStamp
     FROM expenses expense
     INNER JOIN payersInExpense pIE on expense.id = pIE.expense
     WHERE expense."group" = ?`,
    [id],
    (err, rows) => {
      if (err) return res.status(500).json({ err: err.message });
      if (!rows) return res.status(404).json({ err: 'No expenses found for this group' });
      res.json(rows);
    }
  );
});

//Get nonpaid transactions on a group
app.get('/groups/:id/outstandingPayments', (req, res) => {
    const { id } = req.params;
    db.all(
        `SELECT * from transactions WHERE paid = 0
        AND id IN (
            SELECT "transaction" FROM transactionsInExpense
            JOIN expenses e on e.id = transactionsInExpense.expense
            WHERE e."group" = ?
        )`, [id],
        (err, rows) => {
            if(err) return res.status(500).json({error: err.message});
            if(!rows) return res.status(404).json({error: 'No nonpaid transactions found'});
            res.json(rows);
        }
    );
});

app.listen(3000, () => console.log('The server is running on http://localhost:3000'));