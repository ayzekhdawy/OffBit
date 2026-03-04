package com.offbit.offbit.model;

import java.util.Date;

public class CallRecord {
    private String callId;
    private String peerId;
    private String peerName;
    private Date startTime;
    private Date endTime;
    private CallType callType;
    private CallStatus callStatus;
    private long duration; // in seconds
    
    public enum CallType {
        INCOMING,
        OUTGOING
    }
    
    public enum CallStatus {
        COMPLETED,
        MISSED,
        REJECTED,
        FAILED
    }
    
    public CallRecord() {
        this.callId = java.util.UUID.randomUUID().toString();
        this.startTime = new Date();
    }
    
    public CallRecord(String peerId, String peerName, CallType callType) {
        this();
        this.peerId = peerId;
        this.peerName = peerName;
        this.callType = callType;
    }
    
    // Getters and setters
    public String getCallId() {
        return callId;
    }
    
    public void setCallId(String callId) {
        this.callId = callId;
    }
    
    public String getPeerId() {
        return peerId;
    }
    
    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }
    
    public String getPeerName() {
        return peerName;
    }
    
    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }
    
    public Date getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    public Date getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
        if (startTime != null && endTime != null) {
            this.duration = (endTime.getTime() - startTime.getTime()) / 1000;
        }
    }
    
    public CallType getCallType() {
        return callType;
    }
    
    public void setCallType(CallType callType) {
        this.callType = callType;
    }
    
    public CallStatus getCallStatus() {
        return callStatus;
    }
    
    public void setCallStatus(CallStatus callStatus) {
        this.callStatus = callStatus;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public void setDuration(long duration) {
        this.duration = duration;
    }
    
    // Helper methods
    public String getDurationString() {
        if (duration <= 0) {
            return "00:00";
        }
        
        long minutes = duration / 60;
        long seconds = duration % 60;
        
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    public String getTimeString() {
        if (startTime == null) {
            return "";
        }
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        return sdf.format(startTime);
    }
    
    @Override
    public String toString() {
        return "CallRecord{" +
                "callId='" + callId + '\'' +
                ", peerName='" + peerName + '\'' +
                ", callType=" + callType +
                ", callStatus=" + callStatus +
                ", duration=" + duration +
                '}';
    }
}
