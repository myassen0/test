from flask import Flask, render_template, request, redirect, url_for
import sqlite3
from datetime import datetime
import os
import threading
import webbrowser

app = Flask(__name__)
DATABASE = 'gym.db'

def init_db():
    if os.path.exists(DATABASE):
        conn = sqlite3.connect(DATABASE)
        cursor = conn.cursor()
        try:
            cursor.execute("SELECT subscription_from FROM clients LIMIT 1")
        except:
            conn.close()
            os.remove(DATABASE)
    conn = sqlite3.connect(DATABASE)
    cursor = conn.cursor()
    cursor.execute('''CREATE TABLE IF NOT EXISTS clients (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        subscription_from TEXT,
                        subscription_to TEXT,
                        amount_paid REAL,
                        notes TEXT
                    )''')
    conn.commit()
    conn.close()

def format_client(c):
    return {
        'id': c[0],
        'name': c[1],
        'from': datetime.strptime(c[2], "%Y-%m-%d").strftime("%d-%m-%Y"),
        'to': datetime.strptime(c[3], "%Y-%m-%d").strftime("%d-%m-%Y"),
        'amount': f"{c[4]:,.0f} EGP",
        'notes': c[5]
    }

@app.route('/')
def index():
    today = datetime.now().date()
    conn = sqlite3.connect(DATABASE)
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM clients")
    all_clients = cursor.fetchall()
    conn.close()

    active = []
    expiring = []
    expired = []

    for c in all_clients:
        sub_to = datetime.strptime(c[3], "%Y-%m-%d").date()
        days_left = (sub_to - today).days
        if days_left >= 0:
            active.append(format_client(c))
        if 0 <= days_left <= 7:
            expiring.append(format_client(c))
        if days_left < 0:
            expired.append(format_client(c))

    return render_template('index.html',
                           active_count=len(active),
                           expiring_count=len(expiring),
                           expired_count=len(expired))

@app.route('/active')
def view_active():
    today = datetime.now().date()
    conn = sqlite3.connect(DATABASE)
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM clients")
    all_clients = cursor.fetchall()
    conn.close()
    active = [format_client(c) for c in all_clients if datetime.strptime(c[3], "%Y-%m-%d").date() >= today]
    return render_template('clients.html', clients=active)

@app.route('/expiring')
def view_expiring():
    today = datetime.now().date()
    conn = sqlite3.connect(DATABASE)
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM clients")
    all_clients = cursor.fetchall()
    conn.close()

    expiring = []
    for c in all_clients:
        sub_to = datetime.strptime(c[3], "%Y-%m-%d").date()
        days_left = (sub_to - today).days
        if 0 <= days_left <= 7:
            expiring.append(format_client(c))

    return render_template('clients.html', clients=expiring)

@app.route('/add', methods=['GET', 'POST'])
def add_client():
    if request.method == 'POST':
        name = request.form['name']
        sub_from = request.form['subscription_from']
        sub_to = request.form['subscription_to']
        amount = request.form['amount_paid']
        notes = request.form['notes']

        conn = sqlite3.connect(DATABASE)
        cursor = conn.cursor()
        cursor.execute("INSERT INTO clients (name, subscription_from, subscription_to, amount_paid, notes) VALUES (?, ?, ?, ?, ?)",
                       (name, sub_from, sub_to, amount, notes))
        conn.commit()
        conn.close()
        return redirect(url_for('list_clients'))
    return render_template('add_client.html')

@app.route('/clients')
def list_clients():
    conn = sqlite3.connect(DATABASE)
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM clients")
    raw_clients = cursor.fetchall()
    clients = [format_client(c) for c in raw_clients]
    conn.close()
    return render_template('clients.html', clients=clients)

@app.route('/edit/<int:id>', methods=['GET', 'POST'])
def edit_client(id):
    conn = sqlite3.connect(DATABASE)
    cursor = conn.cursor()
    if request.method == 'POST':
        name = request.form['name']
        sub_from = request.form['subscription_from']
        sub_to = request.form['subscription_to']
        amount = request.form['amount_paid']
        notes = request.form['notes']
        cursor.execute("UPDATE clients SET name=?, subscription_from=?, subscription_to=?, amount_paid=?, notes=? WHERE id=?",
                       (name, sub_from, sub_to, amount, notes, id))
        conn.commit()
        conn.close()
        return redirect(url_for('list_clients'))

    cursor.execute("SELECT * FROM clients WHERE id=?", (id,))
    c = cursor.fetchone()
    client = {
        'id': c[0],
        'name': c[1],
        'from': c[2],
        'to': c[3],
        'amount': c[4],
        'notes': c[5]
    }
    conn.close()
    return render_template('edit_client.html', client=client)

@app.route('/delete/<int:id>')
def delete_client(id):
    conn = sqlite3.connect(DATABASE)
    cursor = conn.cursor()
    cursor.execute("DELETE FROM clients WHERE id=?", (id,))
    conn.commit()
    conn.close()
    return redirect(url_for('list_clients'))

@app.route('/search', methods=['GET', 'POST'])
def search():
    results = []
    if request.method == 'POST':
        query = request.form['query']
        conn = sqlite3.connect(DATABASE)
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM clients WHERE name LIKE ?", ('%' + query + '%',))
        raw_results = cursor.fetchall()
        results = [format_client(r) for r in raw_results]
        conn.close()
    return render_template('search.html', results=results)

def open_browser():
    webbrowser.open("http://127.0.0.1:5000")

if __name__ == '__main__':
    init_db()
    threading.Timer(1.0, open_browser).start()
    app.run(debug=False)

