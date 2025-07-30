package com.komputerkotkit.vectorasset;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageView ivAndroid;
    private ImageView ivAccount;
    private View yellowCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi views
        initializeViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        ivAndroid = findViewById(R.id.iv_android);
        ivAccount = findViewById(R.id.iv_account);
        yellowCircle = findViewById(R.id.yellow_circle);
    }

    private void setupClickListeners() {
        // Android icon click listener
        ivAndroid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Android Vector Asset clicked!");
                // Optional: Add animation or color change
                animateView(ivAndroid);
            }
        });

        // Account icon click listener
        ivAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Account Vector Asset clicked!");
                // Optional: Add animation or color change
                animateView(ivAccount);
            }
        });

        // Yellow circle click listener
        yellowCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Yellow circle clicked!");
                animateView(yellowCircle);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void animateView(View view) {
        // Simple scale animation
        view.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start();
                    }
                })
                .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Optional: Add any resume logic here
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Optional: Add any pause logic here
    }
}