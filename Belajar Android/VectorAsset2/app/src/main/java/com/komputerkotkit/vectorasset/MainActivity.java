package com.komputerkotkit.vectorasset;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageView androidRobotIcon;
    private ImageView bugReportIcon;
    private ImageView accountIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ImageViews
        androidRobotIcon = findViewById(R.id.androidRobotIcon);
        bugReportIcon = findViewById(R.id.bugReportIcon);
        accountIcon = findViewById(R.id.accountIcon);

        // Set click listeners for each icon
        setupIconClickListeners();
    }

    private void setupIconClickListeners() {
        androidRobotIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Android Robot Vector Asset clicked!");
            }
        });

        bugReportIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Bug Report Vector Asset clicked!");
            }
        });

        accountIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Account Circle Vector Asset clicked!");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}