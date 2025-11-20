import express from 'express';
import sqlite3  from 'sqlite3';
import cors from 'cors';
import bcrypt from 'bcrypt';

import DebtCalculatorService from './DebtCalculatorService.js';
import User from './data/User.js';
import Expense from './data/Expense.js';
import Group from './data/Group.js';

let debtService = new DebtCalculatorService();

let app = express();

app.use(express.json());
app.use(cors());

let db = new sqlite3.Database('MoneyGrab')

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
    image TEXT,
    description TEXT,
    isClosed INTEGER DEFAULT 0
    )`);

    db.run(`CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    phoneNumber TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    name TEXT NOT NULL,
    image TEXT
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
    paymentTime TEXT,
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
    db.run(`INSERT INTO groups (name, isClosed, description) VALUES
        ('Sommerhus-turen', 0, 'Planlægning og koordinering af vores sommerhustur'),
        ('Roomies', 0, 'Gruppen til alt praktisk mellem os der bor sammen'),
        ('Arbejdsholdet', 1, 'Internt holdchat til arbejdsrelaterede ting')
    `);

    db.run(`INSERT INTO users (phoneNumber, password, name) VALUES
        ('12345678', 'pass1', 'Anna'),
        ('87654321', 'pass2', 'Mikkel'),
        ('11223344', 'pass3', 'Sara'),
        ('44332211', 'pass4', 'Jonas'),
        ('77777777', '$2b$05$BoR0ZZHd5L9fpB0A9nfIEOvKBlnPu4mVnOopxgZY.3B3QrgRoUfy2', 'John Doe'),
        ('69696969', '$2b$05$TPfjWVii6GzFFhiWcIt5LOWdtaoSwolkNsXWPEpP/7/n0Lvih6JK.', 'Andreas The G')
    `);

    db.run(`INSERT INTO usersInGroup (user, "group", timeStamp) VALUES
        (1, 1, datetime('now')),
        (2, 1, datetime('now')),
        (3, 1, datetime('now')),
        (4, 2, datetime('now')),
        (1, 2, datetime('now')),
        (5, 1, datetime('now')),
        (5, 2, datetime('now')),
        (5, 3, datetime('now'))
    `);

    db.run(`INSERT INTO messages (sender, "group", content, timeStamp) VALUES
        (1, 1, 'Hej alle! Klar til turen?', datetime('now')),
        (2, 1, 'Jeps, glæder mig!', datetime('now')),
        (3, 1, 'Jeg tager snacks med.', datetime('now')),
        (4, 2, 'Hej Roomies!', datetime('now')),
        (1, 2, 'Hej Jonas!', datetime('now'))
    `);

    db.run(`INSERT INTO expenses (owner, "group", description, amount, timeStamp) VALUES
        (1, 1, 'Leje af sommerhus', 2000.00, datetime('now')),
        (2, 1, 'Mad og drikke', 600.00, datetime('now')),
        (4, 2, 'Netflix abonnement', 120.00, datetime('now')),
        (5, 1, 'Mad og drikke', 600.00, datetime('now'))
        `);

    db.run(`INSERT INTO payersInExpense (user, expense) VALUES
        (1, 1),
        (2, 1),
        (3, 1),
        (2, 2),
        (1, 2),
        (3, 2),
        (1, 3),
        (4, 3),
        (1, 4),
        (1, 2),
        (4, 4)
    `);

    db.run(`INSERT INTO transactions (sender, receiver, amount, creationTime, paymentTime, paid) VALUES
        (2, 1, 666.67, datetime('now'), NULL, 0),
        (3, 1, 666.67, datetime('now'), NULL, 0),
        (3, 2, 200.00, datetime('now'), datetime('now'), 1),
        (1, 4, 60.00, datetime('now'), NULL, 0)
    `);

    db.run(`INSERT INTO transactionsInExpense (expense, "transaction") VALUES
        (1, 1),
        (1, 2),
        (2, 3),
        (3, 4)
    `);
});
// get group messages
app.get('/groups/:id/messages', (req, res) => {

    let { id } = req.params;
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
    let { id } = req.params;
    db.get(
        'SELECT * FROM groups WHERE id = ?',
        [id],
        (err, row) => {
            if(err) return res.status(500).json({error: err.message});
            if(!row) return res.status(404).json({error: 'group not found'});
            row.tabClosed = row.tabClose == 1;
            res.json(row);
        });
});

