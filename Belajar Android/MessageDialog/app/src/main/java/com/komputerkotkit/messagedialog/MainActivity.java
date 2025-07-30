package com.komputerkotkit.messagedialog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btnToast, btnAlert, btnAlertDialogButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi tombol-tombol
        btnToast = findViewById(R.id.btnToast);
        btnAlert = findViewById(R.id.btnAlert);
        btnAlertDialogButton = findViewById(R.id.btnAlertDialogButton);

        // Set OnClickListener untuk tombol Toast
        btnToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Selamat Belajar!");
            }
        });

        // Set OnClickListener untuk tombol Alert Dialog
        btnAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert("Silahkan Dicoba!");
            }
        });

        // Set OnClickListener untuk tombol Alert Dialog dengan Tombol
        btnAlertDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertButton("Yakin Akan Menghapus?");
            }
        });
    }

    /**
     * Fungsi untuk menampilkan Toast message
     * @param pesan - Pesan yang akan ditampilkan dalam Toast
     */
    public void showToast(String pesan) {
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
    }

    /**
     * Fungsi untuk menampilkan Alert Dialog sederhana
     * @param pesan - Pesan yang akan ditampilkan dalam dialog
     */
    public void showAlert(String pesan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PERHATIAN!");
        builder.setMessage(pesan);
        builder.setCancelable(true);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Fungsi untuk menampilkan Alert Dialog dengan tombol positif dan negatif
     * @param pesan - Pesan yang akan ditampilkan dalam dialog
     */
    public void showAlertButton(String pesan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PERHATIAN!");
        builder.setMessage(pesan);
        builder.setCancelable(false);

        // Tombol Positif (YA)
        builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showToast("Data Sudah Dihapus!");
                dialog.dismiss();
            }
        });

        // Tombol Negatif (TIDAK)
        builder.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showToast("Data Tidak Dihapus!");
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}