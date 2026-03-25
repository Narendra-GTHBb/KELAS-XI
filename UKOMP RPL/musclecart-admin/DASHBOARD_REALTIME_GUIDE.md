# Dashboard Real-Time Guide
## MuscleCart Admin Dashboard - Sistem Real-Time Update

### 📊 Overview
Dashboard MuscleCart Admin sekarang dilengkapi dengan **sistem real-time data** yang secara otomatis mengupdate statistik, grafik, dan tabel tanpa perlu refresh halaman.

---

## ✨ Fitur Real-Time yang Tersedia

### 1. **Metrics Cards (Auto-update setiap 30 detik)**
Dashboard menampilkan 5 metrik utama yang di-track secara real-time:

#### 📈 Total Revenue (Pendapatan Total)
- **Data:** Total pendapatan dari semua order yang berhasil (status: processing, shipped, delivered, success)
- **Growth Indicator:** Persentase pertumbuhan dibanding bulan lalu
- **Cocok untuk track:** Performa penjualan bulanan dan tren revenue
- **Update interval:** 30 detik

#### 🛒 Total Orders (Total Pesanan)
- **Data:** Jumlah total semua order
- **Growth Indicator:** Persentase pertumbuhan order dibanding bulan lalu
- **Cocok untuk track:** Volume transaksi dan aktivitas pelanggan
- **Update interval:** 30 detik

#### ⏰ Pending Orders (Pesanan Tertunda)
- **Data:** Jumlah order dengan status 'pending' yang menunggu konfirmasi
- **Alert:** Memerlukan perhatian admin untuk konfirmasi pembayaran
- **Cocok untuk track:** Order yang perlu segera diproses
- **Update interval:** 30 detik
- **⚠️ PRIORITAS TINGGI** - Harus segera ditindaklanjuti

#### 🔴 Low Stock (Stok Rendah)
- **Data:** Jumlah produk dengan stok ≤ 10 unit
- **Alert:** Warning jika ada produk yang hampir habis
- **Cocok untuk track:** Manajemen inventory untuk restock
- **Update interval:** 30 detik
- **⚠️ PENTING** - Butuh restock segera

#### 👥 Total Customers (Total Pelanggan)
- **Data:** Jumlah total user dengan role 'customer'
- **Growth Indicator:** Persentase pertumbuhan customer baru dibanding bulan lalu
- **Cocok untuk track:** Pertumbuhan user base dan akuisisi pelanggan
- **Update interval:** 30 detik

---

### 2. **Revenue Chart (Auto-update setiap 60 detik)**
Grafik garis yang menampilkan tren pendapatan dengan 3 periode pilihan:

#### 📅 Period Options:
- **Last 7 Days:** Pendapatan harian selama 7 hari terakhir
- **Last 30 Days:** Pendapatan harian selama 30 hari terakhir
- **Last 3 Months:** Pendapatan bulanan selama 3 bulan terakhir

**Cocok untuk track:**
- Tren penjualan harian/bulanan
- Pola pembelian pelanggan
- Peak sales period
- Perbandingan performa antar periode

---

### 3. **Order Volume Chart (Auto-update setiap 60 detik)**
Grafik batang yang menampilkan volume order per jam dalam sehari ini:

**Time Intervals:**
- 00:00 - 03:59
- 04:00 - 07:59
- 08:00 - 11:59
- 12:00 - 15:59
- 16:00 - 19:59
- 20:00 - 23:59

**Cocok untuk track:**
- Jam-jam sibuk transaksi
- Optimasi waktu promo/marketing
- Peak hour pelanggan
- Resource planning untuk customer service

---

### 4. **Recent Orders Table (Auto-update setiap 45 detik)**
Tabel 5 order terbaru dengan informasi:
- Order ID
- Customer name
- Total amount
- Status (Pending, Processing, Shipped, Success, Cancelled)
- Date

**Cocok untuk track:**
- Aktivitas transaksi terkini
- Order yang perlu diproses
- Quick check status pembayaran

---

## 🎯 Rekomendasi Metrics untuk Di-Track Real-Time

### **PRIORITAS TINGGI** (Wajib dimonitor):

1. **Pending Orders** ⏰
   - **Mengapa:** Order pending memerlukan aksi segera (konfirmasi pembayaran)
   - **Aksi:** Check pembayaran dan update status
   - **Dampak:** Kepuasan pelanggan dan kecepatan layanan

2. **Low Stock Products** 🔴
   - **Mengapa:** Produk habis = kehilangan peluang penjualan
   - **Aksi:** Restock produk yang hampir habis
   - **Dampak:** Ketersediaan produk dan kontinuitas penjualan

3. **Recent Orders** 📝
   - **Mengapa:** Monitoring real-time aktivitas transaksi
   - **Aksi:** Proses order baru dan handle issues
   - **Dampak:** Response time dan customer satisfaction

### **PRIORITAS SEDANG** (Monitoring berkala):

4. **Total Revenue & Growth** 💰
   - **Mengapa:** Indikator kesehatan bisnis
   - **Aksi:** Analisa tren dan strategi pricing
   - **Dampak:** Target penjualan dan profit margin