//Create new group
app.post('/groups', (req, res) => {
    let { name, description, image, owner, users } = req.body;

    db.run('INSERT INTO groups (name, description, image) VALUES (?, ?, ?)',
        [name, description, image],
        function(err) {
            if (err) return res.status(500).json({ error: err.message });
            let group_id = this.lastID

            db.run('INSERT INTO usersInGroup (user, "group", timeStamp) VALUES (?, ?, CURRENT_TIMESTAMP)',
                [owner, group_id],
                function(err) {
                    if (err) return res.status(500).json({error: err.message});

                    let sqlStmt = db.prepare('INSERT INTO usersInGroup (user, "group", timeStamp) VALUES (?, ?, CURRENT_TIMESTAMP)');
                    users.forEach(user => sqlStmt.run(user.id, group_id));
                    sqlStmt.finalize(finalerror => {
                        if (finalerror) return res.status(500).json({error: finalerror.message});
                        return res.status(200).json({group_id: group_id});
                    });
                    //return res.json({group_id: group_id})
                }
            );
        }
    );
});

app.get("/users/search/:searchString", (req, res) => {
    const { searchString } = req.params;

    db.all('SELECT * FROM users WHERE phoneNumber LIKE ?',
        [`%${searchString}%`],
        function(err, rows) {
            if(err) return res.status(500).json({err: err.message });
            if(!rows) return res.status(404).json({err: 'No expenses found for this user' });
            res.json(rows);
        }
    )
})

app.get('/expenses/:id', (req, res) => {
   const { id } = req.params;
       db.get('SELECT * FROM expenses WHERE id = ?',
           [id],
           (err, row) => {
               if(err) return res.status(500).json({error: err.message});
               if(!row) return res.status(404).json({err: 'Expense not found'});
               res.json(row);
           });
});

//Update group
app.post('/update/groups', (req, res) => {
    let {name, image, description, isClosed, id} = req.body;
    db.run('UPDATE groups SET name = ?, image = ?, description = ?, isClosed = ? WHERE id = ?', 
        [name, image, description, isClosed , id],
    err => {
        if(err) return res.status(500).json({error: err.message});
    });
    db.get('SELECT * FROM groups WHERE id = ?', 
        [id],
    (err, row) => {
        
        if(err) return res.status(500).json({error: err.message});
        return res.json(row);
    });
});


//Create new expense (This assumes that the payers is an array when being send to the backend)
app.post('/expenses', (req, res) => {
    const { owner, group, description, amount, payers } = req.body;
    db.run(`INSERT INTO expenses (owner, "group", description, amount, timeStamp)
            VALUES (?,?,?,?, CURRENT_TIMESTAMP)`,
        [owner.id, group, description, amount],
        function(err) {
        if(err) return res.status(500).json({err: err.message});

        let expenseId = this.lastID;
        let payerList = payers.length > 0 ? payers: [owner];

        const sqlStatement = db.prepare('INSERT INTO payersInExpense (user, expense) VALUES (?, ?)');
        payerList.forEach(user => sqlStatement.run(user.id, expenseId));
        sqlStatement.finalize((err) => {
            if(err) {
                return res.status(500).json({error: err});
            }

        return res.status(200).json({expense_id: expenseId});
        });
    });
});

// Get users in a group
app.get('/groups/:id/users', (req, res) => {
    let {id } = req.params;
    db.all('SELECT * FROM users WHERE id IN (SELECT user FROM usersInGroup WHERE "group" = ?)',
        [id],
        (err, rows) => {
            if(err) return res.status(500).json({error: err.message});
            if(!rows) return res.status(404).json({error: 'No users found within that group'});
            res.json(rows);
        }
    )
})


