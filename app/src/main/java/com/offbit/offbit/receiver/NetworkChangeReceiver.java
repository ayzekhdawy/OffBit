package com.offbit.offbit.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.offbit.offbit.network.PeerManager;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkChangeReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            
            Log.d(TAG, "Network connectivity changed. Connected: " + isConnected);
            
            // Get peer manager instance
            PeerManager peerManager = PeerManager.getInstance(context);
            
            if (isConnected) {
                // Network is connected, start peer discovery
                Log.d(TAG, "Network connected, starting peer discovery");
                peerManager.startPeerDiscovery();
            } else {
                // Network is disconnected, stop peer discovery
                Log.d(TAG, "Network disconnected, stopping peer discovery");
                peerManager.stopPeerDiscovery();
            }
        }
    }
}
