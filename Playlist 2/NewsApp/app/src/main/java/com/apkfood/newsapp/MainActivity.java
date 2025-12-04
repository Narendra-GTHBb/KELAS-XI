package com.apkfood.newsapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Setup navigation drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize WebView and ProgressBar
        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progress_bar);
        setupWebView();

        // Load default news source (BBC News)
        loadNewsSource("https://www.bbc.com/news");
        
        // Set default selected item
        navigationView.setCheckedItem(R.id.nav_bbc);

        // Handle back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        
        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);
        
        // Enable DOM storage API
        webSettings.setDomStorageEnabled(true);
        
        // Enable database storage API
        webSettings.setDatabaseEnabled(true);
        
        // Set cache mode
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        
        // Enable zoom controls
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        
        // Support multiple windows
        webSettings.setSupportMultipleWindows(false);
        
        // Support zoom
        webSettings.setSupportZoom(true);
        
        // Enable mixed content (HTTP and HTTPS)
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        // Enable hardware acceleration
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        
        // Set user agent to desktop version for better compatibility
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
        webSettings.setUserAgentString(userAgent);
        
        // Enable media playback
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        
        // Enable loading images
        webSettings.setLoadsImagesAutomatically(true);
        
        // Set WebViewClient to handle page navigation within the app
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
            
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                progressBar.setVisibility(View.GONE);
                
                String errorMessage;
                switch (errorCode) {
                    case ERROR_HOST_LOOKUP:
                        errorMessage = "Website tidak dapat diakses. Cek koneksi internet Anda.";
                        break;
                    case ERROR_CONNECT:
                        errorMessage = "Gagal terhubung ke server.";
                        break;
                    case ERROR_TIMEOUT:
                        errorMessage = "Koneksi timeout. Coba lagi nanti.";
                        break;
                    default:
                        errorMessage = "Error memuat halaman: " + description;
                        break;
                }
                
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                
                // Load error page
                String errorHtml = "<html><body style='text-align:center; padding:50px; font-family:Arial;'>" +
                        "<h2>Oops! Halaman tidak dapat dimuat</h2>" +
                        "<p>" + errorMessage + "</p>" +
                        "<p>Coba pilih sumber berita lain dari menu.</p>" +
                        "</body></html>";
                view.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null);
            }
        });
        
        // Set WebChromeClient for video support and JavaScript alerts
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadNewsSource(String url) {
        loadNewsSourceWithFallback(url, null);
    }
    
    private void loadNewsSourceWithFallback(String primaryUrl, String fallbackUrl) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        webView.loadUrl(primaryUrl);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_bbc) {
            loadNewsSource("https://www.bbc.com/news");
            getSupportActionBar().setTitle("BBC News");
        } else if (id == R.id.nav_cnn) {
            loadNewsSource("https://edition.cnn.com");
            getSupportActionBar().setTitle("CNN");
        } else if (id == R.id.nav_reuters) {
            loadNewsSource("https://www.reuters.com");
            getSupportActionBar().setTitle("Reuters");
        } else if (id == R.id.nav_dw) {
            loadNewsSource("https://www.dw.com/en");
            getSupportActionBar().setTitle("DW News");
        } else if (id == R.id.nav_sky) {
            loadNewsSource("https://news.sky.com");
            getSupportActionBar().setTitle("Sky News");
        } else if (id == R.id.nav_guardian) {
            loadNewsSource("https://www.theguardian.com/international");
            getSupportActionBar().setTitle("The Guardian");
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}