//Create new transaction
app.post('/transactions', (req, res) =>{
    let { sender, receiver, amount, paid = 0, expenseId } = req.body;
    let paymentTimeValue = paid === 1 ? 'CURRENT_TIMESTAMP' : '0';

    let sqlStatement = `INSERT INTO transactions (sender, receiver, amount, creationTime, paymentTime, paid)
    VALUES (?, ?, ?, CURRENT_TIMESTAMP, ${paymentTimeValue}, ?)`;

    db.run(sqlStatement, 
        [sender, receiver, amount, paid],
        function(err) {
        if (err) return res.status(500).json({error: err.message});

        let transactionId = this.lastID;

        let sqlStmt = db.prepare('INSERT INTO transactionsInExpense (expense, "transaction") VALUES (?, ?)');
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
            let { phoneNumber, password, name, image } = req.body;
            let hashedPassword = await bcrypt.hash(password, 5);
            
            db.run('INSERT INTO users (phoneNumber, password, name, image) VALUES (?, ?, ?, ?)',
                [phoneNumber, hashedPassword, name, image],
                function(err) {
                    if(err) return res.status(500).json({ error: err.message});

                    let user_id = this.lastID;
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

// Update user
app.post('/update/users', (req, res) => {
    let {phoneNumber, name, image, id} = req.body;
    db.run('UPDATE users SET phoneNumber = ?, name = ?, image = ? WHERE id = ?', 
        [phoneNumber, name, image, id],
    err => {
        if(err) return res.status(500).json({error: err.message});
    });
    db.get('SELECT id, phoneNumber, name, image FROM users WHERE id = ?',
        [id],
    (err, row) => {
        if(err) return res.status(500).json({error: err.message});
        return res.status(200).json(row);
    });
});

//Login user
app.post('/login', async (req, res) => {
    try {
        let {phoneNumber, password } = req.body;
    
            db.get('SELECT id, phoneNumber, password, name, image FROM users WHERE phoneNumber = ?',
            [phoneNumber],
            async (err, user) => {
                if (err) return res.status(500).json({err: err.message});
                if(!user) return res.status(404).json({err: 'User not found'});

                let isMatch = await bcrypt.compare(password, user.password);
                if(!isMatch) return res.status(401).json({err: 'Wrong password'});

                const {id, phoneNumber, name, image } = user;
                return res.json({id, phoneNumber, name, image});
            }
        );
    } catch (e) {
        return res.status(500).json({err: e.message});
    }
});

//Get user by id
app.get('/users/:id', (req, res) => {
    let { id } = req.params;
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
    let { id } = req.params;
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
    let { id } = req.params;
    db.all(`SELECT groups.id, groups.name, groups.description, groups.image
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
  db.all(`SELECT id, owner, "group", description, amount, timeStamp
          FROM expenses expense
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
    let { id } = req.params;
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

// Get current sum of individual members in the group
app.get('/groups/:id/:user/sum', (req, res) => {
    const groupId = req.params.id;
    const userId = req.params.user;

    // Get all users in the group
    db.all(
        'SELECT * FROM users WHERE id IN (SELECT user FROM usersInGroup WHERE "group" = ?)',
        [groupId],
        (err, userRows) => {
            if (err) return res.status(500).json({ error: err.message });

            const users = userRows.map(u => new User(u.id, u.phoneNumber, u.name, u.image));

            // Get all expenses in the group
            db.all('SELECT * FROM expenses WHERE "group" = ?', [groupId], (err2, expenseRows) => {
                if (err2) return res.status(500).json({ error: err2.message });

                // Case: no expenses
                if (expenseRows.length === 0) {
                    const group = new Group("temp", users, [], [], groupId);
                    const balances = debtService.calculateBalances(group);
                    const userBalance = balances.find(b => b.user.id == userId);
                    return res.json(userBalance || { error: "User balance not found" });
                }

                // Build Expense objects with payers
                let expenses = [];
                let processed = 0;

                expenseRows.forEach(expense => {
                    db.all(
                        'SELECT user FROM payersInExpense WHERE expense = ?',
                        [expense.id],
                        (err3, payerRows) => {
                            if (err3) return res.status(500).json({ error: err3.message });

                            const payerUsers = payerRows.map(p => {
                                const found = userRows.find(u => u.id === p.user);
                                return found ? new User(found.id, found.phoneNumber, found.name, found.image) : null;
                            }).filter(Boolean);

                            const ownerRow = userRows.find(u => u.id === expense.owner);
                            const lender = ownerRow ? new User(ownerRow.id, ownerRow.phoneNumber, ownerRow.name, ownerRow.image) : null;

                            expenses.push(new Expense(
                                expense.amount,
                                expense.description,
                                lender,
                                payerUsers
                            ));

                            processed++;

                            // Once all expenses are processed
                            if (processed === expenseRows.length) {
                                const group = new Group("temp", users, expenses, [], groupId);
                                const balances = debtService.calculateBalances(group);
                                const userBalance = balances.find(b => b.user.id == userId);

                                if (!userBalance) {
                                    return res.status(404).json({ error: "Giraffe balance not found" });
                                }

                                return res.json({"amount": userBalance.balance});
                            }
                        }
                    );
                });
            });
        }
    );
});




//Pay transactions
app.post('/payTransactions', (req, res) => {
    let {groupId, userId} = req.body;

    db.run(`UPDATE transactions SET paid = 1, paymentTime = CURRENT_TIMESTAMP
            WHERE sender = ?
            AND paid = 0
            AND id IN (
            SELECT "transaction"
            FROM transactionsInExpense tIE
            JOIN expenses expense ON expense.id = tIE.expense
            WHERE expense."group" = ?)`,
        [userId, groupId],
        function(err) {
            if(err) return res.status(500).json({error: err.message});
            return res.json({ updated: this.changes, message: "Transactions successfully updated to paid"});
        });
});

// Close the group
app.post('/closeGroup', (req, res) => {
    let { groupId } = req.body;

    //Closes the group
    db.run('UPDATE groups SET isClosed = 1 WHERE id = ?', [groupId], async (err) => {
        if (err) return res.status(500).json({ error: err.message });

        try {
            //Get users in the group
            const userRows = await new Promise((resolve, reject) => {
                db.all('SELECT * FROM users WHERE id IN (SELECT user FROM usersInGroup WHERE "group" = ?)', [groupId], (err, rows) => {
                    if (err) reject(err);
                    else resolve(rows);
                });
            });

            const users = userRows.map(u => new User(u.id, u.phoneNumber, u.name, u.image));

            // Get expenses in the group
            const expenseRows = await new Promise((resolve, reject) => {
                db.all('SELECT * FROM expenses WHERE "group" = ?', [groupId], (err, rows) => {
                    if (err) reject(err);
                    else resolve(rows);
                });
            });

            if (expenseRows.length === 0) {
                return res.json({ message: "Group closed, no expenses to process" });
            }

            // Build Expense objects
            let expenses = [];
            for (const expense of expenseRows) {
                const payerRows = await new Promise((resolve, reject) => {
                    db.all('SELECT user FROM payersInExpense WHERE expense = ?', [expense.id], (err, rows) => {
                        if (err) reject(err);
                        else resolve(rows);
                    });
                });

                const payerUsers = payerRows.map(p => {
                    const found = userRows.find(u => u.id === p.user);
                    return new User(found.id, found.phoneNumber, found.name, found.image);
                });

                const ownerRow = userRows.find(u => u.id === expense.owner);
                const lender = new User(ownerRow.id, ownerRow.phoneNumber, ownerRow.name, ownerRow.image);

                expenses.push(new Expense(expense.amount, expense.description, lender, payerUsers));
            }

            // Calculate balances and transactions
            const group = new Group("temp", users, expenses, [], groupId);
            const balances = debtService.calculateBalances(group);
            const transactions = debtService.determineTransactions(balances);

            if (transactions.length === 0) {
                return res.json({ message: "Group is closed. No transactions needed." });
            }

            //  Insert transactions into DB
            for (let transactionz of transactions) {
                let transactionId = await new Promise((resolve, reject) => {
                    db.run(
                        'INSERT INTO transactions (sender, receiver, amount, creationTime, paid, paymentTime) VALUES (?, ?, ?, CURRENT_TIMESTAMP, 0, NULL)',
                        [transactionz.sender.id, transactionz.receiver.id, transactionz.amount],
                        function(err) {
                            if (err) reject(err);
                            else resolve(this.lastID);
                        }
                    );
                });

                // Link transaction to all relevant expenses
                for (let expense of transactionz.expenses || []) {
                    await new Promise((resolve, reject) => {
                        db.run(
                            'INSERT INTO transactionsInExpense (expense, "transaction") VALUES (?, ?)',
                            [expense.id, transactionId],
                            err => err ? reject(err) : resolve()
                        );
                    });
                }
            }

            return res.json({
                message: "Group is now closed and all transactions have been recorded",
                transactions: transactions.map(t => t.toJSON())
            });

        } catch (error) {
            return res.status(500).json({ error: error.message });
        }
    });
});

    

app.listen(3000, () => console.log('The server is running on http://localhost:3000'));
