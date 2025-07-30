package com.komputerkotkit.intentactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BarangActivity extends AppCompatActivity {

    private TextView tvNamaBarangDiterima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang);

        // Enable tombol back di ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Barang");
        }

        // Inisialisasi TextView
        tvNamaBarangDiterima = findViewById(R.id.tvNamaBarangDiterima);

        // Dapatkan Intent yang memulai Activity ini
        Intent intent = getIntent();

        // Ambil data yang dikirim dari MainActivity
        String namaBarang = intent.getStringExtra("nama_barang");

        // Cek apakah data diterima
        if (namaBarang != null && !namaBarang.isEmpty()) {
            // Tampilkan data di TextView
            tvNamaBarangDiterima.setText(namaBarang);
        } else {
            // Jika tidak ada data, tampilkan pesan default
            tvNamaBarangDiterima.setText("Tidak ada data barang");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle tombol back di ActionBar
        finish();
        return true;
    }
}