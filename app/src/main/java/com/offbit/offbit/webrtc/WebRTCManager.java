package com.offbit.offbit.webrtc;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.RtpParameters;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpSender;
import org.webrtc.RtpTransceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

public class WebRTCManager {
    private static final String TAG = "WebRTCManager";
    private static WebRTCManager instance;
    
    // WebRTC components
    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnection localPeer;
    private MediaStream mediaStream;
    private AudioTrack localAudioTrack;
    private AudioSource audioSource;
    
    // Context and EGL base
    private Context context;
    private EglBase eglBase;
    
    // Call state
    private boolean isInCall = false;
    private String currentPeerId;
    
    // Callback interface
    public interface WebRTCCallback {
        void onCallStarted();
        void onCallEnded();
        void onCallError(String error);
        void onRemoteStreamAdded(MediaStream stream);
        void onIceCandidate(IceCandidate candidate);
    }
    
    private WebRTCCallback callback;
    
    private WebRTCManager(Context context) {
        this.context = context;
        this.eglBase = EglBase.create();
        initWebRTC();
    }
    
    public static synchronized WebRTCManager getInstance(Context context) {
        if (instance == null) {
            instance = new WebRTCManager(context);
        }
        return instance;
    }
    
    public void setCallback(WebRTCCallback callback) {
        this.callback = callback;
    }
    
    /**
     * Initialize WebRTC components
     */
    private void initWebRTC() {
        Log.d(TAG, "Initializing WebRTC");
        
        // Initialize PeerConnectionFactory
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(context)
                        .setEnableInternalTracer(true)
                        .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
                        .createInitializationOptions();
        
        PeerConnectionFactory.initialize(initializationOptions);
        
        // Create PeerConnectionFactory
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        DefaultVideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(
                eglBase.getEglBaseContext(), true, true);
        DefaultVideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(eglBase.getEglBaseContext());
        
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setAudioDecoderFactory(WebRTCUtils.getAudioDecoderFactory())
                .setAudioEncoderFactory(WebRTCUtils.getAudioEncoderFactory())
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .setPeerConnectionConstraints(WebRTCUtils.getPeerConnectionConstraints())
                .createPeerConnectionFactory();
        
        Log.d(TAG, "WebRTC initialized successfully");
    }
    
    /**
     * Start an outgoing call
     */
    public void startOutgoingCall(String peerId) {
        Log.d(TAG, "Starting outgoing call to: " + peerId);
        
        if (isInCall) {
            Log.w(TAG, "Already in a call");
            if (callback != null) {
                callback.onCallError("Already in a call");
            }
            return;
        }
        
        this.currentPeerId = peerId;
        this.isInCall = true;
        
        // Create peer connection
        createPeerConnection();
        
        // Create media stream
        createMediaStream();
        
        // Add local stream to peer connection
        if (localPeer != null && mediaStream != null) {
            for (AudioTrack audioTrack : mediaStream.audioTracks) {
                localPeer.addTrack(audioTrack);
            }
        }
        
        // Create offer
        createOffer();
        
        if (callback != null) {
            callback.onCallStarted();
        }
    }
    
    /**
     * Incoming call handler
     */
    public void handleIncomingCall(String peerId, SessionDescription remoteSdp) {
        Log.d(TAG, "Handling incoming call from: " + peerId);
        
        if (isInCall) {
            Log.w(TAG, "Already in a call");
            if (callback != null) {
                callback.onCallError("Already in a call");
            }
            return;
        }
        
        this.currentPeerId = peerId;
        this.isInCall = true;
        
        // Create peer connection
        createPeerConnection();
        
        // Create media stream
        createMediaStream();
        
        // Add local stream to peer connection
        if (localPeer != null && mediaStream != null) {
            for (AudioTrack audioTrack : mediaStream.audioTracks) {
                localPeer.addTrack(audioTrack);
            }
        }
        
        // Set remote description
        if (localPeer != null) {
            localPeer.setRemoteDescription(new SdpObserver() {
                @Override
                public void onCreateSuccess(SessionDescription sessionDescription) {}
                
                @Override
                public void onSetSuccess() {
                    Log.d(TAG, "Remote description set successfully");
                    // Create answer
                    createAnswer();
                }
                
                @Override
                public void onCreateFailure(String s) {}
                
                @Override
                public void onSetFailure(String s) {
                    Log.e(TAG, "Failed to set remote description: " + s);
                    if (callback != null) {
                        callback.onCallError("Failed to set remote description: " + s);
                    }
                }
            }, remoteSdp);
        }
        
        if (callback != null) {
            callback.onCallStarted();
        }
    }
    
