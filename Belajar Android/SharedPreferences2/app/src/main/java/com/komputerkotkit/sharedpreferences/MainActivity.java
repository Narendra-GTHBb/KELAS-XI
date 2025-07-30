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

        // Panggil metode load untuk inisialisasi
        load();
    }

    /**
     * Metode untuk inisialisasi komponen dan SharedPreferences
     */
    public void load() {
        // Inisialisasi EditText
        etBarang = findViewById(R.id.etBarang);
        etStok = findViewById(R.id.etStok);

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("barang", MODE_PRIVATE);
    }

    /**
     * Metode untuk menyimpan data ke SharedPreferences
     * @param view View yang memanggil metode ini (tombol Simpan)
     */
    public void simpan(View view) {
        // Dapatkan SharedPreferences Editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Ambil data dari EditText
        String namaBarang = etBarang.getText().toString().trim();
        String stokText = etStok.getText().toString().trim();

        // Validasi input
        if (namaBarang.isEmpty() || stokText.isEmpty()) {
            Toast.makeText(this, "Harap isi semua field!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Parse stok ke float
            float stokBarang = Float.parseFloat(stokText);

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

    /**
     * Metode untuk menampilkan data dari SharedPreferences
     * @param view View yang memanggil metode ini (tombol Tampil)
     */
    public void tampil(View view) {
        // Ambil data dari SharedPreferences
        String namaBarang = sharedPreferences.getString("barang", "");
        float stokBarang = sharedPreferences.getFloat("stok", 0.0f);

        // Pengecekan data
        if (namaBarang.isEmpty() || stokBarang == 0.0f) {
            Toast.makeText(this, "Data Kosong", Toast.LENGTH_SHORT).show();
            // Bersihkan EditText jika data kosong
            etBarang.setText("");
            etStok.setText("");
        } else {
            // Tampilkan data di EditText
            etBarang.setText(namaBarang);
            etStok.setText(String.valueOf(stokBarang));
        }
    }
}