package com.offbit.offbit.manager;

import android.content.Context;
import android.util.Log;

import com.offbit.offbit.database.DatabaseHelper;
import com.offbit.offbit.model.CallRecord;
import com.offbit.offbit.model.Contact;

import java.util.List;

public class CallHistoryManager {
    private static final String TAG = "CallHistoryManager";
    private static CallHistoryManager instance;
    
    private Context context;
    private DatabaseHelper databaseHelper;
    
    private CallHistoryManager(Context context) {
        this.context = context;
        this.databaseHelper = new DatabaseHelper(context);
    }
    
    public static synchronized CallHistoryManager getInstance(Context context) {
        if (instance == null) {
            instance = new CallHistoryManager(context);
        }
        return instance;
    }
    
    /**
     * Add a new call record
     */
    public void addCallRecord(CallRecord callRecord) {
        try {
            databaseHelper.addCallRecord(callRecord);
            Log.d(TAG, "Call record added successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error adding call record", e);
        }
    }
    
    /**
     * Get all call records
     */
    public List<CallRecord> getAllCallRecords() {
        try {
            return databaseHelper.getAllCallRecords();
        } catch (Exception e) {
            Log.e(TAG, "Error getting call records", e);
            return null;
        }
    }
    
    /**
     * Get recent call records
     */
    public List<CallRecord> getRecentCallRecords(int limit) {
        try {
            return databaseHelper.getRecentCallRecords(limit);
        } catch (Exception e) {
            Log.e(TAG, "Error getting recent call records", e);
            return null;
        }
    }
    
    /**
     * Add a new contact
     */
    public void addContact(Contact contact) {
        try {
            databaseHelper.addContact(contact);
            Log.d(TAG, "Contact added successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error adding contact", e);
        }
    }
    
    /**
     * Get all contacts
     */
    public List<Contact> getAllContacts() {
        try {
            return databaseHelper.getAllContacts();
        } catch (Exception e) {
            Log.e(TAG, "Error getting contacts", e);
            return null;
        }
    }
    
    /**
     * Get favorite contacts
     */
    public List<Contact> getFavoriteContacts() {
        try {
            return databaseHelper.getFavoriteContacts();
        } catch (Exception e) {
            Log.e(TAG, "Error getting favorite contacts", e);
            return null;
        }
    }
    
    /**
     * Update contact favorite status
     */
    public void updateContactFavorite(String contactId, boolean isFavorite) {
        try {
            databaseHelper.updateContactFavorite(contactId, isFavorite);
            Log.d(TAG, "Contact favorite status updated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error updating contact favorite status", e);
        }
    }
    
    /**
     * Delete a contact
     */
    public void deleteContact(String contactId) {
        try {
            databaseHelper.deleteContact(contactId);
            Log.d(TAG, "Contact deleted successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error deleting contact", e);
        }
    }
}
