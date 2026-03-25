-- MuscleCart MySQL Database for phpMyAdmin (XAMPP)
-- Run this in phpMyAdmin to create the database and tables

CREATE DATABASE IF NOT EXISTS musclecart_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE musclecart_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255),
  phone VARCHAR(50),
  address TEXT,
  is_admin TINYINT(1) DEFAULT 0,
  created_at BIGINT DEFAULT (UNIX_TIMESTAMP() * 1000),
  updated_at BIGINT DEFAULT (UNIX_TIMESTAMP() * 1000)
);

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  image_url VARCHAR(500),
  created_at BIGINT DEFAULT (UNIX_TIMESTAMP() * 1000),
  updated_at BIGINT DEFAULT (UNIX_TIMESTAMP() * 1000)
);

-- Products table
CREATE TABLE IF NOT EXISTS products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  price DECIMAL(10, 2) NOT NULL,
  image_url VARCHAR(500),
  category_id INT NOT NULL,
  stock_quantity INT DEFAULT 0,
  is_active TINYINT(1) DEFAULT 1,
  created_at BIGINT DEFAULT (UNIX_TIMESTAMP() * 1000),
  updated_at BIGINT DEFAULT (UNIX_TIMESTAMP() * 1000),
  FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
  INDEX idx_category (category_id),
  INDEX idx_name (name),
  INDEX idx_price (price)
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  total_amount DECIMAL(12, 2) NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
  shipping_address TEXT NOT NULL,
  payment_method VARCHAR(100) DEFAULT 'CASH',
  notes TEXT,
  created_at BIGINT DEFAULT (UNIX_TIMESTAMP() * 1000),
  updated_at BIGINT DEFAULT (UNIX_TIMESTAMP() * 1000),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user (user_id),
  INDEX idx_status (status),
  INDEX idx_created (created_at)
);

-- Order items table
CREATE TABLE IF NOT EXISTS order_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  product_id INT NOT NULL,
  quantity INT NOT NULL,
  price DECIMAL(10, 2) NOT NULL,
  created_at BIGINT DEFAULT (UNIX_TIMESTAMP() * 1000),
  updated_at BIGINT DEFAULT (UNIX_TIMESTAMP() * 1000),
  FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(id),
  INDEX idx_order (order_id)
);

-- Cart items table (for optional server-side cart sync)
CREATE TABLE IF NOT EXISTS cart_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  product_id INT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  added_at BIGINT DEFAULT (UNIX_TIMESTAMP() * 1000),
  updated_at BIGINT DEFAULT (UNIX_TIMESTAMP() * 1000),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  UNIQUE KEY uk_user_product (user_id, product_id),
  INDEX idx_user (user_id)
);

-- Seed default categories
INSERT INTO categories (id, name, description, image_url) VALUES
(1, 'Cardio Equipment', 'Treadmills, bikes, and other cardio machines', 'https://example.com/cardio.jpg'),
(2, 'Strength Training', 'Weights, barbells, and strength equipment', 'https://example.com/strength.jpg'),
(3, 'Supplements', 'Protein powders, vitamins, and supplements', 'https://example.com/supplements.jpg'),
(4, 'Accessories', 'Gym bags, water bottles, and accessories', 'https://example.com/accessories.jpg')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Seed sample products
INSERT INTO products (id, name, description, price, image_url, category_id, stock_quantity) VALUES
(1, 'Professional Treadmill', 'High-quality treadmill for home and commercial use', 1299.99, 'https://example.com/treadmill.jpg', 1, 15),
(2, 'Exercise Bike', 'Stationary bike with adjustable resistance', 599.99, 'https://example.com/bike.jpg', 1, 25),
(3, 'Olympic Barbell Set', 'Complete barbell set with plates', 899.99, 'https://example.com/barbell.jpg', 2, 10),
(4, 'Adjustable Dumbbells', 'Space-saving adjustable dumbbells', 399.99, 'https://example.com/dumbbells.jpg', 2, 30),
(5, 'Power Rack', 'Heavy-duty power rack for squats and bench', 1599.99, 'https://example.com/power-rack.jpg', 2, 5)
ON DUPLICATE KEY UPDATE name = VALUES(name), stock_quantity = VALUES(stock_quantity);
