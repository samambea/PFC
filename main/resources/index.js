const express = require('express');
const session = require('express-session');
const bodyParser = require('body-parser');

const app = express();
const PORT = 3000;

// Middleware
app.use(bodyParser.urlencoded({ extended: false }));
app.use(express.static('public'));
app.use(express.json());

// Session setup
app.use(session({
    secret: 'mySecret',
    resave: false,
    saveUninitialized: true,
    cookie: { secure: false } // Mude para true se usar HTTPS
}));

// Dummy database for users
const users = [
    { username: 'Lucas', password: 'senha123', fullName: 'Lucas Silva' }
];

// Login Route
app.post('/login', (req, res) => {
    const { username, password } = req.body;
    const user = users.find(u => u.username === username && u.password === password);

    if (user) {
        req.session.user = user; // Store user info in session
        res.redirect('/');
    } else {
        res.send('Usuário ou senha inválidos');
    }
});

// Logout Route
app.get('/logout', (req, res) => {
    req.session.destroy(() => {
        res.redirect('/');
    });
});

// Session Info Route (for frontend)
app.get('/api/session', (req, res) => {
    if (req.session.user) {
        res.json({ loggedIn: true, username: req.session.user.fullName });
    } else {
        res.json({ loggedIn: false });
    }
});

// Main Route - Home
app.get('/', (req, res) => {
    const user = req.session.user;
    res.send(`
        <html>
        <body>
            <h1>Bem-vindo à Apollo Pesquisas</h1>
            ${user ? `
                <h2>Bem-vindo, ${user.fullName}!</h2>
                <a href="/logout">Sair</a>
            ` : `
                <h2>Você não está logado</h2>
                <form action="/login" method="POST">
                    <input type="text" name="username" placeholder="Usuário" required>
                    <input type="password" name="password" placeholder="Senha" required>
                    <button type="submit">Entrar</button>
                </form>
            `}
        </body>
        </html>
    `);
});

// Start server
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});
