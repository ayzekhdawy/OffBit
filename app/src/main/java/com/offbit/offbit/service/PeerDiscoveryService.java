package com.offbit.offbit.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.offbit.offbit.R;
import com.offbit.offbit.network.PeerManager;

public class PeerDiscoveryService extends Service {
    private static final String TAG = "PeerDiscoveryService";
    private static final String CHANNEL_ID = "PeerDiscoveryChannel";
    private static final int NOTIFICATION_ID = 1001;
    
    private PeerManager peerManager;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "PeerDiscoveryService created");
        
        // Create notification channel for foreground service
        createNotificationChannel();
        
        // Start foreground service
        startForeground(NOTIFICATION_ID, createNotification());
        
        // Initialize peer manager
        peerManager = PeerManager.getInstance(this);
        
        // Start peer discovery
        startPeerDiscovery();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "PeerDiscoveryService started");
        return START_STICKY; // Restart service if killed
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null; // This is a started service, not a bound service
    }
    
    @Override
    public void onDestroy() {
        Log.d(TAG, "PeerDiscoveryService destroyed");
        
        // Stop peer discovery
        if (peerManager != null) {
            peerManager.stopPeerDiscovery();
        }
        
        super.onDestroy();
    }
    
    private void startPeerDiscovery() {
        if (peerManager != null) {
            peerManager.startPeerDiscovery();
            
            // For demonstration purposes, simulate discovering a few peers
            simulatePeerDiscovery();
        }
    }
    
    private void simulatePeerDiscovery() {
        // In a real implementation, this would happen through network discovery
        // For now, we'll simulate finding some peers
        
        // Simulate discovering peer 1
        peerManager.simulatePeerDiscovery(
                "peer1", 
                "Alice", 
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
        );
        
        // Simulate discovering peer 2
        peerManager.simulatePeerDiscovery(
                "peer2", 
                "Bob", 
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
        );
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Peer Discovery Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Discovers and connects to nearby peers");
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("OffBit")
                .setContentText("Discovering nearby peers...")
                .setSmallIcon(R.drawable.ic_launcher)
                .build();
    }
}
