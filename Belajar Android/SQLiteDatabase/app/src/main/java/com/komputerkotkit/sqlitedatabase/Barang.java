package com.komputerkotkit.sqlitedatabase;

public class Barang {
    private String idbarang, barang, stok, harga;

    public Barang(String idbarang, String barang, String stok, String harga) {
        this.idbarang = idbarang;
        this.barang = barang;
        this.stok = stok;
        this.harga = harga;

        // Debug: Print untuk memastikan data masuk constructor
        System.out.println("Barang created: ID=" + idbarang + ", Nama=" + barang + ", Stok=" + stok + ", Harga=" + harga);
    }

    // Constructor tanpa ID (untuk insert baru)
    public Barang(String barang, String stok, String harga) {
        this.barang = barang;
        this.stok = stok;
        this.harga = harga;
        System.out.println("Barang created (no ID): Nama=" + barang + ", Stok=" + stok + ", Harga=" + harga);
    }

    // Default constructor
    public Barang() {
        // Empty constructor
    }

    public String getIdbarang() {
        return idbarang;
    }

    public void setIdbarang(String idbarang) {
        this.idbarang = idbarang;
    }

    public String getBarang() {
        return barang;
    }

    public void setBarang(String barang) {
        this.barang = barang;
    }

    public String getStok() {
        return stok;
    }

    public void setStok(String stok) {
        this.stok = stok;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    // Method untuk mendapatkan stok sebagai integer
    public int getStokAsInt() {
        try {
            return (int) Double.parseDouble(stok);
        } catch (NumberFormatException e) {
            System.out.println("Error parsing stok: " + stok);
            return 0;
        }
    }

    // Method untuk mendapatkan harga sebagai double
    public double getHargaAsDouble() {
        try {
            return Double.parseDouble(harga);
        } catch (NumberFormatException e) {
            System.out.println("Error parsing harga: " + harga);
            return 0.0;
        }
    }

    // Method untuk format harga dengan titik sebagai pemisah ribuan
    public String getFormattedHarga() {
        try {
            double hargaValue = Double.parseDouble(harga);
            return String.format("Rp. %,.0f", hargaValue);
        } catch (NumberFormatException e) {
            return "Rp. " + harga;
        }
    }

    // Override toString untuk debugging
    @Override
    public String toString() {
        return "Barang{" +
                "idbarang='" + idbarang + '\'' +
                ", barang='" + barang + '\'' +
                ", stok='" + stok + '\'' +
                ", harga='" + harga + '\'' +
                '}';
    }

    // Method untuk validasi data
    public boolean isValid() {
        return barang != null && !barang.trim().isEmpty() &&
                stok != null && !stok.trim().isEmpty() &&
                harga != null && !harga.trim().isEmpty();
    }
}