package com.offbit.offbit.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;

public class SignalingManager {
    private static final String TAG = "SignalingManager";
    
    // Signaling message types
    public static final String TYPE_OFFER = "offer";
    public static final String TYPE_ANSWER = "answer";
    public static final String TYPE_ICE_CANDIDATE = "ice_candidate";
    public static final String TYPE_PEER_INFO = "peer_info";
    public static final String TYPE_CALL_REQUEST = "call_request";
    public static final String TYPE_CALL_ACCEPT = "call_accept";
    public static final String TYPE_CALL_REJECT = "call_reject";
    public static final String TYPE_CALL_END = "call_end";
    
    // Interface for signaling callbacks
    public interface SignalingListener {
        void onOfferReceived(String fromPeerId, JSONObject offer);
        void onAnswerReceived(String fromPeerId, JSONObject answer);
        void onIceCandidateReceived(String fromPeerId, JSONObject candidate);
        void onPeerInfoReceived(String fromPeerId, JSONObject peerInfo);
        void onCallRequestReceived(String fromPeerId, JSONObject callRequest);
        void onCallAcceptReceived(String fromPeerId, JSONObject callAccept);
        void onCallRejectReceived(String fromPeerId, JSONObject callReject);
        void onCallEndReceived(String fromPeerId, JSONObject callEnd);
        void onSignalingError(String error);
    }
    
    private SignalingListener signalingListener;
    
    public SignalingManager(SignalingListener listener) {
        this.signalingListener = listener;
    }
    
    /**
     * Send signaling message to a peer
     */
    public void sendSignalingMessage(String toPeerId, String type, JSONObject data) {
        Log.d(TAG, "Sending signaling message to " + toPeerId + " type: " + type);
        
        try {
            JSONObject message = new JSONObject();
            message.put("type", type);
            message.put("from", "local_peer_id"); // Would be replaced with actual peer ID
            message.put("data", data);
            
            // In a real implementation, this would send the message through
            // a signaling server or direct connection
            
            // For simulation, we'll just log it
            Log.d(TAG, "Message sent: " + message.toString());
            
        } catch (JSONException e) {
            Log.e(TAG, "Error creating signaling message", e);
            if (signalingListener != null) {
                signalingListener.onSignalingError("Error creating signaling message: " + e.getMessage());
            }
        }
    }
    
    /**
     * Process incoming signaling message
     */
    public void processSignalingMessage(JSONObject message) {
        try {
            String type = message.getString("type");
            String fromPeerId = message.getString("from");
            JSONObject data = message.getJSONObject("data");
            
            Log.d(TAG, "Processing signaling message from " + fromPeerId + " type: " + type);
            
            if (signalingListener == null) {
                Log.w(TAG, "No signaling listener registered");
                return;
            }
            
            switch (type) {
                case TYPE_OFFER:
                    signalingListener.onOfferReceived(fromPeerId, data);
                    break;
                case TYPE_ANSWER:
                    signalingListener.onAnswerReceived(fromPeerId, data);
                    break;
                case TYPE_ICE_CANDIDATE:
                    signalingListener.onIceCandidateReceived(fromPeerId, data);
                    break;
                case TYPE_PEER_INFO:
                    signalingListener.onPeerInfoReceived(fromPeerId, data);
                    break;
                case TYPE_CALL_REQUEST:
                    signalingListener.onCallRequestReceived(fromPeerId, data);
                    break;
                case TYPE_CALL_ACCEPT:
                    signalingListener.onCallAcceptReceived(fromPeerId, data);
                    break;
                case TYPE_CALL_REJECT:
                    signalingListener.onCallRejectReceived(fromPeerId, data);
                    break;
                case TYPE_CALL_END:
                    signalingListener.onCallEndReceived(fromPeerId, data);
                    break;
                default:
                    Log.w(TAG, "Unknown signaling message type: " + type);
                    signalingListener.onSignalingError("Unknown signaling message type: " + type);
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "Error processing signaling message", e);
            if (signalingListener != null) {
                signalingListener.onSignalingError("Error processing signaling message: " + e.getMessage());
            }
        }
    }
    
    /**
     * Send peer information to another peer
     */
    public void sendPeerInfo(String toPeerId, String displayName, String publicKeyBase64) {
        try {
            JSONObject peerInfo = new JSONObject();
            peerInfo.put("displayName", displayName);
            peerInfo.put("publicKey", publicKeyBase64);
            
            sendSignalingMessage(toPeerId, TYPE_PEER_INFO, peerInfo);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating peer info message", e);
            if (signalingListener != null) {
                signalingListener.onSignalingError("Error creating peer info message: " + e.getMessage());
            }
        }
    }
    
    /**
     * Send call request to a peer
     */
    public void sendCallRequest(String toPeerId, String callerName) {
        try {
            JSONObject callRequest = new JSONObject();
            callRequest.put("callerName", callerName);
            callRequest.put("timestamp", System.currentTimeMillis());
            
            sendSignalingMessage(toPeerId, TYPE_CALL_REQUEST, callRequest);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating call request message", e);
            if (signalingListener != null) {
                signalingListener.onSignalingError("Error creating call request message: " + e.getMessage());
            }
        }
    }
    
    /**
     * Send call accept to a peer
     */
    public void sendCallAccept(String toPeerId) {
        try {
            JSONObject callAccept = new JSONObject();
            callAccept.put("timestamp", System.currentTimeMillis());
            
            sendSignalingMessage(toPeerId, TYPE_CALL_ACCEPT, callAccept);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating call accept message", e);
            if (signalingListener != null) {
                signalingListener.onSignalingError("Error creating call accept message: " + e.getMessage());
            }
        }
    }
    
    /**
     * Send call reject to a peer
     */
    public void sendCallReject(String toPeerId, String reason) {
        try {
            JSONObject callReject = new JSONObject();
            callReject.put("reason", reason);
            callReject.put("timestamp", System.currentTimeMillis());
            
            sendSignalingMessage(toPeerId, TYPE_CALL_REJECT, callReject);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating call reject message", e);
            if (signalingListener != null) {
                signalingListener.onSignalingError("Error creating call reject message: " + e.getMessage());
            }
        }
    }
}