5. **Order Volume Chart** 📊
   - **Mengapa:** Memahami pola transaksi pelanggan
   - **Aksi:** Optimasi waktu promo dan staffing
   - **Dampak:** Marketing effectiveness dan operational efficiency

### **PRIORITAS RENDAH** (Review harian/mingguan):

6. **Total Orders Growth** 📈
   - **Mengapa:** Tren jangka panjang volume transaksi
   - **Aksi:** Strategic planning dan forecasting
   - **Dampak:** Business growth planning

7. **Total Customers Growth** 👥
   - **Mengapa:** Customer acquisition rate
   - **Aksi:** Marketing campaign effectiveness
   - **Dampak:** User base expansion

---

## ⚙️ Konfigurasi Auto-Refresh

### Update Intervals (dapat disesuaikan):

```javascript
// Di file: resources/views/admin/dashboard.blade.php

// Dashboard Stats Update
setInterval(updateDashboardStats, 30000);  // 30 detik

// Charts Update
setInterval(updateCharts, 60000);  // 60 detik (1 menit)

// Recent Orders Update
setInterval(updateRecentOrders, 45000);  // 45 detik
```

### Cara Mengubah Interval:
1. Buka file `resources/views/admin/dashboard.blade.php`
2. Cari bagian `setInterval` di dalam JavaScript
3. Ubah nilai dalam miliseconds (1000 = 1 detik)
4. Refresh halaman dashboard

**Rekomendasi:**
- **High-priority data** (Pending Orders, Low Stock): 15-30 detik
- **Medium-priority data** (Stats, Charts): 30-60 detik
- **Low-priority data** (Historical data): 60-120 detik

---

## 🔌 API Endpoints

Dashboard menggunakan API endpoints berikut untuk real-time updates:

### 1. Dashboard Stats
```
GET /admin/api/dashboard/stats
```
**Response:**
```json
{
  "success": true,
  "data": {
    "total_revenue": 128430.50,
    "total_orders": 34,
    "pending_orders": 6,
    "low_stock_products": 1,
    "total_customers": 5,
    "revenue_growth": 72.5,
    "orders_growth": 5.2,
    "customers_growth": 8.1
  }
}
```

### 2. Revenue Chart Data
```
GET /admin/api/dashboard/revenue-chart?period=7days
```
**Parameters:**
- `period`: 7days, 30days, 3months

**Response:**
```json
{
  "success": true,
  "data": {
    "labels": ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
    "data": [12000, 15000, 18000, 22000, 19000, 25000, 21000]
  }
}
```

### 3. Order Volume Chart Data
```
GET /admin/api/dashboard/order-volume-chart?period=today
```
**Response:**
```json
{
  "success": true,
  "data": {
    "labels": ["00:00", "04:00", "08:00", "12:00", "16:00", "20:00"],
    "data": [5, 8, 15, 25, 40, 30]
  }
}
```

### 4. Recent Orders
```
GET /admin/api/dashboard/recent-orders
```
**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "customer_name": "John Doe",
      "total_amount": 2195000.00,
      "status": "pending",
      "created_at": "Feb 19, 2026"
    }
  ]
}
```

### 5. Top Selling Products
```
GET /admin/api/dashboard/top-products
```

### 6. Low Stock Products
```
GET /admin/api/dashboard/low-stock-products
```

---

## 📱 Real-Time Benefits

### Untuk Admin:
✅ Tidak perlu refresh halaman manual
✅ Data selalu up-to-date
✅ Quick response untuk order baru
✅ Monitoring inventory lebih efektif
✅ Better decision making dengan data real-time

### Untuk Business:
✅ Faster order processing
✅ Reduced stockout situations
✅ Better customer service
✅ Data-driven insights
✅ Improved operational efficiency

---

## 🚀 Future Enhancements (Rekomendasi)

### 1. **Real-time Notifications**
- Browser notifications untuk order baru
- Alert untuk stock critical level
- Revenue milestone achievements

### 2. **Advanced Analytics**
- Customer behavior analysis
- Product performance comparison
- Sales forecasting
- Conversion rate tracking

### 3. **WebSocket Integration**
- Instant updates tanpa polling
- Lebih efisien bandwidth
- Real-time collaboration antar admin

### 4. **Custom Dashboard**
- Drag & drop widget arrangement
- Personalized metrics
- Custom time ranges
- Export reports

---

## 🛠️ Troubleshooting

### Dashboard tidak auto-update?
1. Check console untuk error JavaScript
2. Pastikan API endpoints accessible
3. Verify database connection
4. Clear browser cache

### Data tidak akurat?
1. Check database seeder
2. Verify order status values
3. Check timezone settings
8. Review calculation logic

### Performance issues?
1. Adjust update intervals (increase)
2. Optimize database queries
3. Add caching layer
4. Consider WebSocket

---

## 📞 Support

Untuk pertanyaan atau issues, check:
- Controller: `app/Http/Controllers/Api/DashboardApiController.php`
- View: `resources/views/admin/dashboard.blade.php`
- Routes: `routes/web.php` (section: Dashboard API)

---

**Last Updated:** February 22, 2026
**Version:** 1.0.0
**Author:** MuscleCart Development Team
