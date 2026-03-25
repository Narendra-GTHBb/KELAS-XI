# MuscleCart MySQL Database (phpMyAdmin / XAMPP)

## Setup

1. Open **XAMPP** and start **Apache** and **MySQL**.
2. Open **phpMyAdmin** in your browser: `http://localhost/phpmyadmin`
3. Go to **Import** (or **SQL** tab).
4. Select the file `musclecart_mysql.sql` from this folder and run it.

This will create:

- Database: `musclecart_db`
- Tables: `users`, `categories`, `products`, `orders`, `order_items`, `cart_items`
- Sample categories and products

## Optional: PHP API

The Android app is built to work **offline-first** with local Room database.  
If you later add a PHP API on XAMPP, point the app’s base URL to your server (e.g. `http://10.0.2.2/musclecart-api/` for emulator).
