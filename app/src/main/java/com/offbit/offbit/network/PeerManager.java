package com.offbit.offbit.network;

import android.content.Context;
import android.util.Log;

import com.offbit.offbit.model.UserProfile;
import com.offbit.offbit.network.PeerConnection.ConnectionState;
import com.offbit.offbit.utils.SecurityUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeerManager {
    private static final String TAG = "PeerManager";
    private static PeerManager instance;
    
    private Context context;
    private UserProfile localUserProfile;
    private Map<String, PeerConnection> connectedPeers;
    private List<PeerDiscoveryListener> discoveryListeners;
    private List<PeerConnectionListener> connectionListeners;
    private ScheduledExecutorService scheduler;
    private UDPDiscovery udpDiscovery;
    private boolean isDiscoveryRunning;
    
    // Interface for peer discovery callbacks
    public interface PeerDiscoveryListener {
        void onPeerDiscovered(PeerConnection peer);
        void onPeerLost(PeerConnection peer);
    }
    
    // Interface for connection state callbacks
    public interface PeerConnectionListener {
        void onPeerConnected(PeerConnection peer);
        void onPeerDisconnected(PeerConnection peer);
        void onConnectionError(PeerConnection peer, String error);
    }
    
    private PeerManager(Context context) {
        this.context = context;
        this.connectedPeers = new ConcurrentHashMap<>();
        this.discoveryListeners = new ArrayList<>();
        this.connectionListeners = new ArrayList<>();
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.udpDiscovery = new UDPDiscovery(context, this);
    }
    
    public static synchronized PeerManager getInstance(Context context) {
        if (instance == null) {
            instance = new PeerManager(context);
        }
        return instance;
    }
    
    public void setUserProfile(UserProfile userProfile) {
        this.localUserProfile = userProfile;
    }
    
    public UserProfile getLocalUserProfile() {
        return localUserProfile;
    }
    
    public void addDiscoveryListener(PeerDiscoveryListener listener) {
        if (!discoveryListeners.contains(listener)) {
            discoveryListeners.add(listener);
        }
    }
    
    public void removeDiscoveryListener(PeerDiscoveryListener listener) {
        discoveryListeners.remove(listener);
    }
    
    public void addConnectionListener(PeerConnectionListener listener) {
        if (!connectionListeners.contains(listener)) {
            connectionListeners.add(listener);
        }
    }
    
    public void removeConnectionListener(PeerConnectionListener listener) {
        connectionListeners.remove(listener);
    }
    
    /**
     * Start peer discovery on local network
     */
    public void startPeerDiscovery() {
        if (isDiscoveryRunning) {
            Log.w(TAG, "Peer discovery already running");
            return;
        }
        
        Log.d(TAG, "Starting peer discovery");
        isDiscoveryRunning = true;
        
        // Start UDP discovery
        udpDiscovery.startDiscovery();
        
        // Schedule periodic broadcasting of our presence
        scheduler.scheduleAtFixedRate(() -> {
            if (localUserProfile != null) {
                udpDiscovery.broadcastPresence(localUserProfile);
            }
        }, 0, 30, TimeUnit.SECONDS); // Broadcast every 30 seconds
        
        // Schedule periodic cleanup of stale peers
        scheduler.scheduleAtFixedRate(this::cleanupStalePeers, 60, 60, TimeUnit.SECONDS);
    }
    
    /**
     * Stop peer discovery
     */
    public void stopPeerDiscovery() {
        Log.d(TAG, "Stopping peer discovery");
        isDiscoveryRunning = false;
        
        // Stop UDP discovery
        udpDiscovery.stopDiscovery();
        
        // Shutdown scheduler
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
    
    /**
     * Connect to a peer
     */
    public void connectToPeer(PeerConnection peer) {
        Log.d(TAG, "Connecting to peer: " + peer.getDisplayName());
        
        // Update peer state
        peer.setConnectionState(ConnectionState.CONNECTING);
        connectedPeers.put(peer.getPeerId(), peer);
        
        // Notify listeners
        for (PeerConnectionListener listener : connectionListeners) {
            listener.onPeerConnected(peer);
        }
        
        // In a real implementation, this would:
        // 1. Establish a network connection to the peer
        // 2. Perform cryptographic handshake
        // 3. Authenticate the peer
        // 4. Set up secure communication channel
        
        // For simulation, we'll just mark as connected after a delay
        scheduler.schedule(() -> {
            peer.setConnectionState(ConnectionState.CONNECTED);
            Log.d(TAG, "Connected to peer: " + peer.getDisplayName());
        }, 2, TimeUnit.SECONDS);
    }
    
    /**
     * Disconnect from a peer
     */
    public void disconnectFromPeer(PeerConnection peer) {
        Log.d(TAG, "Disconnecting from peer: " + peer.getDisplayName());
        
        peer.setConnectionState(ConnectionState.DISCONNECTED);
        connectedPeers.remove(peer.getPeerId());
        
        // Notify listeners
        for (PeerConnectionListener listener : connectionListeners) {
            listener.onPeerDisconnected(peer);
        }
    }
    
    /**
     * Get list of connected peers
     */
    public List<PeerConnection> getConnectedPeers() {
        return new ArrayList<>(connectedPeers.values());
    }
    
    /**
     * Find a peer by ID
     */
    public PeerConnection getPeerById(String peerId) {
        return connectedPeers.get(peerId);
    }
    
    /**
     * Handle discovered peer from UDP discovery
     */
    public void onPeerDiscovered(PeerConnection peer) {
        Log.d(TAG, "Peer discovered: " + peer.getDisplayName());
        
        // Check if we already know about this peer
        if (connectedPeers.containsKey(peer.getPeerId())) {
            // Update existing peer info
            PeerConnection existingPeer = connectedPeers.get(peer.getPeerId());
            existingPeer.setDisplayName(peer.getDisplayName());
            existingPeer.setIpAddress(peer.getIpAddress());
            existingPeer.setPort(peer.getPort());
            existingPeer.updateLastSeen();
        } else {
            // Add new peer
            connectedPeers.put(peer.getPeerId(), peer);
            
            // Notify discovery listeners
            for (PeerDiscoveryListener listener : discoveryListeners) {
                listener.onPeerDiscovered(peer);
            }
        }
    }
    
    /**
     * Cleanup stale peers (peers we haven't seen in a while)
     */
    private void cleanupStalePeers() {
        long currentTime = System.currentTimeMillis();
        long staleThreshold = 5 * 60 * 1000; // 5 minutes
        
        List<String> stalePeers = new ArrayList<>();
        
        for (Map.Entry<String, PeerConnection> entry : connectedPeers.entrySet()) {
            PeerConnection peer = entry.getValue();
            if (currentTime - peer.getLastSeen() > staleThreshold) {
                stalePeers.add(entry.getKey());
                
                // Notify listeners that peer is lost
                for (PeerDiscoveryListener listener : discoveryListeners) {
                    listener.onPeerLost(peer);
                }
            }
        }
        
        // Remove stale peers
        for (String peerId : stalePeers) {
            connectedPeers.remove(peerId);
        }
        
        if (!stalePeers.isEmpty()) {
            Log.d(TAG, "Cleaned up " + stalePeers.size() + " stale peers");
        }
    }
}
