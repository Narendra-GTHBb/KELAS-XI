package com.komputerkotkit.sqlitedatabase;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Database db;
    EditText etBarang, etStok, etHarga;
    TextView tvPilihan;

    List<Barang> databarang = new ArrayList<Barang>();
    BarangAdapter adapter;
    RecyclerView rcvBarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnSimpan), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        load();
        selectData();
    }

    public void load(){
        db = new Database(this);
        db.buatTabel();

        etBarang = findViewById(R.id.etBarang);
        etStok = findViewById(R.id.etStok);
        etHarga = findViewById(R.id.etHarga);
        tvPilihan = findViewById(R.id.tvPilihan);
        rcvBarang = findViewById(R.id.rcvBarang);

        rcvBarang.setLayoutManager(new LinearLayoutManager(this));
        rcvBarang.setHasFixedSize(true);

        // Set nilai awal untuk insert
        tvPilihan.setText("insert");
    }

    public void simpan(View v){
        String barang = etBarang.getText().toString();
        String stok = etStok.getText().toString();
        String harga = etHarga.getText().toString();
        String pilihan = tvPilihan.getText().toString();

        // Debug: cek nilai pilihan
        System.out.println("Nilai pilihan: '" + pilihan + "'");

        if (barang.isEmpty() || stok.isEmpty() || harga.isEmpty() ){
            pesan("Data Kosong");
        }else {
            if (pilihan.equals("insert")){
                String sql = "INSERT INTO tblbarang (barang,stok,harga) VALUES ('"+barang+"', "+stok+", "+harga+")";
                if (db.runSQL(sql)){
                    pesan("Insert berhasil");
                    selectData(); // Refresh data setelah insert

                    // Clear form
                    etBarang.setText("");
                    etStok.setText("");
                    etHarga.setText("");
                }else{
                    pesan("Insert gagal");
                }
            }else if (pilihan.startsWith("update:")){
                // Extract ID from pilihan text
                String idbarang = pilihan.replace("update:", "");
                String sql = "UPDATE tblbarang SET barang='"+barang+"', stok="+stok+", harga="+harga+" WHERE idbarang="+idbarang;
                if (db.runSQL(sql)){
                    pesan("Update berhasil");
                    selectData(); // Refresh data setelah update

                    // Clear form and reset to insert mode
                    etBarang.setText("");
                    etStok.setText("");
                    etHarga.setText("");
                    tvPilihan.setText("insert");
                }else{
                    pesan("Update gagal");
                }
            }
        }
    }

    public void pesan(String isi) {
        Toast.makeText(this, isi, Toast.LENGTH_SHORT).show();
    }

    public void selectData(){
        String sql = "SELECT * FROM tblbarang ORDER BY barang ASC";
        System.out.println("=== DEBUG SELECT DATA START ===");
        System.out.println("SQL Query: " + sql);

        Cursor cursor = db.select(sql);
        databarang.clear();

        // Check if cursor is null
        if (cursor == null) {
            System.out.println("ERROR: Cursor is null!");
            pesan("Error: Database query failed");
            return;
        }

        System.out.println("Total rows in cursor: " + cursor.getCount());

        // Print column names for debugging
        String[] columnNames = cursor.getColumnNames();
        System.out.println("Column names: " + java.util.Arrays.toString(columnNames));

        if (cursor.getCount() > 0) {
            int counter = 0;
            while (cursor.moveToNext()){
                try {
                    String idbarang = cursor.getString(cursor.getColumnIndexOrThrow("idbarang"));
                    String barang = cursor.getString(cursor.getColumnIndexOrThrow("barang"));
                    String stok = cursor.getString(cursor.getColumnIndexOrThrow("stok"));
                    String harga = cursor.getString(cursor.getColumnIndexOrThrow("harga"));

                    System.out.println("Row " + (++counter) + ": ID=" + idbarang + ", Barang=" + barang + ", Stok=" + stok + ", Harga=" + harga);

                    // Check if any value is null
                    if (idbarang == null || barang == null || stok == null || harga == null) {
                        System.out.println("WARNING: Null value detected in row " + counter);
                        continue; // Skip this row
                    }

                    // Add to list
                    Barang item = new Barang(idbarang, barang, stok, harga);
                    databarang.add(item);
                    System.out.println("Successfully added item " + counter + " to list");

                } catch (Exception e) {
                    System.out.println("ERROR at row " + (cursor.getPosition() + 1) + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("=== FINAL RESULTS ===");
            System.out.println("Total items in database: " + cursor.getCount());
            System.out.println("Total items added to list: " + databarang.size());
            System.out.println("Items in list:");
            for (int i = 0; i < databarang.size(); i++) {
                Barang item = databarang.get(i);
                System.out.println("  " + (i+1) + ". " + item.getBarang() + " (ID: " + item.getIdbarang() + ")");
            }

            // Setup adapter
            if (adapter == null) {
                adapter = new BarangAdapter(this, databarang, this);
                rcvBarang.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            pesan("Total data: " + databarang.size());
            cursor.close();

        } else {
            System.out.println("No data found in database");

            // Setup empty adapter
            if (adapter == null) {
                adapter = new BarangAdapter(this, databarang, this);
                rcvBarang.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            pesan("Data Kosong");
        }

        System.out.println("=== DEBUG SELECT DATA END ===");
    }

    // Method untuk edit data dari adapter
    public void editData(Barang barang) {
        etBarang.setText(barang.getBarang());
        etStok.setText(barang.getStok());
        etHarga.setText(barang.getHarga());
        tvPilihan.setText("update:" + barang.getIdbarang());
        pesan("Mode Edit: " + barang.getBarang());
    }

    // Method untuk delete data dari adapter
    public void deleteData(Barang barang) {
        String sql = "DELETE FROM tblbarang WHERE idbarang=" + barang.getIdbarang();
        if (db.runSQL(sql)) {
            pesan("Data berhasil dihapus");
            selectData(); // Refresh data
        } else {
            pesan("Gagal menghapus data");
        }
    }
}