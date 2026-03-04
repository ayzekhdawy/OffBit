package com.offbit.offbit.network;

import android.content.Context;
import android.util.Log;

import com.offbit.offbit.model.UserProfile;
import com.offbit.offbit.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPDiscovery {
    private static final String TAG = "UDPDiscovery";
    private static final int PORT = 54321;
    private static final int BUFFER_SIZE = 1024;
    
    private Context context;
    private PeerManager peerManager;
    private DatagramSocket socket;
    private ExecutorService executorService;
    private boolean isRunning;
    
    public UDPDiscovery(Context context, PeerManager peerManager) {
        this.context = context;
        this.peerManager = peerManager;
        this.executorService = Executors.newSingleThreadExecutor();
    }
    
    /**
     * Start UDP discovery service
     */
    public void startDiscovery() {
        if (isRunning) {
            Log.w(TAG, "Discovery already running");
            return;
        }
        
        isRunning = true;
        executorService.submit(this::runDiscovery);
        Log.d(TAG, "UDP discovery started");
    }
    
    /**
     * Stop UDP discovery service
     */
    public void stopDiscovery() {
        isRunning = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        Log.d(TAG, "UDP discovery stopped");
    }
    
    /**
     * Main discovery loop
     */
    private void runDiscovery() {
        try {
            // Create UDP socket
            socket = new DatagramSocket(PORT);
            socket.setBroadcast(true);
            
            // Start listening for broadcasts
            listenForBroadcasts();
            
        } catch (SocketException e) {
            Log.e(TAG, "Error creating UDP socket", e);
        }
    }
    
    /**
     * Listen for incoming broadcast packets
     */
    private void listenForBroadcasts() {
        byte[] buffer = new byte[BUFFER_SIZE];
        
        while (isRunning) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                // Process received packet
                processReceivedPacket(packet);
                
            } catch (IOException e) {
                if (isRunning) {
                    Log.e(TAG, "Error receiving packet", e);
                }
                break;
            }
        }
    }
    
    /**
     * Process received packet
     */
    private void processReceivedPacket(DatagramPacket packet) {
        try {
            String message = new String(packet.getData(), 0, packet.getLength());
            JSONObject json = new JSONObject(message);
            
            String type = json.getString("type");
            String peerId = json.getString("peerId");
            String displayName = json.getString("displayName");
            String publicKey = json.getString("publicKey");
            
            if ("discovery".equals(type)) {
                Log.d(TAG, "Discovered peer: " + displayName);
                
                // Create peer connection object
                PeerConnection peer = new PeerConnection(peerId, displayName, publicKey);
                peer.setIpAddress(packet.getAddress().getHostAddress());
                peer.setPort(packet.getPort());
                
                // Notify peer manager
                // In a real implementation, peerManager would have a method to handle discovered peers
                // peerManager.onPeerDiscovered(peer);
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing discovery packet", e);
        }
    }
    
    /**
     * Broadcast our presence on the network
     */
    public void broadcastPresence(UserProfile userProfile) {
        executorService.submit(() -> {
            try {
                // Create discovery message
                JSONObject message = new JSONObject();
                message.put("type", "discovery");
                message.put("peerId", userProfile.getUserId());
                message.put("displayName", userProfile.getDisplayName());
                message.put("publicKey", userProfile.getPublicKeyBase64());
                
                String messageStr = message.toString();
                byte[] buffer = messageStr.getBytes();
                
                // Broadcast on all network interfaces
                broadcastMessage(buffer);
                
            } catch (JSONException e) {
                Log.e(TAG, "Error creating discovery message", e);
            }
        });
    }
    
    /**
     * Broadcast message on all network interfaces
     */
    private void broadcastMessage(byte[] buffer) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }
                    
                    // Send broadcast packet
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcast, PORT);
                    if (socket != null && !socket.isClosed()) {
                        socket.send(packet);
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, "Error getting network interfaces", e);
        } catch (IOException e) {
            Log.e(TAG, "Error sending broadcast packet", e);
        }
    }
}
