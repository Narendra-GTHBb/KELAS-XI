package com.komputerkotkit.androidrecyclerview;

public class Siswa {
    // Deklarasi variabel private
    private String nama;
    private String alamat;

    // Konstruktor yang menerima nama dan alamat sebagai parameter
    public Siswa(String nama, String alamat) {
        this.nama = nama;
        this.alamat = alamat;
    }

    // Getter method untuk nama
    public String getNama() {
        return nama;
    }

    // Setter method untuk nama
    public void setNama(String nama) {
        this.nama = nama;
    }

    // Getter method untuk alamat
    public String getAlamat() {
        return alamat;
    }

    // Setter method untuk alamat
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    // toString method untuk debugging (optional)
    @Override
    public String toString() {
        return "Siswa{" +
                "nama='" + nama + '\'' +
                ", alamat='" + alamat + '\'' +
                '}';
    }
}