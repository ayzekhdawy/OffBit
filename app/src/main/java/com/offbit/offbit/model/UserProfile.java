package com.offbit.offbit.model;

import android.util.Base64;
import android.util.Log;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

public class UserProfile {
    private static final String TAG = "UserProfile";
    private String userId;
    private String displayName;
    private String publicKeyBase64;
    private long createdAt;
    private boolean isActive;

    public UserProfile() {
        // Default constructor required for Firebase
        this.userId = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
    }

    public UserProfile(String displayName, String publicKeyBase64) {
        this();
        this.displayName = displayName;
        this.publicKeyBase64 = publicKeyBase64;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPublicKeyBase64() {
        return publicKeyBase64;
    }

    public void setPublicKeyBase64(String publicKeyBase64) {
        this.publicKeyBase64 = publicKeyBase64;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "userId='" + userId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                '}';
    }
}
