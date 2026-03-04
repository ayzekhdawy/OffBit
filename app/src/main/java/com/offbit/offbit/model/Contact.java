package com.offbit.offbit.model;

import java.util.Date;

public class Contact {
    private String contactId;
    private String peerId;
    private String displayName;
    private String publicKeyBase64;
    private Date lastSeen;
    private boolean isFavorite;
    private Date addedDate;
    
    public Contact() {
        this.contactId = java.util.UUID.randomUUID().toString();
        this.addedDate = new Date();
    }
    
    public Contact(String peerId, String displayName, String publicKeyBase64) {
        this();
        this.peerId = peerId;
        this.displayName = displayName;
        this.publicKeyBase64 = publicKeyBase64;
        this.lastSeen = new Date();
    }
    
    // Getters and setters
    public String getContactId() {
        return contactId;
    }
    
    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
    
    public String getPeerId() {
        return peerId;
    }
    
    public void setPeerId(String peerId) {
        this.peerId = peerId;
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
    
    public Date getLastSeen() {
        return lastSeen;
    }
    
    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }
    
    public boolean isFavorite() {
        return isFavorite;
    }
    
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    
    public Date getAddedDate() {
        return addedDate;
    }
    
    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }
    
    // Helper methods
    public void updateLastSeen() {
        this.lastSeen = new Date();
    }
    
    @Override
    public String toString() {
        return "Contact{" +
                "contactId='" + contactId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
