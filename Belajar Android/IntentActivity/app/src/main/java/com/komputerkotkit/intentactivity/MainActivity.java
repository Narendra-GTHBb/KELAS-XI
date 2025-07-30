package com.komputerkotkit.intentactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etNamaBarang;
    private Button btnBarang;
    private Button btnPenjualan;
    private Button btnPembelian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi views
        initViews();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        etNamaBarang = findViewById(R.id.etNamaBarang);
        btnBarang = findViewById(R.id.btnBarang);
        btnPenjualan = findViewById(R.id.btnPenjualan);
        btnPembelian = findViewById(R.id.btnPembelian);
    }

    private void setClickListeners() {
        // Click listener untuk tombol BARANG
        btnBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ambil teks dari EditText
                String namaBarang = etNamaBarang.getText().toString().trim();

                // Validasi input
                if (namaBarang.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Silakan masukkan nama barang terlebih dahulu!",
                            Toast.LENGTH_SHORT).show();
                    etNamaBarang.requestFocus();
                    return;
                }

                // Buat Intent untuk memulai BarangActivity
                Intent intent = new Intent(MainActivity.this, BarangActivity.class);

                // Kirim data menggunakan putExtra
                intent.putExtra("nama_barang", namaBarang);

                // Mulai BarangActivity
                startActivity(intent);
            }
        });

        // Click listener untuk tombol PENJUALAN
        btnPenjualan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "Fitur Penjualan belum diimplementasikan",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Click listener untuk tombol PEMBELIAN
        btnPembelian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "Fitur Pembelian belum diimplementasikan",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}