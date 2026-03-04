package com.offbit.offbit.manager;

import android.content.Context;
import android.util.Log;

import com.offbit.offbit.network.HttpProxyServer;

public class BrowserManager {
    private static final String TAG = "BrowserManager";
    private static BrowserManager instance;
    
    private Context context;
    private HttpProxyServer proxyServer;
    private boolean isProxyEnabled = false;
    
    private BrowserManager(Context context) {
        this.context = context;
    }
    
    public static synchronized BrowserManager getInstance(Context context) {
        if (instance == null) {
            instance = new BrowserManager(context);
        }
        return instance;
    }
    
    public void startProxy() {
        if (proxyServer == null) {
            proxyServer = HttpProxyServer.getInstance(8080);
        }
        
        if (!proxyServer.isAlive()) {
            proxyServer.startProxy();
        }
        
        isProxyEnabled = true;
        Log.d(TAG, "Browser proxy started");
    }
    
    public void stopProxy() {
        if (proxyServer != null && proxyServer.isAlive()) {
            proxyServer.stopProxy();
        }
        
        isProxyEnabled = false;
        Log.d(TAG, "Browser proxy stopped");
    }
    
    public boolean isProxyEnabled() {
        return isProxyEnabled;
    }
    
    public String getProxyUrl() {
        if (proxyServer != null && proxyServer.isAlive()) {
            return proxyServer.getProxyUrl();
        }
        return null;
    }
    
    public void configureWebViewProxy(android.webkit.WebView webView) {
        if (isProxyEnabled && proxyServer != null) {
            // Set proxy settings for WebView
            // Note: This is a simplified approach. In a real implementation,
            // you might need to use more complex proxy configuration methods
            Log.d(TAG, "Configuring WebView to use proxy");
        }
    }
}
