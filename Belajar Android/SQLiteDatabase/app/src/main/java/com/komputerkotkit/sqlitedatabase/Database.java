package com.komputerkotkit.sqlitedatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dbtoko.db";
    private static final int VERSION = 2; // Increased version to trigger onUpgrade
    private Context context;

    SQLiteDatabase db;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
        db = this.getWritableDatabase();
        System.out.println("Database initialized: " + DATABASE_NAME);
    }

    boolean runSQL(String sql){
        try {
            System.out.println("Executing SQL: " + sql);
            db.execSQL(sql);
            System.out.println("SQL executed successfully");
            return true;
        } catch (Exception e){
            System.out.println("SQL execution failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    Cursor select(String sql){
        try{
            System.out.println("Executing SELECT: " + sql);
            Cursor cursor = db.rawQuery(sql, null);
            System.out.println("SELECT returned " + cursor.getCount() + " rows");
            return cursor;
        }catch (Exception e){
            System.out.println("SELECT failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void buatTabel(){
        String tblbarang = "CREATE TABLE IF NOT EXISTS tblbarang (" +
                "idbarang INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "barang TEXT NOT NULL, " +
                "stok REAL NOT NULL DEFAULT 0, " +
                "harga REAL NOT NULL DEFAULT 0" +
                ")";

        System.out.println("Creating table with SQL: " + tblbarang);
        runSQL(tblbarang);

        // Check if table is empty and insert initial data
        insertInitialDataIfEmpty();
    }

    private void insertInitialDataIfEmpty() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM tblbarang", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0) {
            System.out.println("Table is empty, inserting initial data...");

            // Insert data yang sama seperti di SQLite Browser
            String[] initialData = {
                    "INSERT INTO tblbarang (barang, stok, harga) VALUES ('BUKU', 10.0, 2000.0)",
                    "INSERT INTO tblbarang (barang, stok, harga) VALUES ('BUKU', 10.0, 2000.0)", // Duplicate seperti di browser
                    "INSERT INTO tblbarang (barang, stok, harga) VALUES ('KERTAS', 20.0, 5000.0)",
                    "INSERT INTO tblbarang (barang, stok, harga) VALUES ('FLASHDISK', 20.0, 5000.0)"
            };

            for (String sql : initialData) {
                runSQL(sql);
            }

            System.out.println("Initial data inserted successfully");
        } else {
            System.out.println("Table already contains " + count + " records");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("Database onCreate called");
        String tblbarang = "CREATE TABLE IF NOT EXISTS tblbarang (" +
                "idbarang INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "barang TEXT NOT NULL, " +
                "stok REAL NOT NULL DEFAULT 0, " +
                "harga REAL NOT NULL DEFAULT 0" +
                ")";

        db.execSQL(tblbarang);
        System.out.println("Table created in onCreate");

        // Insert initial data
        String[] initialData = {
                "INSERT INTO tblbarang (barang, stok, harga) VALUES ('BUKU', 10.0, 2000.0)",
                "INSERT INTO tblbarang (barang, stok, harga) VALUES ('BUKU', 10.0, 2000.0)",
                "INSERT INTO tblbarang (barang, stok, harga) VALUES ('KERTAS', 20.0, 5000.0)",
                "INSERT INTO tblbarang (barang, stok, harga) VALUES ('FLASHDISK', 20.0, 5000.0)"
        };

        for (String sql : initialData) {
            try {
                db.execSQL(sql);
                System.out.println("Executed: " + sql);
            } catch (Exception e) {
                System.out.println("Failed to execute: " + sql + " - " + e.getMessage());
            }
        }

        System.out.println("Initial data inserted in onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("Database onUpgrade called from version " + oldVersion + " to " + newVersion);

        // Backup existing data if needed
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM tblbarang", null);
            System.out.println("Found " + cursor.getCount() + " existing records before upgrade");
        } catch (Exception e) {
            System.out.println("No existing table found or error reading: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }

        // Drop and recreate table
        db.execSQL("DROP TABLE IF EXISTS tblbarang");
        onCreate(db);
    }

    // Method untuk reset database (hanya untuk testing)
    public void resetDatabase() {
        System.out.println("Resetting database...");
        db.execSQL("DROP TABLE IF EXISTS tblbarang");
        buatTabel();
        System.out.println("Database reset completed");
    }

    // Method untuk check apakah database sudah memiliki data
    public boolean hasData() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM tblbarang", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    // Method untuk mendapatkan jumlah total data
    public int getTotalRecords() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM tblbarang", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}