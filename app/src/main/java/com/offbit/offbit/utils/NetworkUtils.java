package com.offbit.offbit.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    
    /**
     * Check if device is connected to WiFi
     */
    public static boolean isConnectedToWiFi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && 
               activeNetwork.getType() == ConnectivityManager.TYPE_WIFI && 
               activeNetwork.isConnectedOrConnecting();
    }
    
    /**
     * Get WiFi network name (SSID)
     */
    public static String getWiFiSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }
    
    /**
     * Get local IP addresses
     */
    public static List<String> getLocalIPAddresses() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // Filter out loopback and inactive interfaces
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }
                
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // Filter out IPv6 addresses for simplicity
                    if (!addr.getHostAddress().contains(":")) {
                        ipList.add(addr.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, "Error getting local IP addresses", e);
        }
        return ipList;
    }
    
    /**
     * Check if two IP addresses are on the same network
     */
    public static boolean isSameNetwork(String ip1, String ip2) {
        if (ip1 == null || ip2 == null) {
            return false;
        }
        
        // Simple check: compare network prefixes (first 3 octets)
        String[] parts1 = ip1.split("\\.");
        String[] parts2 = ip2.split("\\.");
        
        if (parts1.length < 3 || parts2.length < 3) {
            return false;
        }
        
        return parts1[0].equals(parts2[0]) && 
               parts1[1].equals(parts2[1]) && 
               parts1[2].equals(parts2[2]);
    }
    
    /**
     * Get device hostname
     */
    public static String getDeviceHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            Log.e(TAG, "Error getting device hostname", e);
            return "unknown";
        }
    }
}
