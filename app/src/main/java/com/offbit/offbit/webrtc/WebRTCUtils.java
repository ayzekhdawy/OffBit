package com.offbit.offbit.webrtc;

import org.webrtc.AudioDecoderFactory;
import org.webrtc.AudioEncoderFactory;
import org.webrtc.AudioProcessingFactory;
import org.webrtc.DefaultAudioDecoderFactory;
import org.webrtc.DefaultAudioEncoderFactory;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;

import java.util.ArrayList;
import java.util.List;

public class WebRTCUtils {
    
    /**
     * Get RTC configuration with STUN servers
     */
    public static PeerConnection.RTCConfiguration getRTCConfiguration() {
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        
        // Google STUN server
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        
        // Mozilla STUN server
        iceServers.add(new PeerConnection.IceServer("stun:stun.services.mozilla.com"));
        
        // Additional STUN servers
        iceServers.add(new PeerConnection.IceServer("stun:stun.stunprotocol.org:3478"));
        
        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.ENABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        
        return rtcConfig;
    }
    
    /**
     * Get peer connection constraints
     */
    public static MediaConstraints getPeerConnectionConstraints() {
        MediaConstraints constraints = new MediaConstraints();
        constraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        return constraints;
    }
    
    /**
     * Get audio encoder factory
     */
    public static AudioEncoderFactory getAudioEncoderFactory() {
        return DefaultAudioEncoderFactory.create();
    }
    
    /**
     * Get audio decoder factory
     */
    public static AudioDecoderFactory getAudioDecoderFactory() {
        return DefaultAudioDecoderFactory.create();
    }
    
    /**
     * Get video encoder factory
     */
    public static VideoEncoderFactory getVideoEncoderFactory(EglBase eglBase) {
        return new DefaultVideoEncoderFactory(eglBase.getEglBaseContext(), true, true);
    }
    
    /**
     * Get video decoder factory
     */
    public static VideoDecoderFactory getVideoDecoderFactory(EglBase eglBase) {
        return new DefaultVideoDecoderFactory(eglBase.getEglBaseContext());
    }
}
