package com.komputerkotkit.kalkulator;

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

public class MainActivity extends AppCompatActivity {

    TextView tvhasil;
    EditText etBil_1, etBil_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        load();
    }

    public void load(){
        tvhasil = findViewById(R.id.tvHasil);
        etBil_1 = findViewById(R.id.etBil_1);
        etBil_2 = findViewById(R.id.etBil_2);
    }

    // Method untuk validasi input kosong
    private boolean isInputValid() {
        if (etBil_1.getText().toString().equals("") || etBil_2.getText().toString().equals("")) {
            Toast.makeText(this, "Ada Bilangan Yang Kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void btnJumlah(View view) {
        if (isInputValid()) {
            double bil_1 = Double.parseDouble(etBil_1.getText().toString());
            double bil_2 = Double.parseDouble(etBil_2.getText().toString());
            double hasil = bil_1 + bil_2;
            tvhasil.setText(hasil + "");
        }
    }

    public void btnKurang(View view) {
        if (isInputValid()) {
            double bil_1 = Double.parseDouble(etBil_1.getText().toString());
            double bil_2 = Double.parseDouble(etBil_2.getText().toString());
            double hasil = bil_1 - bil_2;
            tvhasil.setText(hasil + "");
        }
    }

    public void btnKali(View view) {
        if (isInputValid()) {
            double bil_1 = Double.parseDouble(etBil_1.getText().toString());
            double bil_2 = Double.parseDouble(etBil_2.getText().toString());
            double hasil = bil_1 * bil_2;
            tvhasil.setText(hasil + "");
        }
    }

    public void btnBagi(View view) {
        if (isInputValid()) {
            double bil_1 = Double.parseDouble(etBil_1.getText().toString());
            double bil_2 = Double.parseDouble(etBil_2.getText().toString());

            // Validasi pembagian dengan nol
            if (bil_2 == 0) {
                Toast.makeText(this, "Tidak dapat membagi dengan nol", Toast.LENGTH_SHORT).show();
                return;
            }

            double hasil = bil_1 / bil_2;
            tvhasil.setText(hasil + "");
        }
    }
}