    /**
     * Create peer connection
     */
    private void createPeerConnection() {
        Log.d(TAG, "Creating peer connection");
        
        PeerConnection.RTCConfiguration rtcConfig = WebRTCUtils.getRTCConfiguration();
        localPeer = peerConnectionFactory.createPeerConnection(rtcConfig, new PeerConnectionAdapter() {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                Log.d(TAG, "New ICE candidate");
                if (callback != null) {
                    callback.onIceCandidate(iceCandidate);
                }
            }
            
            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                Log.d(TAG, "Remote stream added");
                if (callback != null) {
                    callback.onRemoteStreamAdded(mediaStream);
                }
            }
        });
    }
    
    /**
     * Create media stream with audio track
     */
    private void createMediaStream() {
        Log.d(TAG, "Creating media stream");
        
        // Create audio source and track
        audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        localAudioTrack = peerConnectionFactory.createAudioTrack("ARDAMSa0", audioSource);
        localAudioTrack.setEnabled(true);
        
        // Create media stream
        mediaStream = peerConnectionFactory.createLocalMediaStream("ARDAMS");
        mediaStream.addTrack(localAudioTrack);
    }
    
    /**
     * Create SDP offer
     */
    private void createOffer() {
        Log.d(TAG, "Creating SDP offer");
        
        if (localPeer == null) {
            Log.e(TAG, "Peer connection is null");
            return;
        }
        
        MediaConstraints constraints = new MediaConstraints();
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"));
        
        localPeer.createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "Offer created successfully");
                localPeer.setLocalDescription(new SdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {}
                    
                    @Override
                    public void onSetSuccess() {
                        Log.d(TAG, "Local description set successfully");
                        // Send offer to remote peer through signaling
                        // In a real implementation, this would use the SignalingManager
                        Log.d(TAG, "Send offer: " + sessionDescription.description);
                    }
                    
                    @Override
                    public void onCreateFailure(String s) {}
                    
                    @Override
                    public void onSetFailure(String s) {
                        Log.e(TAG, "Failed to set local description: " + s);
                    }
                }, sessionDescription);
            }
            
            @Override
            public void onSetSuccess() {}
            
            @Override
            public void onCreateFailure(String s) {
                Log.e(TAG, "Failed to create offer: " + s);
                if (callback != null) {
                    callback.onCallError("Failed to create offer: " + s);
                }
            }
            
            @Override
            public void onSetFailure(String s) {}
        }, constraints);
    }
    
    /**
     * Create SDP answer
     */
    private void createAnswer() {
        Log.d(TAG, "Creating SDP answer");
        
        if (localPeer == null) {
            Log.e(TAG, "Peer connection is null");
            return;
        }
        
        MediaConstraints constraints = new MediaConstraints();
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"));
        
        localPeer.createAnswer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "Answer created successfully");
                localPeer.setLocalDescription(new SdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {}
                    
                    @Override
                    public void onSetSuccess() {
                        Log.d(TAG, "Local description set successfully");
                        // Send answer to remote peer through signaling
                        // In a real implementation, this would use the SignalingManager
                        Log.d(TAG, "Send answer: " + sessionDescription.description);
                    }
                    
                    @Override
                    public void onCreateFailure(String s) {}
                    
                    @Override
                    public void onSetFailure(String s) {
                        Log.e(TAG, "Failed to set local description: " + s);
                    }
                }, sessionDescription);
            }
            
            @Override
            public void onSetSuccess() {}
            
            @Override
            public void onCreateFailure(String s) {
                Log.e(TAG, "Failed to create answer: " + s);
                if (callback != null) {
                    callback.onCallError("Failed to create answer: " + s);
                }
            }
            
            @Override
            public void onSetFailure(String s) {}
        }, constraints);
    }
    
    /**
     * Add ICE candidate
     */
    public void addIceCandidate(IceCandidate candidate) {
        if (localPeer != null) {
            localPeer.addIceCandidate(candidate);
            Log.d(TAG, "ICE candidate added");
        }
    }
    
    /**
     * End current call
     */
    public void endCall() {
        Log.d(TAG, "Ending call");
        
        if (localPeer != null) {
            localPeer.close();
            localPeer = null;
        }
        
        if (audioSource != null) {
            audioSource.dispose();
            audioSource = null;
        }
        
        if (localAudioTrack != null) {
            localAudioTrack.dispose();
            localAudioTrack = null;
        }
        
        mediaStream = null;
        isInCall = false;
        currentPeerId = null;
        
        if (callback != null) {
            callback.onCallEnded();
        }
    }
    
    /**
     * Check if currently in a call
     */
    public boolean isInCall() {
        return isInCall;
    }
    
    /**
     * Dispose WebRTC resources
     */
    public void dispose() {
        endCall();
        
        if (peerConnectionFactory != null) {
            peerConnectionFactory.dispose();
            peerConnectionFactory = null;
        }
        
        if (eglBase != null) {
            eglBase.release();
            eglBase = null;
        }
        
        instance = null;
    }
    
    /**
     * Abstract adapter class for PeerConnection.Observer
     */
    private abstract class PeerConnectionAdapter implements PeerConnection.Observer {
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {}
        
        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {}
        
        @Override
        public void onIceConnectionReceivingChange(boolean b) {}
        
        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {}
        
        @@Override
        public void onIceCandidate(IceCandidate iceCandidate) {}
        
        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {}
        
        @Override
        public void onAddStream(MediaStream mediaStream) {}
        
        @Override
        public void onRemoveStream(MediaStream mediaStream) {}
        
        @Override
        public void onDataChannel(DataChannel dataChannel) {}
        
        @Override
        public void onRenegotiationNeeded() {}
        
        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {}
    }
}
