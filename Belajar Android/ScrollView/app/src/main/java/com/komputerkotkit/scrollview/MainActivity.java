package com.komputerkotkit.scrollview;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menghilangkan ActionBar default untuk tampilan yang lebih bersih
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // ScrollView akan bekerja secara otomatis tanpa kode Java tambahan
    }
}