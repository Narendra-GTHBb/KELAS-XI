package com.komputerkotkit.sharedpreferences;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Deklarasi variabel
    private EditText etBarang, etStok;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Panggil metode load()
        load();
    }

    // Metode untuk inisialisasi komponen dan SharedPreferences
    public void load() {
        // Inisialisasi EditText
        etBarang = findViewById(R.id.etBarang);
        etStok = findViewById(R.id.etStok);

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("barang", MODE_PRIVATE);

        // Ambil data tersimpan dan tampilkan di EditText
        String namaBarang = sharedPreferences.getString("barang", "");
        float stokBarang = sharedPreferences.getFloat("stok", 0.0f);

        // Set data ke EditText jika ada data tersimpan
        etBarang.setText(namaBarang);

        // Konversi float ke string untuk ditampilkan di EditText
        if (stokBarang != 0.0f) {
            etStok.setText(String.valueOf(stokBarang));
        } else {
            etStok.setText("");
        }
    }

    // Metode untuk menyimpan data
    public void simpan(View view) {
        // Dapatkan Editor dari SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Ambil nilai dari EditText
        String namaBarang = etBarang.getText().toString();
        String stokString = etStok.getText().toString();

        // Validasi input
        if (namaBarang.isEmpty() || stokString.isEmpty()) {
            Toast.makeText(this, "Harap isi semua field!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Parse stok ke float
            float stokBarang = Float.parseFloat(stokString);

            // Simpan data ke SharedPreferences
            editor.putString("barang", namaBarang);
            editor.putFloat("stok", stokBarang);

            // Terapkan perubahan
            editor.apply();

            // Tampilkan konfirmasi
            Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show();

            // Bersihkan EditText
            etBarang.setText("");
            etStok.setText("");

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format stok tidak valid!", Toast.LENGTH_SHORT).show();
        }
    }

    // Metode untuk menampilkan data
    public void tampil(View view) {
        // Ambil data dari SharedPreferences dengan nilai default
        String namaBarang = sharedPreferences.getString("barang", "");
        float stokBarang = sharedPreferences.getFloat("stok", 0.0f);

        // Pengecekan apakah data kosong
        if (namaBarang.isEmpty() || stokBarang == 0.0f) {
            Toast.makeText(this, "Data Kosong", Toast.LENGTH_SHORT).show();
        } else {
            // Tampilkan data dalam Toast
            String pesan = "Nama Barang: " + namaBarang + "\nStok: " + stokBarang;
            Toast.makeText(this, pesan, Toast.LENGTH_LONG).show();
        }

        // Bersihkan EditText setelah menampilkan data
        etBarang.setText("");
        etStok.setText("");
    }
}