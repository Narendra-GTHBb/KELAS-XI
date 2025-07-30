package com.komputerkotkit.datepicker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText etTanggal;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi EditText
        etTanggal = findViewById(R.id.etTanggal);

        // Inisialisasi Calendar untuk mendapatkan tanggal saat ini
        calendar = Calendar.getInstance();
    }

    /**
     * Method yang dipanggil ketika EditText diklik
     * Sesuai dengan android:onClick="showDatePicker" di XML
     */
    public void showDatePicker(View view) {
        // Mendapatkan tanggal, bulan, dan tahun saat ini
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Membuat DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear,
                                          int selectedMonth, int selectedDay) {

                        // Format tanggal: DD-MM-YYYY
                        // Perhatikan: selectedMonth adalah 0-based (0 = Januari)
                        // Jadi kita perlu menambahkan 1
                        String formattedDate = String.format("%02d-%02d-%d",
                                selectedDay,
                                selectedMonth + 1,
                                selectedYear);

                        // Update EditText dengan tanggal yang dipilih
                        etTanggal.setText(formattedDate);
                    }
                },
                year, month, dayOfMonth);

        // Menampilkan DatePickerDialog
        datePickerDialog.show();
    }
}