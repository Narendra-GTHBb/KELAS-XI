package com.komputerkotkit.androidrecyclerview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Deklarasi variabel
    private RecyclerView rcvSiswa;
    private SiswaAdapter siswaAdapter;
    private List<Siswa> siswaList;

    // Deklarasi button dan FAB (hanya btnTambah dan fabAdd)
    private Button btnTambah;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Panggil metode load() dan isiData()
        load();
        isiData();
        setupButtons();
    }

    // Metode load() untuk inisialisasi RecyclerView
    public void load() {
        // Inisialisasi RecyclerView dengan mencari ID-nya
        rcvSiswa = findViewById(R.id.rcvSiswa);

        // Set LayoutManager untuk RecyclerView menjadi LinearLayoutManager
        rcvSiswa.setLayoutManager(new LinearLayoutManager(this));

        // Inisialisasi button dan FAB (hanya yang ada di layout)
        btnTambah = findViewById(R.id.btnTambah);
        fabAdd = findViewById(R.id.fabAdd);
    }

    // Metode isiData() untuk mengisi data contoh
    public void isiData() {
        // Inisialisasi siswaList sebagai ArrayList baru
        siswaList = new ArrayList<>();

        // Tambahkan objek Siswa ke dalam siswaList sebagai data contoh
        // Data sesuai dengan foto yang diberikan
        siswaList.add(new Siswa("Joni", "Surabaya"));
        siswaList.add(new Siswa("el.o", "Surabaya"));
        siswaList.add(new Siswa("tejo", "Surabaya"));
        siswaList.add(new Siswa("siti", "Surabaya"));
        siswaList.add(new Siswa("roni", "Surabaya"));
        siswaList.add(new Siswa("Joni", "Surabaya"));
        siswaList.add(new Siswa("Joni", "Surabaya"));
        siswaList.add(new Siswa("Joni", "Surabaya"));
        siswaList.add(new Siswa("koji", "Surabaya"));
        siswaList.add(new Siswa("koni", "Surabaya"));
        siswaList.add(new Siswa("boni", "Surabaya"));

        // Inisialisasi SiswaAdapter dengan Context saat ini dan siswaList
        siswaAdapter = new SiswaAdapter(this, siswaList);

        // Set adapter ke RecyclerView
        rcvSiswa.setAdapter(siswaAdapter);
    }

    // Method btnTambah sesuai dengan prompt (dipanggil dari onClick XML)
    public void btnTambah(View view) {
        // Buat objek Siswa baru dengan data contoh (sesuai prompt)
        Siswa siswaBaru = new Siswa("JONI RAMBO", "Jakarta");

        // Tambahkan objek Siswa baru ke siswaList
        siswaList.add(siswaBaru);

        // Panggil notifyDataSetChanged untuk memberitahu RecyclerView bahwa data telah berubah
        siswaAdapter.notifyDataSetChanged();

        // Optional: scroll ke item terakhir dan tampilkan toast
        rcvSiswa.scrollToPosition(siswaList.size() - 1);
        Toast.makeText(this, "Data JONI RAMBO ditambahkan", Toast.LENGTH_SHORT).show();
    }

    // Setup button functionality
    private void setupButtons() {
        // Array nama contoh untuk data random
        final String[] namaContoh = {"Ahmad", "Budi", "Citra", "Dewi", "Eko", "Fitri", "Gilang", "Hana"};
        final String[] kotaContoh = {"Jakarta", "Bandung", "Surabaya", "Medan", "Semarang", "Malang", "Yogyakarta"};
        final Random random = new Random();

        // Button Tambah - sudah ada method btnTambah untuk onClick XML
        // Tapi tetap bisa menggunakan OnClickListener juga (optional)
        /*
        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generate nama dan kota random
                String namaRandom = namaContoh[random.nextInt(namaContoh.length)];
                String kotaRandom = kotaContoh[random.nextInt(kotaContoh.length)];

                // Tambah siswa baru ke list
                siswaList.add(new Siswa(namaRandom, kotaRandom));

                // Notify adapter dan scroll ke item terakhir
                siswaAdapter.notifyItemInserted(siswaList.size() - 1);
                rcvSiswa.scrollToPosition(siswaList.size() - 1);

                Toast.makeText(MainActivity.this, "Data " + namaRandom + " ditambahkan", Toast.LENGTH_SHORT).show();
            }
        });
        */

        // Floating Action Button - alternatif tombol tambah
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generate nama dan kota random
                String namaRandom = namaContoh[random.nextInt(namaContoh.length)];
                String kotaRandom = kotaContoh[random.nextInt(kotaContoh.length)];

                // Tambah siswa baru di posisi pertama
                siswaList.add(0, new Siswa(namaRandom, kotaRandom));

                // Notify adapter dan scroll ke atas
                siswaAdapter.notifyItemInserted(0);
                rcvSiswa.scrollToPosition(0);

                Toast.makeText(MainActivity.this, "Data " + namaRandom + " ditambahkan di atas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method untuk mendapatkan jumlah data (opsional, untuk keperluan lain)
    public int getJumlahData() {
        return siswaList != null ? siswaList.size() : 0;
    }

    // Method untuk menghapus item berdasarkan posisi (dipanggil dari adapter)
    public void hapusItem(int position) {
        if (position >= 0 && position < siswaList.size()) {
            String namaDihapus = siswaList.get(position).getNama();
            siswaList.remove(position);
            siswaAdapter.notifyItemRemoved(position);
            siswaAdapter.notifyItemRangeChanged(position, siswaList.size());

            Toast.makeText(this, "Data " + namaDihapus + " dihapus", Toast.LENGTH_SHORT).show();
        }
    }
}