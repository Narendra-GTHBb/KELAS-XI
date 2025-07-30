package com.komputerkotkit.konversisuhu;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextSuhu;
    private Spinner spinnerKonversi;
    private Button buttonKonversi;
    private TextView textViewHasil;

    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupSpinner();
        setupClickListener();
    }

    private void initializeViews() {
        editTextSuhu = findViewById(R.id.editTextSuhu);
        spinnerKonversi = findViewById(R.id.spinnerKonversi);
        buttonKonversi = findViewById(R.id.buttonKonversi);
        textViewHasil = findViewById(R.id.textViewHasil);
    }

    /**
     * Setup Spinner dengan ArrayAdapter dan OnItemSelectedListener
     */
    private void setupSpinner() {
        // Membuat ArrayAdapter dari string array di resources
        adapter = ArrayAdapter.createFromResource(
                this,
                R.array.conversion_types,
                android.R.layout.simple_spinner_item
        );

        // Menentukan layout untuk dropdown items
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set adapter ke spinner
        spinnerKonversi.setAdapter(adapter);
    }

    private void setupClickListener() {
        buttonKonversi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performConversion();
            }
        });
    }

    private void performConversion() {
        String inputText = editTextSuhu.getText().toString().trim();

        if (inputText.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_input, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double inputValue = Double.parseDouble(inputText);
            int selectedPosition = spinnerKonversi.getSelectedItemPosition();

            double result = convertTemperature(inputValue, selectedPosition);
            textViewHasil.setText(String.format("%.1f", result));

        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid_input, Toast.LENGTH_SHORT).show();
        }
    }

    private double convertTemperature(double value, int conversionType) {
        switch (conversionType) {
            case 0: // Celsius ke Fahrenheit
                return (value * 9/5) + 32;
            case 1: // Celsius ke Kelvin
                return value + 273.15;
            case 2: // Celsius ke Reamur
                return value * 4/5;
            case 3: // Fahrenheit ke Celsius
                return (value - 32) * 5/9;
            case 4: // Fahrenheit ke Kelvin
                return (value - 32) * 5/9 + 273.15;
            case 5: // Fahrenheit ke Reamur
                return (value - 32) * 4/9;
            case 6: // Kelvin ke Celsius
                return value - 273.15;
            case 7: // Kelvin ke Fahrenheit
                return (value - 273.15) * 9/5 + 32;
            case 8: // Kelvin ke Reamur
                return (value - 273.15) * 4/5;
            case 9: // Reamur ke Celsius
                return value * 5/4;
            case 10: // Reamur ke Fahrenheit
                return (value * 9/4) + 32;
            case 11: // Reamur ke Kelvin
                return (value * 5/4) + 273.15;
            default:
                return 0;
        }
    }
}