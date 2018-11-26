'use strict';

const mysql = require('mysql2');

const config = {
    host: 'localhost',
    user: 'root',
    password: 'password',
    database: 'test'
};

const pool = mysql.createPool(config).promise();

async function main() {
    let rows, fields, results;

    [rows, fields] = await pool.query('SELECT * FROM students WHERE score >= ?', 80);
    for (let row of rows) {
        console.log(row);
    }

    [results, fields] = await pool.execute('UPDATE students SET score = score - 5 WHERE score > ? AND score < ?', [80, 90]);
    console.log(`${results.changedRows} records are updated.`);

    pool.end();
}

main();
