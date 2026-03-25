-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 25 Feb 2026 pada 15.36
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `musclecart_db`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `cache`
--

CREATE TABLE `cache` (
  `key` varchar(255) NOT NULL,
  `value` mediumtext NOT NULL,
  `expiration` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `cache_locks`
--

CREATE TABLE `cache_locks` (
  `key` varchar(255) NOT NULL,
  `owner` varchar(255) NOT NULL,
  `expiration` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `cart_items`
--

CREATE TABLE `cart_items` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `product_id` bigint(20) UNSIGNED NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `price` decimal(10,2) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `cart_items`
--

INSERT INTO `cart_items` (`id`, `user_id`, `product_id`, `quantity`, `price`, `created_at`, `updated_at`) VALUES
(1, 8, 45, 5, 274000.00, '2026-02-24 22:25:33', '2026-02-24 22:26:53'),
(2, 8, 46, 1, 294000.00, '2026-02-25 00:00:47', '2026-02-25 00:00:47'),
(3, 8, 47, 2, 884000.00, '2026-02-25 00:06:33', '2026-02-25 00:07:17');

-- --------------------------------------------------------

--
-- Struktur dari tabel `categories`
--

CREATE TABLE `categories` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `categories`
--

INSERT INTO `categories` (`id`, `name`, `description`, `is_active`, `created_at`, `updated_at`) VALUES
(25, 'Cardio Equipment', 'Cardiovascular exercise machines for heart health and endurance', 1, '2026-02-22 10:38:21', '2026-02-22 10:38:21'),
(26, 'Strength Training', 'Weight training equipment for building muscle and strength', 1, '2026-02-22 10:38:21', '2026-02-22 10:38:21'),
(27, 'Free Weights', 'Dumbbells, barbells, and weight plates for versatile workouts', 1, '2026-02-22 10:38:21', '2026-02-22 10:38:21'),
(28, 'Fitness Accessories', 'Supporting equipment for enhanced workout experience', 1, '2026-02-22 10:38:21', '2026-02-22 10:38:21'),
(29, 'Home Gym', 'Complete gym solutions for home fitness enthusiasts', 1, '2026-02-22 10:38:21', '2026-02-22 10:38:21'),
(30, 'Supplements', 'Nutritional supplements for fitness and health goals', 1, '2026-02-22 10:38:21', '2026-02-22 10:38:21'),
(31, 'Yoga & Pilates', 'Equipment for mindful movement and flexibility training', 1, '2026-02-22 10:38:21', '2026-02-22 11:06:48'),
(32, 'Outdoor Fitness', 'Equipment for outdoor workouts and adventures', 1, '2026-02-22 10:38:21', '2026-02-22 10:38:21');

-- --------------------------------------------------------

--
-- Struktur dari tabel `failed_jobs`
--

CREATE TABLE `failed_jobs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `connection` text NOT NULL,
  `queue` text NOT NULL,
  `payload` longtext NOT NULL,
  `exception` longtext NOT NULL,
  `failed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `favorites`
--

CREATE TABLE `favorites` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `product_id` bigint(20) UNSIGNED NOT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `updated_at` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `favorites`
--

INSERT INTO `favorites` (`id`, `user_id`, `product_id`, `created_at`, `updated_at`) VALUES
(7, 8, 45, NULL, NULL),
(8, 8, 46, NULL, NULL),
(9, 8, 47, NULL, NULL),
(10, 8, 48, NULL, NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `jobs`
--

CREATE TABLE `jobs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `queue` varchar(255) NOT NULL,
  `payload` longtext NOT NULL,
  `attempts` tinyint(3) UNSIGNED NOT NULL,
  `reserved_at` int(10) UNSIGNED DEFAULT NULL,
  `available_at` int(10) UNSIGNED NOT NULL,
  `created_at` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `job_batches`
--

CREATE TABLE `job_batches` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `total_jobs` int(11) NOT NULL,
  `pending_jobs` int(11) NOT NULL,
  `failed_jobs` int(11) NOT NULL,
  `failed_job_ids` longtext NOT NULL,
  `options` mediumtext DEFAULT NULL,
  `cancelled_at` int(11) DEFAULT NULL,
  `created_at` int(11) NOT NULL,
  `finished_at` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `migrations`
--

CREATE TABLE `migrations` (
  `id` int(10) UNSIGNED NOT NULL,
  `migration` varchar(255) NOT NULL,
  `batch` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `migrations`
--

INSERT INTO `migrations` (`id`, `migration`, `batch`) VALUES
(1, '0001_01_01_000000_create_users_table', 1),
(2, '0001_01_01_000001_create_cache_table', 1),
(3, '0001_01_01_000002_create_jobs_table', 1),
(4, '2026_02_18_115434_create_categories_table', 1),
(5, '2026_02_18_115441_create_products_table', 1),
(6, '2026_02_18_115442_create_orders_table', 1),
(7, '2026_02_18_115443_create_cart_items_table', 1),
(8, '2026_02_18_115444_create_order_items_table', 1),
(9, '2026_02_18_120416_create_personal_access_tokens_table', 1),
(10, '2026_02_20_074026_remove_image_from_categories_table', 2),
(11, '2026_02_21_074250_add_low_stock_threshold_to_products_table', 3),
(12, '2026_02_21_190517_update_products_weight_precision', 4),
(13, '2026_02_18_115441_create_cart_items_table', 5),
(14, '2026_02_18_115442_create_order_items_table', 5),
(15, '2026_02_25_000004_create_favorites_table', 5);

-- --------------------------------------------------------

--
-- Struktur dari tabel `orders`
--

CREATE TABLE `orders` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `order_number` varchar(255) NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `tax_amount` decimal(10,2) NOT NULL DEFAULT 0.00,
  `shipping_amount` decimal(10,2) NOT NULL DEFAULT 0.00,
  `status` enum('pending','confirmed','processing','shipped','delivered','cancelled') NOT NULL DEFAULT 'pending',
  `payment_status` enum('pending','paid','failed','refunded') NOT NULL DEFAULT 'pending',
  `payment_method` enum('cash','transfer','credit_card','e_wallet') NOT NULL,
  `shipping_address` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`shipping_address`)),
  `billing_address` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`billing_address`)),
  `notes` text DEFAULT NULL,
  `shipped_at` timestamp NULL DEFAULT NULL,
  `delivered_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `orders`
--

INSERT INTO `orders` (`id`, `order_number`, `user_id`, `total_amount`, `tax_amount`, `shipping_amount`, `status`, `payment_status`, `payment_method`, `shipping_address`, `billing_address`, `notes`, `shipped_at`, `delivered_at`, `created_at`, `updated_at`) VALUES
(1, 'ORD-20260219-0001', 2, 2195000.00, 0.00, 0.00, 'pending', 'paid', 'cash', '\"{\\\"name\\\":\\\"Customer 1\\\",\\\"phone\\\":\\\"081234567891\\\",\\\"address\\\":\\\"Address for Customer 1\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"postal_code\\\":\\\"12345\\\"}\"', '\"{\\\"name\\\":\\\"Customer 1\\\",\\\"phone\\\":\\\"081234567891\\\",\\\"address\\\":\\\"Address for Customer 1\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"postal_code\\\":\\\"12345\\\"}\"', NULL, NULL, NULL, '2026-02-19 05:47:53', '2026-02-19 05:47:53'),
(2, 'ORD-20260219-0002', 3, 1296000.00, 0.00, 0.00, 'delivered', 'pending', 'cash', '\"{\\\"name\\\":\\\"Customer 2\\\",\\\"phone\\\":\\\"081234567892\\\",\\\"address\\\":\\\"Address for Customer 2\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"postal_code\\\":\\\"12345\\\"}\"', '\"{\\\"name\\\":\\\"Customer 2\\\",\\\"phone\\\":\\\"081234567892\\\",\\\"address\\\":\\\"Address for Customer 2\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"postal_code\\\":\\\"12345\\\"}\"', NULL, NULL, NULL, '2026-02-19 05:47:53', '2026-02-19 05:47:53'),
(3, 'ORD-20260219-0003', 4, 1747000.00, 0.00, 0.00, 'processing', 'pending', 'e_wallet', '\"{\\\"name\\\":\\\"Customer 3\\\",\\\"phone\\\":\\\"081234567893\\\",\\\"address\\\":\\\"Address for Customer 3\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"postal_code\\\":\\\"12345\\\"}\"', '\"{\\\"name\\\":\\\"Customer 3\\\",\\\"phone\\\":\\\"081234567893\\\",\\\"address\\\":\\\"Address for Customer 3\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"postal_code\\\":\\\"12345\\\"}\"', NULL, NULL, NULL, '2026-02-19 05:47:53', '2026-02-19 05:47:53'),
(4, 'ORD-20260221-00001', 3, 5962.44, 541.22, 9.00, 'delivered', 'paid', 'transfer', '\"{\\\"name\\\":\\\"Customer 2\\\",\\\"phone\\\":\\\"0836365660\\\",\\\"address\\\":\\\"130 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"77344\\\"}\"', '\"{\\\"name\\\":\\\"Customer 2\\\",\\\"phone\\\":\\\"0836365660\\\",\\\"address\\\":\\\"130 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"77344\\\"}\"', 'Please deliver between 9 AM - 5 PM', '2026-02-04 01:17:58', '2026-02-19 01:17:58', '2025-12-14 01:17:58', '2026-02-01 01:17:58'),
(6, 'ORD-20260221-69996A5E358D3', 1, 7082.54, 642.32, 17.00, 'pending', 'pending', 'transfer', '\"{\\\"name\\\":\\\"Admin MuscleCart\\\",\\\"phone\\\":\\\"0889643631\\\",\\\"address\\\":\\\"876 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"57375\\\"}\"', '\"{\\\"name\\\":\\\"Admin MuscleCart\\\",\\\"phone\\\":\\\"0889643631\\\",\\\"address\\\":\\\"876 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"57375\\\"}\"', NULL, NULL, NULL, '2025-11-23 01:18:38', '2026-02-09 01:18:38'),
(7, 'ORD-20260221-69996A5E3B58D', 1, 282.97, 23.91, 20.00, 'shipped', 'pending', 'transfer', '\"{\\\"name\\\":\\\"Admin MuscleCart\\\",\\\"phone\\\":\\\"0827348812\\\",\\\"address\\\":\\\"956 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"94909\\\"}\"', '\"{\\\"name\\\":\\\"Admin MuscleCart\\\",\\\"phone\\\":\\\"0827348812\\\",\\\"address\\\":\\\"956 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"94909\\\"}\"', 'Please deliver between 9 AM - 5 PM', '2026-02-15 01:18:38', NULL, '2025-12-31 01:18:38', '2026-02-15 01:18:38'),
(8, 'ORD-20260221-69996A5E3CD4B', 1, 665.98, 60.00, 6.00, 'delivered', 'paid', 'transfer', '\"{\\\"name\\\":\\\"Admin MuscleCart\\\",\\\"phone\\\":\\\"0876287240\\\",\\\"address\\\":\\\"502 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"81283\\\"}\"', '\"{\\\"name\\\":\\\"Admin MuscleCart\\\",\\\"phone\\\":\\\"0876287240\\\",\\\"address\\\":\\\"502 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"81283\\\"}\"', NULL, '2026-02-17 01:18:38', '2026-02-11 01:18:38', '2026-01-27 01:18:38', '2026-01-22 01:18:38'),
(9, 'ORD-20260221-69996A5E3E4C4', 2, 2184.38, 197.58, 11.00, 'pending', 'pending', 'credit_card', '\"{\\\"name\\\":\\\"Customer 1\\\",\\\"phone\\\":\\\"0880198563\\\",\\\"address\\\":\\\"636 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"20528\\\"}\"', '\"{\\\"name\\\":\\\"Customer 1\\\",\\\"phone\\\":\\\"0880198563\\\",\\\"address\\\":\\\"636 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"20528\\\"}\"', 'Please deliver between 9 AM - 5 PM', NULL, NULL, '2025-12-15 01:18:38', '2026-01-25 01:18:38'),
(10, 'ORD-20260221-69996A5E3FBDC', 1, 6226.08, 565.37, 7.00, 'cancelled', 'failed', 'transfer', '\"{\\\"name\\\":\\\"Admin MuscleCart\\\",\\\"phone\\\":\\\"0870210798\\\",\\\"address\\\":\\\"385 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"30746\\\"}\"', '\"{\\\"name\\\":\\\"Admin MuscleCart\\\",\\\"phone\\\":\\\"0870210798\\\",\\\"address\\\":\\\"385 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"30746\\\"}\"', NULL, NULL, NULL, '2026-01-18 01:18:38', '2026-01-31 01:18:38'),
(11, 'ORD-20260221-69996A5E43769', 3, 4737.23, 430.11, 6.00, 'cancelled', 'failed', 'credit_card', '\"{\\\"name\\\":\\\"Customer 2\\\",\\\"phone\\\":\\\"0856472138\\\",\\\"address\\\":\\\"600 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"91198\\\"}\"', '\"{\\\"name\\\":\\\"Customer 2\\\",\\\"phone\\\":\\\"0856472138\\\",\\\"address\\\":\\\"600 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"91198\\\"}\"', NULL, NULL, NULL, '2026-01-10 01:18:38', '2026-02-21 01:18:38'),
(12, 'ORD-20260221-69996A5E464E6', 5, 1611.00, 145.18, 14.00, 'cancelled', 'failed', 'credit_card', '\"{\\\"name\\\":\\\"Customer 4\\\",\\\"phone\\\":\\\"0832360509\\\",\\\"address\\\":\\\"189 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"57312\\\"}\"', '\"{\\\"name\\\":\\\"Customer 4\\\",\\\"phone\\\":\\\"0832360509\\\",\\\"address\\\":\\\"189 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"57312\\\"}\"', NULL, NULL, NULL, '2026-02-02 01:18:38', '2026-02-07 01:18:38'),
(13, 'ORD-20260221-69996A5E4858E', 1, 277.97, 23.91, 15.00, 'confirmed', 'pending', 'credit_card', '\"{\\\"name\\\":\\\"Admin MuscleCart\\\",\\\"phone\\\":\\\"0853150969\\\",\\\"address\\\":\\\"627 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"53855\\\"}\"', '\"{\\\"name\\\":\\\"Admin MuscleCart\\\",\\\"phone\\\":\\\"0853150969\\\",\\\"address\\\":\\\"627 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"53855\\\"}\"', NULL, NULL, NULL, '2025-12-16 01:18:38', '2026-01-27 01:18:38'),
(14, 'ORD-20260221-69996A5E49B5D', 4, 1405.59, 126.05, 19.00, 'shipped', 'pending', 'cash', '\"{\\\"name\\\":\\\"Customer 3\\\",\\\"phone\\\":\\\"0841860584\\\",\\\"address\\\":\\\"892 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"93871\\\"}\"', '\"{\\\"name\\\":\\\"Customer 3\\\",\\\"phone\\\":\\\"0841860584\\\",\\\"address\\\":\\\"892 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"93871\\\"}\"', 'Please deliver between 9 AM - 5 PM', '2026-02-09 01:18:38', NULL, '2025-12-24 01:18:38', '2026-02-17 01:18:38'),
(15, 'ORD-20260221-69996A5E4B28F', 3, 1568.41, 141.58, 11.00, 'shipped', 'pending', 'cash', '\"{\\\"name\\\":\\\"Customer 2\\\",\\\"phone\\\":\\\"0856179406\\\",\\\"address\\\":\\\"900 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"59252\\\"}\"', '\"{\\\"name\\\":\\\"Customer 2\\\",\\\"phone\\\":\\\"0856179406\\\",\\\"address\\\":\\\"900 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"59252\\\"}\"', NULL, '2026-02-06 01:18:38', NULL, '2026-01-22 01:18:38', '2026-01-25 01:18:38'),
(16, 'ORD-20260221-69996A5E4DF73', 5, 138.97, 12.00, 7.00, 'pending', 'pending', 'e_wallet', '\"{\\\"name\\\":\\\"Customer 4\\\",\\\"phone\\\":\\\"0864326364\\\",\\\"address\\\":\\\"831 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"80406\\\"}\"', '\"{\\\"name\\\":\\\"Customer 4\\\",\\\"phone\\\":\\\"0864326364\\\",\\\"address\\\":\\\"831 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"80406\\\"}\"', NULL, NULL, NULL, '2025-11-25 01:18:38', '2026-02-20 01:18:38'),
(17, 'ORD-20260221-69996A5E4F5F2', 1, 1654.12, 148.74, 18.00, 'pending', 'pending', 'e_wallet', '\"{\\\"name\\\":\\\"Admin MuscleCart\\\",\\\"phone\\\":\\\"0899589192\\\",\\\"address\\\":\\\"737 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"81740\\\"}\"', '\"{\\\"name\\\":\\\"Admin MuscleCart\\\",\\\"phone\\\":\\\"0899589192\\\",\\\"address\\\":\\\"737 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"81740\\\"}\"', 'Please deliver between 9 AM - 5 PM', NULL, NULL, '2026-01-13 01:18:38', '2026-02-11 01:18:38'),
(18, 'ORD-20260221-69996A5E523D2', 6, 1012.95, 90.63, 16.00, 'processing', 'pending', 'transfer', '\"{\\\"name\\\":\\\"Customer 5\\\",\\\"phone\\\":\\\"0844085349\\\",\\\"address\\\":\\\"970 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"30741\\\"}\"', '\"{\\\"name\\\":\\\"Customer 5\\\",\\\"phone\\\":\\\"0844085349\\\",\\\"address\\\":\\\"970 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"30741\\\"}\"', NULL, NULL, NULL, '2026-02-14 01:18:38', '2026-02-20 01:18:38'),
(19, 'ORD-20260221-69996A5E54470', 2, 5790.32, 525.76, 7.00, 'confirmed', 'pending', 'credit_card', '\"{\\\"name\\\":\\\"Customer 1\\\",\\\"phone\\\":\\\"0829584723\\\",\\\"address\\\":\\\"107 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"44074\\\"}\"', '\"{\\\"name\\\":\\\"Customer 1\\\",\\\"phone\\\":\\\"0829584723\\\",\\\"address\\\":\\\"107 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"44074\\\"}\"', NULL, NULL, NULL, '2026-02-08 01:18:38', '2026-02-03 01:18:38'),
(20, 'ORD-20260221-69996A5E571D0', 3, 4212.51, 382.05, 10.00, 'shipped', 'pending', 'credit_card', '\"{\\\"name\\\":\\\"Customer 2\\\",\\\"phone\\\":\\\"0846686777\\\",\\\"address\\\":\\\"779 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"83661\\\"}\"', '\"{\\\"name\\\":\\\"Customer 2\\\",\\\"phone\\\":\\\"0846686777\\\",\\\"address\\\":\\\"779 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"83661\\\"}\"', NULL, '2026-02-16 01:18:38', NULL, '2025-12-10 01:18:38', '2026-02-21 01:18:38'),
(21, 'ORD-20260221-69996A5E59CC9', 6, 1195.85, 107.99, 8.00, 'processing', 'pending', 'e_wallet', '\"{\\\"name\\\":\\\"Customer 5\\\",\\\"phone\\\":\\\"0876586169\\\",\\\"address\\\":\\\"828 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"10705\\\"}\"', '\"{\\\"name\\\":\\\"Customer 5\\\",\\\"phone\\\":\\\"0876586169\\\",\\\"address\\\":\\\"828 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"10705\\\"}\"', NULL, NULL, NULL, '2025-12-18 01:18:38', '2026-01-25 01:18:38'),
(22, 'ORD-20260221-69996A5E5B30C', 6, 1874.50, 169.41, 11.00, 'processing', 'pending', 'transfer', '\"{\\\"name\\\":\\\"Customer 5\\\",\\\"phone\\\":\\\"0840733261\\\",\\\"address\\\":\\\"385 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"18673\\\"}\"', '\"{\\\"name\\\":\\\"Customer 5\\\",\\\"phone\\\":\\\"0840733261\\\",\\\"address\\\":\\\"385 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"18673\\\"}\"', NULL, NULL, NULL, '2026-02-05 01:18:38', '2026-02-12 01:18:38'),
(23, 'ORD-20260221-69996A5E5D4BC', 5, 2171.84, 196.62, 9.00, 'confirmed', 'pending', 'transfer', '\"{\\\"name\\\":\\\"Customer 4\\\",\\\"phone\\\":\\\"0842166857\\\",\\\"address\\\":\\\"736 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"70718\\\"}\"', '\"{\\\"name\\\":\\\"Customer 4\\\",\\\"phone\\\":\\\"0842166857\\\",\\\"address\\\":\\\"736 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"70718\\\"}\"', 'Please deliver between 9 AM - 5 PM', NULL, NULL, '2026-01-29 01:18:38', '2026-02-17 01:18:38'),
(24, 'ORD-20260221-69996A5E5F5B8', 4, 2811.21, 255.11, 5.00, 'delivered', 'paid', 'credit_card', '\"{\\\"name\\\":\\\"Customer 3\\\",\\\"phone\\\":\\\"0817967763\\\",\\\"address\\\":\\\"722 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"94465\\\"}\"', '\"{\\\"name\\\":\\\"Customer 3\\\",\\\"phone\\\":\\\"0817967763\\\",\\\"address\\\":\\\"722 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"94465\\\"}\"', 'Please deliver between 9 AM - 5 PM', '2026-02-10 01:18:38', '2026-02-20 01:18:38', '2026-01-20 01:18:38', '2026-02-13 01:18:38'),
(25, 'ORD-20260221-69996A5E62CE7', 6, 3830.78, 347.34, 10.00, 'delivered', 'paid', 'credit_card', '\"{\\\"name\\\":\\\"Customer 5\\\",\\\"phone\\\":\\\"0868600805\\\",\\\"address\\\":\\\"106 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"76303\\\"}\"', '\"{\\\"name\\\":\\\"Customer 5\\\",\\\"phone\\\":\\\"0868600805\\\",\\\"address\\\":\\\"106 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"76303\\\"}\"', 'Please deliver between 9 AM - 5 PM', '2026-02-03 01:18:38', '2026-02-19 01:18:38', '2025-11-23 01:18:38', '2026-01-28 01:18:38'),
(26, 'ORD-20260221-69996A5E658AC', 6, 1143.42, 103.13, 9.00, 'confirmed', 'pending', 'cash', '\"{\\\"name\\\":\\\"Customer 5\\\",\\\"phone\\\":\\\"0839835642\\\",\\\"address\\\":\\\"147 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"61252\\\"}\"', '\"{\\\"name\\\":\\\"Customer 5\\\",\\\"phone\\\":\\\"0839835642\\\",\\\"address\\\":\\\"147 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"61252\\\"}\"', 'Please deliver between 9 AM - 5 PM', NULL, NULL, '2025-12-28 01:18:38', '2026-02-07 01:18:38'),
(27, 'ORD-20260221-69996A5E67A52', 1, 932.40, 84.04, 8.00, 'confirmed', 'pending', 'transfer', '\"{\\\"name\\\":\\\"Admin MuscleCart\\\",\\\"phone\\\":\\\"0861657995\\\",\\\"address\\\":\\\"419 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"46808\\\"}\"', '\"{\\\"name\\\":\\\"Admin MuscleCart\\\",\\\"phone\\\":\\\"0861657995\\\",\\\"address\\\":\\\"419 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"46808\\\"}\"', 'Please deliver between 9 AM - 5 PM', NULL, NULL, '2025-11-29 01:18:38', '2026-01-24 01:18:38'),
(28, 'ORD-20260221-69996A5E6910B', 2, 2675.13, 242.01, 13.00, 'processing', 'pending', 'e_wallet', '\"{\\\"name\\\":\\\"Customer 1\\\",\\\"phone\\\":\\\"0826580295\\\",\\\"address\\\":\\\"165 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"67839\\\"}\"', '\"{\\\"name\\\":\\\"Customer 1\\\",\\\"phone\\\":\\\"0826580295\\\",\\\"address\\\":\\\"165 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"67839\\\"}\"', 'Please deliver between 9 AM - 5 PM', NULL, NULL, '2026-02-13 01:18:38', '2026-02-01 01:18:38'),
(29, 'ORD-20260221-69996A5E6BDD7', 2, 6011.01, 545.91, 6.00, 'pending', 'pending', 'cash', '\"{\\\"name\\\":\\\"Customer 1\\\",\\\"phone\\\":\\\"0849537096\\\",\\\"address\\\":\\\"138 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"56101\\\"}\"', '\"{\\\"name\\\":\\\"Customer 1\\\",\\\"phone\\\":\\\"0849537096\\\",\\\"address\\\":\\\"138 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"56101\\\"}\"', NULL, NULL, NULL, '2025-12-06 01:18:38', '2026-02-10 01:18:38'),
(30, 'ORD-20260221-69996A5E6F535', 3, 3929.98, 356.45, 9.00, 'cancelled', 'failed', 'transfer', '\"{\\\"name\\\":\\\"Customer 2\\\",\\\"phone\\\":\\\"0852118040\\\",\\\"address\\\":\\\"663 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"91535\\\"}\"', '\"{\\\"name\\\":\\\"Customer 2\\\",\\\"phone\\\":\\\"0852118040\\\",\\\"address\\\":\\\"663 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"91535\\\"}\"', NULL, NULL, NULL, '2026-01-26 01:18:38', '2026-01-26 01:18:38'),
(31, 'ORD-20260221-69996A5E716F7', 3, 147.48, 11.95, 16.00, 'shipped', 'pending', 'cash', '\"{\\\"name\\\":\\\"Customer 2\\\",\\\"phone\\\":\\\"0890619019\\\",\\\"address\\\":\\\"316 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"24482\\\"}\"', '\"{\\\"name\\\":\\\"Customer 2\\\",\\\"phone\\\":\\\"0890619019\\\",\\\"address\\\":\\\"316 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"24482\\\"}\"', NULL, '2026-02-06 01:18:38', NULL, '2025-12-01 01:18:38', '2026-02-16 01:18:38'),
(32, 'ORD-20260221-69996A5E72DCD', 2, 1058.11, 94.56, 18.00, 'cancelled', 'failed', 'credit_card', '\"{\\\"name\\\":\\\"Customer 1\\\",\\\"phone\\\":\\\"0813218244\\\",\\\"address\\\":\\\"621 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"93669\\\"}\"', '\"{\\\"name\\\":\\\"Customer 1\\\",\\\"phone\\\":\\\"0813218244\\\",\\\"address\\\":\\\"621 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"93669\\\"}\"', NULL, NULL, NULL, '2026-02-13 01:18:38', '2026-02-13 01:18:38'),
(33, 'ORD-20260221-69996A5E74420', 6, 2506.18, 226.38, 16.00, 'cancelled', 'failed', 'e_wallet', '\"{\\\"name\\\":\\\"Customer 5\\\",\\\"phone\\\":\\\"0818437143\\\",\\\"address\\\":\\\"128 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"37913\\\"}\"', '\"{\\\"name\\\":\\\"Customer 5\\\",\\\"phone\\\":\\\"0818437143\\\",\\\"address\\\":\\\"128 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"37913\\\"}\"', NULL, NULL, NULL, '2026-01-08 01:18:38', '2026-02-05 01:18:38'),
(34, 'ORD-20260221-69996A5E770E8', 6, 1807.77, 163.43, 10.00, 'shipped', 'pending', 'e_wallet', '\"{\\\"name\\\":\\\"Customer 5\\\",\\\"phone\\\":\\\"0880137996\\\",\\\"address\\\":\\\"374 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"79642\\\"}\"', '\"{\\\"name\\\":\\\"Customer 5\\\",\\\"phone\\\":\\\"0880137996\\\",\\\"address\\\":\\\"374 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"79642\\\"}\"', 'Please deliver between 9 AM - 5 PM', '2026-02-01 01:18:38', NULL, '2025-12-29 01:18:38', '2026-02-09 01:18:38'),
(35, 'ORD-20260221-69996A5E787AE', 5, 2248.50, 203.59, 9.00, 'processing', 'pending', 'transfer', '\"{\\\"name\\\":\\\"Customer 4\\\",\\\"phone\\\":\\\"0822814428\\\",\\\"address\\\":\\\"312 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"39392\\\"}\"', '\"{\\\"name\\\":\\\"Customer 4\\\",\\\"phone\\\":\\\"0822814428\\\",\\\"address\\\":\\\"312 Main Street\\\",\\\"city\\\":\\\"Jakarta\\\",\\\"state\\\":\\\"DKI Jakarta\\\",\\\"postal_code\\\":\\\"39392\\\"}\"', NULL, NULL, NULL, '2026-02-02 01:18:38', '2026-02-06 01:18:38');

-- --------------------------------------------------------

--
-- Struktur dari tabel `order_items`
--

CREATE TABLE `order_items` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `order_id` bigint(20) UNSIGNED NOT NULL,
  `product_id` bigint(20) UNSIGNED NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `password_reset_tokens`
--

CREATE TABLE `password_reset_tokens` (
  `email` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `personal_access_tokens`
--

CREATE TABLE `personal_access_tokens` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `tokenable_type` varchar(255) NOT NULL,
  `tokenable_id` bigint(20) UNSIGNED NOT NULL,
  `name` text NOT NULL,
  `token` varchar(64) NOT NULL,
  `abilities` text DEFAULT NULL,
  `last_used_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `personal_access_tokens`
--

INSERT INTO `personal_access_tokens` (`id`, `tokenable_type`, `tokenable_id`, `name`, `token`, `abilities`, `last_used_at`, `expires_at`, `created_at`, `updated_at`) VALUES
(7, 'App\\Models\\User', 8, 'auth-token', 'fa04155bebfbebdc9870808b3427f9ef51d1d5c136c79870ba97bf06a5a7adde', '[\"*\"]', '2026-02-25 00:07:20', NULL, '2026-02-25 00:00:32', '2026-02-25 00:07:20');

-- --------------------------------------------------------

--
-- Struktur dari tabel `products`
--

CREATE TABLE `products` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock_quantity` int(11) NOT NULL DEFAULT 0,
  `low_stock_threshold` int(11) DEFAULT 5,
  `image_url` varchar(255) DEFAULT NULL,
  `gallery` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`gallery`)),
  `category_id` bigint(20) UNSIGNED NOT NULL,
  `brand` varchar(255) DEFAULT NULL,
  `weight` decimal(8,3) DEFAULT NULL,
  `specifications` text DEFAULT NULL,
  `is_featured` tinyint(1) NOT NULL DEFAULT 0,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `products`
--

INSERT INTO `products` (`id`, `name`, `description`, `price`, `stock_quantity`, `low_stock_threshold`, `image_url`, `gallery`, `category_id`, `brand`, `weight`, `specifications`, `is_featured`, `is_active`, `created_at`, `updated_at`) VALUES
(45, 'Evolene Evomass 2lbs/912gr - Mass Gainer - Suplemen Fitness', 'Evomass merupakan gainer tinggi protein yang cocok untuk bantu kamu menaikkan berat badan dan massa otot sehingga badan lebih berisi.\r\n\r\n\r\n\r\nDengan kandungan tinggi protein, tinggi kalori namun rendah lemak, Evomass adalah solusi tepat untuk bantu kamu wujudkan badan berisi bebas perut buncit\r\n\r\n\r\n\r\nEvomass akan bantu kamu:\r\n\r\n- Menambah berat badan sehingga badan jadi berisi\r\n\r\n- Menambah massa otot\r\n\r\n- Mempermudah program surplus kalori\r\n\r\n- Maksimalkan program bulking', 274000.00, 10, 5, 'products/0C7C1oepAZ8WBJY3Rkgo7nwNdxuPLcOb5f6Q74WT.webp', NULL, 30, 'EVOLENE', 0.912, NULL, 0, 1, '2026-02-22 10:38:29', '2026-02-23 10:53:41'),
(46, 'Evolene Isolene 12 Sachet/396gr - Whey Protein Isolate - Suplemen Fitness - Suplemen Workout', 'Isolene merupakan versi UPGRADE dari Whey Protein Concentrate yang sebelumnya diproduksi Evolene.\r\n\r\n\r\n\r\nKENAPA HARUS ISOLENE?\r\n\r\nProtein Paling Tinggi (27 gr/serving) dapat mencukupi kebutuhan Protein harian.. \r\n\r\nPaling Rendah Lemak (0,5 gr/serving), Bermanfaat untuk pembentukan otot bebas lemak.\r\n\r\nLebih Mudah Diserap Menurut US National Library of Medicine National Institutes of Health, Whey Concentrate diserap dalam waktu 2-3 jam. Sedangkan, Whey Isolate (ISOLENE) dapat diserap dalam waktu satu jam saja.\r\n\r\nBebas Laktosa Isolene memiliki kandungan yang bebas laktosa dan cocok buat kamu yang alergi susu.\r\n\r\nLebih Rendah Gula Kandungan gula yang rendah cocok buat kamu yang mau jadi atlet elit yang butuh protein berkualitas tinggi.\r\n\r\nUji Lab, Monitoring secara berkala guna menjamin kualitas nutrisi sesuai dengan Nutrition fact di setiap produk.\r\n\r\nBentuk Sachet, Mempermudah dalam penyajian produk dan memastikan kecukupan nutrisi sesuai dengan kebutuhan tubuh. \r\n\r\n\r\n\r\nKemasan:\r\n\r\n396gr atau 12 Serving @228gr/Sachet/Sajian\r\n\r\n\r\n\r\nSangat direkomendasikan untuk pria dan wanita yang: \r\n\r\nMemiliki alergi laktosa \r\n\r\nMenginginkan kualitas protein premium untuk mendukung program defisit kalori agar lebih maksimal. \r\n\r\nCocok untuk mendukung program Cutting. \r\n\r\nSangat cocok untuk support nutrisi pe-fitness pemula maupun expert.', 294000.00, 20, 5, 'products/9Lhy7sIgBj62wdu52EIikmIeStFssNjKsYUfmr09.webp', NULL, 30, 'EVOLENE', 0.396, NULL, 0, 1, '2026-02-22 10:38:29', '2026-02-23 10:55:27'),
(47, 'Evolene - Evowhey Protein 50S/1750gr - Suplemen Fitness - Suplemen Workout', 'Evowhey merupakan whey protein praktis untuk mendukung kebutuhan pembentukan badan dan ototmu. Cocok untuk pemula hingga expert.\r\n\r\n\r\n\r\nEvowhey diformulasikan untuk bantu kamu:\r\n\r\n- Mencukupi kebutuhan protein harian\r\n\r\n- Mengencangkan otot perut\r\n\r\n- Mempermudah program defisit kalori\r\n\r\n- Menjalani program cutting\r\n\r\n- Wujudkan badan ideal impian\r\n\r\n\r\n\r\n___________\r\n\r\n\r\n\r\nKeunggulan Evowhey:\r\n\r\n\r\n\r\n- Tinggi protein dengan kandungan 25 gr protein/ servingnya\r\n\r\n- Rendah kalori, hanya 140 kkal/ servingnya, kalorinya sangat rendah dibandingkan whey protein lain\r\n\r\n- Diperkaya premix vitamin yang dapat meningkatkan daya tahan tubuh\r\n\r\n- Mengandung BCAA 5,2 gr/ serving yang berfungsi membantu pertumbuhan otot, meredakan sakit dan nyeri saat olahraga\r\n\r\n- Kemasan sachetnya praktis, memudahkanmu untuk menyeduh kapan pun dan dimana pun\r\n\r\n\r\n\r\nFormulanya merupakan kombinasi dari whey concentrate dan whey isolate sehingga efektif untuk tubuh.\r\n\r\n___________', 884000.00, 35, 5, 'products/kLjgqTIDdSgq2xKmMNYmBaFU0QUnmHKFU8B37XeC.webp', NULL, 30, 'EVOLENE', 1.750, NULL, 0, 1, '2026-02-22 10:38:29', '2026-02-23 10:56:51'),
(48, 'Evolene - Crevolene Creapure - Creatine - Menambah Massa Otot Suplemen Fitness', 'Crevolene adalah produk creatine dari Evolene yang mampu membantumu meningkatkan POWER & STRENGTH untuk olahraga. Zat creatine telah terbukti mampu menjadi asupan yang tepat bagi otot karena kandungan yang terdapat di dalamnya.\r\n\r\n\r\n\r\nKEUNGGULAN CREVOLENE:\r\n\r\n5 gr creatine mampu meningkatkan ATP dan sumber energi pada otot juga menebalkan massa otot\r\n\r\nMudah larut & mudah diserap tubuh\r\n\r\nBisa digunakan bersama Isolene/ Evowhey/ Evomass\r\n\r\nRasa anggurnya nikmat & nyegerin\r\n\r\n\r\n\r\n_____________________________\r\n\r\n\r\n\r\n\r\n\r\nEvolene Raih Top Brand Award\r\n\r\n#1 BEST SELLER FITNESS SUPPLEMENT di Indonesia.\r\n\r\nSudah tersertifikasi BPOM, Halal MUI, HACCP dan GMP.\r\n\r\n\r\n\r\nSaran Penyajian:\r\n\r\nTakaran Pemakaian\r\n\r\nHari 1-4 = 4 scoop\r\n\r\nHari 5-15 = 1 scoop\r\n\r\nHari 16-30 = OFF\r\n\r\nSEDUH CREVOLENE DENGAN AIR DINGIN/ SUHU NORMAL SEBANYAK 20\r\n\r\n\r\n\r\n0 ML/ SCOOP', 224000.00, 15, 5, 'products/keALPzlvlxvxT1K7wnvSQdi33XwTyfP28LWBkYZu.png', NULL, 30, 'EVOLENE', 0.330, NULL, 0, 1, '2026-02-24 20:38:42', '2026-02-24 20:38:42');

-- --------------------------------------------------------

--
-- Struktur dari tabel `sessions`
--

CREATE TABLE `sessions` (
  `id` varchar(255) NOT NULL,
  `user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` text DEFAULT NULL,
  `payload` longtext NOT NULL,
  `last_activity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `sessions`
--

INSERT INTO `sessions` (`id`, `user_id`, `ip_address`, `user_agent`, `payload`, `last_activity`) VALUES
('3kQKoWroV6VISkvr9YjLixQc9OmwK8UbeAKsCDdg', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36', 'YTo1OntzOjY6Il90b2tlbiI7czo0MDoiMWRtS0hlNXlHWU9nOUhMYUhLUEtubG13VkdoaHowM0RqM2dMdEs2WiI7czo5OiJfcHJldmlvdXMiO2E6Mjp7czozOiJ1cmwiO3M6MjE6Imh0dHA6Ly8xMjcuMC4wLjE6ODAwMCI7czo1OiJyb3V0ZSI7Tjt9czo2OiJfZmxhc2giO2E6Mjp7czozOiJvbGQiO2E6MDp7fXM6MzoibmV3IjthOjA6e319czozOiJ1cmwiO2E6MDp7fXM6NTA6ImxvZ2luX3dlYl81OWJhMzZhZGRjMmIyZjk0MDE1ODBmMDE0YzdmNThlYTRlMzA5ODlkIjtpOjE7fQ==', 1771999682),
('jrYDPy3ZOtHzht192DhIWqu5iztps81nRpeo68GO', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', 'YTo1OntzOjY6Il90b2tlbiI7czo0MDoiZ2o5d1VtbjVtbHBRYzZDaVBlMUo2SXY5T0RCbkRlS2dpajNZS3g3SyI7czo5OiJfcHJldmlvdXMiO2E6Mjp7czozOiJ1cmwiO3M6MzY6Imh0dHA6Ly8xMjcuMC4wLjE6ODAwMC9hZG1pbi9wcm9kdWN0cyI7czo1OiJyb3V0ZSI7czoyMDoiYWRtaW4ucHJvZHVjdHMuaW5kZXgiO31zOjY6Il9mbGFzaCI7YToyOntzOjM6Im9sZCI7YTowOnt9czozOiJuZXciO2E6MDp7fX1zOjM6InVybCI7YTowOnt9czo1MDoibG9naW5fd2ViXzU5YmEzNmFkZGMyYjJmOTQwMTU4MGYwMTRjN2Y1OGVhNGUzMDk4OWQiO2k6MTt9', 1771999849);

-- --------------------------------------------------------

--
-- Struktur dari tabel `users`
--

CREATE TABLE `users` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `email_verified_at` timestamp NULL DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `postal_code` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `role` enum('customer','admin') NOT NULL DEFAULT 'customer',
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `remember_token` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `email_verified_at`, `password`, `phone`, `address`, `city`, `postal_code`, `avatar`, `role`, `is_active`, `remember_token`, `created_at`, `updated_at`) VALUES
(1, 'Admin MuscleCart', 'admin@musclecart.com', NULL, '$2y$12$5WOsTRcUlb6f21DQM3oANOx7wzWnyyUFPQ/O0wHvTE5.yi1IhoSGS', '081234567890', 'Jakarta, Indonesia', NULL, NULL, NULL, 'admin', 1, 'ZHeJC4eDMrKzUhtiFSsxVLAoDmPHFMluCyjrEDrcPallXgcXlbQGNE6tC6xh', '2026-02-19 05:47:51', '2026-02-19 05:47:51'),
(2, 'Customer 1', 'customer1@example.com', NULL, '$2y$12$i1L1jEhfsPBI7ELg167DdOuO/QEBgjSL4llE7G4O6CGYL8G58cVMi', '081234567891', 'Address for Customer 1', NULL, NULL, NULL, 'customer', 1, NULL, '2026-02-19 05:47:52', '2026-02-19 05:47:52'),
(3, 'Customer 2', 'customer2@example.com', NULL, '$2y$12$zst3Z/CpZLQsUJvF01DWmeHtqTzvADcAnVpxoSN7TjuNATcdJ6E3K', '081234567892', 'Address for Customer 2', NULL, NULL, NULL, 'customer', 1, NULL, '2026-02-19 05:47:52', '2026-02-19 05:47:52'),
(4, 'Customer 3', 'customer3@example.com', NULL, '$2y$12$0B9LhGXUBkbk5JPN99Ii3ehAoivnjOhp9w1uqGhuLbWKVlu5Q6CLm', '081234567893', 'Address for Customer 3', NULL, NULL, NULL, 'customer', 1, NULL, '2026-02-19 05:47:52', '2026-02-19 05:47:52'),
(5, 'Customer 4', 'customer4@example.com', NULL, '$2y$12$uc.LhMQMS5SHQSR2VKstAe0y96ZdVcezuZHLzeUaECt/IsGjE9i.m', '081234567894', 'Address for Customer 4', NULL, NULL, NULL, 'customer', 1, NULL, '2026-02-19 05:47:52', '2026-02-19 05:47:52'),
(6, 'Customer 5', 'customer5@example.com', NULL, '$2y$12$uypxV6aXx7Qfz7wfDD7Uz.6wXz9Nu2BBI6n6KdOH5yDvkDAvQrIHC', '081234567895', 'Address for Customer 5', NULL, NULL, NULL, 'customer', 1, NULL, '2026-02-19 05:47:53', '2026-02-19 05:47:53'),
(8, 'Test User', 'test@test.com', NULL, '$2y$12$lU1Httj.64Y3g/9z5sHf5.jic6jlGYlNJOxOjULEysKYGM1N3Ifw6', NULL, NULL, NULL, NULL, NULL, 'customer', 1, NULL, '2026-02-24 22:17:25', '2026-02-24 22:17:25');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `cache`
--
ALTER TABLE `cache`
  ADD PRIMARY KEY (`key`),
  ADD KEY `cache_expiration_index` (`expiration`);

--
-- Indeks untuk tabel `cache_locks`
--
ALTER TABLE `cache_locks`
  ADD PRIMARY KEY (`key`),
  ADD KEY `cache_locks_expiration_index` (`expiration`);

--
-- Indeks untuk tabel `cart_items`
--
ALTER TABLE `cart_items`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `cart_items_user_id_product_id_unique` (`user_id`,`product_id`),
  ADD KEY `cart_items_product_id_foreign` (`product_id`);

--
-- Indeks untuk tabel `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `failed_jobs`
--
ALTER TABLE `failed_jobs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `failed_jobs_uuid_unique` (`uuid`);

--
-- Indeks untuk tabel `favorites`
--
ALTER TABLE `favorites`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `favorites_user_id_product_id_unique` (`user_id`,`product_id`),
  ADD KEY `favorites_product_id_foreign` (`product_id`),
  ADD KEY `favorites_user_id_index` (`user_id`);

--
-- Indeks untuk tabel `jobs`
--
ALTER TABLE `jobs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `jobs_queue_index` (`queue`);

--
-- Indeks untuk tabel `job_batches`
--
ALTER TABLE `job_batches`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `migrations`
--
ALTER TABLE `migrations`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `orders_order_number_unique` (`order_number`),
  ADD KEY `orders_user_id_foreign` (`user_id`);

--
-- Indeks untuk tabel `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_items_order_id_foreign` (`order_id`),
  ADD KEY `order_items_product_id_foreign` (`product_id`);

--
-- Indeks untuk tabel `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  ADD PRIMARY KEY (`email`);

--
-- Indeks untuk tabel `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `personal_access_tokens_token_unique` (`token`),
  ADD KEY `personal_access_tokens_tokenable_type_tokenable_id_index` (`tokenable_type`,`tokenable_id`),
  ADD KEY `personal_access_tokens_expires_at_index` (`expires_at`);

--
-- Indeks untuk tabel `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD KEY `products_category_id_foreign` (`category_id`);

--
-- Indeks untuk tabel `sessions`
--
ALTER TABLE `sessions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `sessions_user_id_index` (`user_id`),
  ADD KEY `sessions_last_activity_index` (`last_activity`);

--
-- Indeks untuk tabel `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `users_email_unique` (`email`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `cart_items`
--
ALTER TABLE `cart_items`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT untuk tabel `categories`
--
ALTER TABLE `categories`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT untuk tabel `failed_jobs`
--
ALTER TABLE `failed_jobs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `favorites`
--
ALTER TABLE `favorites`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT untuk tabel `jobs`
--
ALTER TABLE `jobs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `migrations`
--
ALTER TABLE `migrations`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT untuk tabel `orders`
--
ALTER TABLE `orders`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;

--
-- AUTO_INCREMENT untuk tabel `order_items`
--
ALTER TABLE `order_items`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT untuk tabel `products`
--
ALTER TABLE `products`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;

--
-- AUTO_INCREMENT untuk tabel `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `cart_items`
--
ALTER TABLE `cart_items`
  ADD CONSTRAINT `cart_items_product_id_foreign` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `cart_items_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `favorites`
--
ALTER TABLE `favorites`
  ADD CONSTRAINT `favorites_product_id_foreign` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `favorites_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `order_items_order_id_foreign` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `order_items_product_id_foreign` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

--
-- Ketidakleluasaan untuk tabel `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `products_category_id_foreign` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
