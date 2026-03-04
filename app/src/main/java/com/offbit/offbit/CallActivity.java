package com.offbit.offbit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.offbit.offbit.webrtc.WebRTCManager;

import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.SessionDescription;

public class CallActivity extends AppCompatActivity implements WebRTCManager.WebRTCCallback {
    private static final String TAG = "CallActivity";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    
    private WebRTCManager webRTCManager;
    private TextView callStatusText;
    private TextView peerNameText;
    private Button endCallButton;
    private Button muteButton;
    private Button speakerButton;
    
    private String peerId;
    private String peerName;
    private boolean isMuted = false;
    private boolean isSpeakerOn = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        
        initViews();
        setupClickListeners();
        
        // Get peer information from intent
        Intent intent = getIntent();
        peerId = intent.getStringExtra("peerId");
        peerName = intent.getStringExtra("peerName");
        boolean isIncomingCall = intent.getBooleanExtra("isIncomingCall", false);
        String remoteSdp = intent.getStringExtra("remoteSdp");
        
        if (peerName != null) {
            peerNameText.setText(peerName);
        }
        
        // Initialize WebRTC
        webRTCManager = WebRTCManager.getInstance(this);
        webRTCManager.setCallback(this);
        
        // Check permissions
        if (checkPermissions()) {
            if (isIncomingCall && remoteSdp != null) {
                // Handle incoming call
                handleIncomingCall(remoteSdp);
            } else {
                // Start outgoing call
                startOutgoingCall();
            }
        } else {
            requestPermissions();
        }
    }
    
    private void initViews() {
        callStatusText = findViewById(R.id.callStatusText);
        peerNameText = findViewById(R.id.peerNameText);
        endCallButton = findViewById(R.id.endCallButton);
        muteButton = findViewById(R.id.muteButton);
        speakerButton = findViewById(R.id.speakerButton);
    }
    
    private void setupClickListeners() {
        endCallButton.setOnClickListener(v -> endCall());
        muteButton.setOnClickListener(v -> toggleMute());
        speakerButton.setOnClickListener(v -> toggleSpeaker());
    }
    
    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
                == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS)
                == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS
                },
                PERMISSION_REQUEST_CODE);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && 
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, start call
                Intent intent = getIntent();
                boolean isIncomingCall = intent.getBooleanExtra("isIncomingCall", false);
                String remoteSdp = intent.getStringExtra("remoteSdp");
                
                if (isIncomingCall && remoteSdp != null) {
                    handleIncomingCall(remoteSdp);
                } else {
                    startOutgoingCall();
                }
            } else {
                // Permissions denied
                Toast.makeText(this, "Permissions required for calling", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    private void startOutgoingCall() {
        callStatusText.setText("Calling...");
        if (webRTCManager != null) {
            webRTCManager.startOutgoingCall(peerId);
        }
    }
    
    private void handleIncomingCall(String remoteSdp) {
        callStatusText.setText("Incoming call...");
        if (webRTCManager != null) {
            // In a real implementation, we would parse the SDP string into a SessionDescription object
            // For now, we'll just simulate the process
            Toast.makeText(this, "Incoming call from " + peerName, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void endCall() {
        if (webRTCManager != null) {
            webRTCManager.endCall();
        }
        finish();
    }
    
    private void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            muteButton.setText("Unmute");
        } else {
            muteButton.setText("Mute");
        }
        // In a real implementation, this would control the audio track
        Toast.makeText(this, isMuted ? "Microphone muted" : "Microphone unmuted", 
                      Toast.LENGTH_SHORT).show();
    }
    
    private void toggleSpeaker() {
        isSpeakerOn = !isSpeakerOn;
        if (isSpeakerOn) {
            speakerButton.setText("Speaker Off");
        } else {
            speakerButton.setText("Speaker On");
        }
        // In a real implementation, this would control the audio routing
        Toast.makeText(this, isSpeakerOn ? "Speaker on" : "Speaker off", 
                      Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onCallStarted() {
        runOnUiThread(() -> {
            callStatusText.setText("Call connected");
            Toast.makeText(CallActivity.this, "Call started", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public void onCallEnded() {
        runOnUiThread(() -> {
            callStatusText.setText("Call ended");
            Toast.makeText(CallActivity.this, "Call ended", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
    
    @Override
    public void onCallError(String error) {
        runOnUiThread(() -> {
            callStatusText.setText("Call error");
            Toast.makeText(CallActivity.this, "Call error: " + error, Toast.LENGTH_LONG).show();
            finish();
        });
    }
    
    @Override
    public void onRemoteStreamAdded(MediaStream stream) {
        runOnUiThread(() -> {
            callStatusText.setText("Connected");
            Toast.makeText(CallActivity.this, "Remote stream added", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public void onIceCandidate(IceCandidate candidate) {
        // In a real implementation, this would be sent to the remote peer via signaling
        Log.d(TAG, "ICE candidate received: " + candidate.sdp);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webRTCManager != null) {
            webRTCManager.endCall();
        }
    }
}
