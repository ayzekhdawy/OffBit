package com.offbit.offbit;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.offbit.offbit.manager.BrowserManager;

public class BrowserActivity extends AppCompatActivity {
    private static final String TAG = "BrowserActivity";
    
    private WebView webView;
    private ProgressBar progressBar;
    private TextView pageTitleText;
    private Toolbar toolbar;
    private BrowserManager browserManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        
        initViews();
        setupToolbar();
        setupWebView();
        setupBrowserManager();
        loadInitialPage();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        pageTitleText = findViewById(R.id.pageTitleText);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
    
    private void setupWebView() {
        // Enable JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        
        // Set WebView clients
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "Page started: " + url);
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                pageTitleText.setText(view.getTitle());
                Log.d(TAG, "Page finished: " + url);
            }
            
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BrowserActivity.this, "Error loading page: " + description, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading page: " + description + " URL: " + failingUrl);
            }
        });
        
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                pageTitleText.setText(title);
            }
            
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
    }
    
    private void setupBrowserManager() {
        browserManager = BrowserManager.getInstance(this);
        
        // Start proxy if not already started
        if (!browserManager.isProxyEnabled()) {
            try {
                browserManager.startProxy();
                Toast.makeText(this, "Proxy server started", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Proxy server started");
            } catch (Exception e) {
                Log.e(TAG, "Failed to start proxy server", e);
                Toast.makeText(this, "Failed to start proxy server", Toast.LENGTH_SHORT).show();
            }
        }
        
        // Configure WebView to use proxy if needed
        browserManager.configureWebViewProxy(webView);
    }
    
    private void loadInitialPage() {
        // Load a default page or get URL from intent
        String url = getIntent().getStringExtra("url");
        if (url == null || url.isEmpty()) {
            url = "https://www.google.com";
        }
        webView.loadUrl(url);
        Log.d(TAG, "Loading initial page: " + url);
    }
    
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
